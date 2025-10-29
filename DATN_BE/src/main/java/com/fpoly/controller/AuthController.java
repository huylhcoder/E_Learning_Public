package com.fpoly.controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.text.ParseException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.entity.MailInfo;
import com.fpoly.entity.Role;
import com.fpoly.entity.User;
import com.fpoly.exceptions.PermissionDenyException;
import com.fpoly.repository.RoleRepository;
import com.fpoly.repository.UserRepository;
import com.fpoly.dto.IntrospectResponse;
import com.fpoly.dto.ApiResponse;
import com.fpoly.dto.ChangePasswordDTO;
import com.fpoly.dto.GoogleLoginRequest;
import com.fpoly.dto.IntrospectRequest;
import com.fpoly.dto.UserLoginDTO;
import com.fpoly.dto.UserRegisterDTO;

import com.fpoly.response.RegisterResponse;

import com.fpoly.security.JwtTokenUtils;
import com.fpoly.security.LocalizationUtils;
import com.fpoly.security.MessageKeys;
import com.fpoly.security.RefreshTokenService;
import com.fpoly.service.AuthenticationService;
import com.fpoly.service.EmailService;
import com.fpoly.service.RoleService;
import com.fpoly.service.UserService;
import com.fpoly.service.VerificationService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nimbusds.jose.JOSEException;
import com.fpoly.controller.OtpController.DateTimeUtils;

@CrossOrigin("*")
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {

	private static final String SUCCESS = "SUCCESS";
	private static final String USER_CREATED_SUCCESS = "User created successfully";

	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	AuthenticationService authenticationService;
	@Autowired
	RoleService roleService;
	@Autowired
	EmailService emailService;
	@Autowired
	private VerificationService verificationService;
	@Autowired
	RefreshTokenService refreshTokenService;

	@Autowired
	LocalizationUtils localizationUtils;
	@Autowired
	JwtTokenUtils jwtTokenUtils;

//Register Page 
	@PostMapping("/check-user")
	public ResponseEntity<?> checkUser(@RequestBody UserRegisterDTO userRegisterDTO) {
		if (userService.existsByEmail(userRegisterDTO.getEmail())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"Email đã được sử dụng!\"}");
		}
		if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getRetypePassword())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"message\": \"Mật khẩu không khớp!\"}");
		}
		return ResponseEntity.status(HttpStatus.OK).body(userService.existsByEmail(userRegisterDTO.getEmail()));
	}

	// Gửi mã OTP
	@PostMapping("/send-verification-code")
	public ResponseEntity<String> sendVerificationCode(@RequestBody String email) {
		String code = String.format("%06d", new Random().nextInt(999999));

		// Lưu vào Redis
		verificationService.saveOTP(email, code);

		// Gửi email
		String htmlContent = "<h3>Mã xác minh đăng ký tài khoản:</h3><h2>" + code + "</h2>";
		emailService.sendOTP(new MailInfo(email, "Mã xác minh E-Learning", htmlContent));

		return ResponseEntity.ok("Mã xác minh đã được gửi đến email của bạn");
	}

	// Kiểm tra OTP
	@PostMapping("/verify-code")
	public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> payload) {
		String email = payload.get("email");
		String code = payload.get("code");

		if (verificationService.verifyOTP(email, code)) {
			return ResponseEntity.ok("Xác thực thành công");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã xác thực không đúng hoặc đã hết hạn");
	}

	// Đăng ký
	@PostMapping("/register")
	public ResponseEntity<?> createUser(@RequestBody UserRegisterDTO userDTO) throws Exception {
		if (!verificationService.verifyOTP(userDTO.getEmail(), userDTO.getOtp())) {
			return ResponseEntity.badRequest().body("OTP không hợp lệ");
		}
		verificationService.clearOTP(userDTO.getEmail()); // Xóa mã OTP sau khi dùng
		User user = userService.createUser(userDTO);
		return ResponseEntity.ok("Đăng ký thành công");
	}

//Login Page
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
		try {
			String accessToken = userService.login(userLoginDTO.getEmail(), userLoginDTO.getPassword());
			String email = jwtTokenUtils.extractEmail(accessToken);
			User user = userService.getUserByEmailToan(email);

			Map<String, Object> response = new HashMap<>();
			response.put("token", accessToken);
			response.put("avatar", user.getUrlProfileImage());
			response.put("name", user.getName());
			response.put("role", user.getRole().getName());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	// Kiểm tra tính hợp lệ của token
	// Nhận một request trong đó chứa token
	// Trả về hợp lệ của token về hạn hay giả
	// Scope là danh sách quyền
	@PostMapping("/introspect")
	public ResponseEntity<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request) {
		String token = request.getToken();
		System.out.println("Introspect Token:" + token);
		boolean isValid = false;
		String email = null;
		User userDetails = new User();
		try {
			email = jwtTokenUtils.extractEmail(token);
			userDetails = userService.getUserByEmailToan(email);
			System.out.println(userDetails.getRole().getName());
			isValid = jwtTokenUtils.validateToken(token, userDetails);
		} catch (Exception e) {
			isValid = false;
		}

		IntrospectResponse result = IntrospectResponse.builder().valid(isValid) // true || false
				.scope(userDetails.getRole().getName())// USER || ADMIN
				.build();

		return ResponseEntity
				.ok(ApiResponse.<IntrospectResponse>builder().code(HttpStatus.OK.value()).result(result).build());
	}

//Change Password
	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String token,
			@RequestBody @Valid ChangePasswordDTO dto) {
		try {
			userService.changePassword(token, dto);
			return ResponseEntity.ok("Đổi mật khẩu thành công");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

//Google Login
	@PostMapping("/google")
	public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
		try {
			// Bước 1: Xác thực token ID từ Google
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
					new JacksonFactory())
					.setAudience(Collections
							.singletonList("191517587755-sl1mq6vmh78knfhj15k9o3mahjml8qqj.apps.googleusercontent.com"))
					.build();

			GoogleIdToken idToken = verifier.verify(request.getCredential());
			if (idToken == null) {
				return ResponseEntity.badRequest().body("Token không hợp lệ.");
			}

			GoogleIdToken.Payload payload = idToken.getPayload();
			String email = payload.getEmail();
			String name = (String) payload.get("name");
			String pictureUrl = (String) payload.get("picture");
			String googleId = payload.getSubject();

			Role role = roleRepository.findByName("USER")
					.orElseThrow(() -> new DataIntegrityViolationException("Không tìm thấy Role USER"));

			if (Role.ADMIN.equalsIgnoreCase(role.getName())) {
				throw new PermissionDenyException("Bạn không thể đăng ký tài khoản ADMIN");
			}

			// Bước 2: Tìm user theo googleAccountId hoặc email
			User user = userRepository.findByGoogleAccountId(googleId).orElseGet(() -> {
				// Nếu chưa có thì tạo mới
				User newUser = new User();
				newUser.setEmail(email);
				newUser.setName(name);
				newUser.setUrlProfileImage(pictureUrl);
				newUser.setGoogleAccountId(googleId);
				newUser.setActive(true);
				newUser.setRole(role);// Mặc định Role User
				newUser.setLoginProvider("GOOGLE");
				newUser.setCreateAt(new Date());
				return userRepository.save(newUser);
			});

			// Bước 3: Sinh JWT token cho user
			String jwt = jwtTokenUtils.generateToken(user);
			return ResponseEntity.ok(Map.of("token", jwt, "user", user));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Đăng nhập Google thất bại: " + e.getMessage());
		}
	}

}
