package com.fpoly.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fpoly.cloudinary.CloudinaryService;
import com.fpoly.controller.OtpController.DateTimeUtils;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.MailInfo;
import com.fpoly.entity.User;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.service.CourseProgressService;
import com.fpoly.service.EmailService;
import com.fpoly.service.UserService;

import io.jsonwebtoken.io.IOException;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;

	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private JwtTokenUtils jwtTokenUtils;

//Header - DefaultLayout
	// Lấy Avatar
	@GetMapping("/get-avatar")
	public ResponseEntity<String> getAvatar(@RequestHeader("Authorization") String token) {
		try {
			String cleanToken = token.replace("Bearer ", "").trim();
			String email = jwtTokenUtils.extractEmail(cleanToken);
			User user = userService.getUserByEmailToan(email);

			return ResponseEntity.ok(user.getUrlProfileImage());
		} catch (Exception e) {
			System.err.println("Lỗi trích xuất Email từ token: " + e.getMessage());
			return ResponseEntity.badRequest().body(null);
		}
	}

	// Lấy Tác giả
	@GetMapping("/email/{user_email}")
	public User getUserByEmail(@PathVariable String user_email) {
		return userService.getUserByEmailToan(user_email);
	}

//UserProfile Layout
	// Lấy thông tin user
	@GetMapping("/profile")
	public ResponseEntity<?> getUserTheoEmail(@RequestHeader(value = "Authorization", required = false) String token) {
		String email = "";
		try {
			email = jwtTokenUtils.extractEmail(token.replace("Bearer ", "").trim());
			System.out.println("Email: " + email);
		} catch (Exception e) {
			System.out.println("Token không hợp lệ: " + token);
			return ResponseEntity.status(400).body("Token không hợp lệ.");
		}
		User user = userService.getUserByEmailToan(email);
		if (user == null) {
			return ResponseEntity.status(404).body("Người dùng không tồn tại.");
		}
		return ResponseEntity.ok(user);
	}

	@GetMapping("/update/{user_email}")
	public ResponseEntity<User> updateUser(@PathVariable String user_email) {
		User users = userService.getUserByEmailToan(user_email);
		User savedUser = userService.saveUser(users);
		return ResponseEntity.ok(savedUser);
	}

	@PutMapping("/update/{token}")
	public ResponseEntity<?> updateUserEmail(@PathVariable("token") String token,
			@RequestParam("name") Optional<String> name, @RequestParam("phone") Optional<String> phone,
			@RequestParam("urlProfileImage") Optional<String> urlProfileImage,
			@RequestParam(value = "file", required = false) MultipartFile file)
			throws IOException, java.io.IOException {
		System.out.println("Tên: " + name.orElse(""));
		System.out.println("Phone: " + phone.orElse(""));
		System.out.println(file);
		String email = "";
		try {
			email = jwtTokenUtils.extractEmail(token);
			System.out.println("Email: " + email);
		} catch (Exception e) {
			return ResponseEntity.status(400).body("Token không hợp lệ.");
		}
		User userCanCapNhat = userService.getUserByEmailToan(email);
		if (userCanCapNhat == null) {
			return ResponseEntity.status(404).body("Người dùng không tồn tại.");
		}
		// Xử lý hình ảnh nếu có file
		if (file != null && !file.isEmpty()) {
			MultipartFile fileUpToCloudinary = file;
			Map<?, ?> data = this.cloudinaryService.upload(fileUpToCloudinary);
			// Cập nhật lại ảnh nếu có
			urlProfileImage = Optional.ofNullable((String) data.get("url").toString());
		}
		// Cập nhật thông tin người dùng
		userCanCapNhat.setName(name.orElse(""));
		userCanCapNhat.setPhone(phone.orElse(""));
		userCanCapNhat.setUrlProfileImage(urlProfileImage.orElse(""));
		User updatedUser = userService.saveUser(userCanCapNhat);
		return ResponseEntity.ok(updatedUser);
	}

	@GetMapping("/userAdmin")
	public List<User> getAllUserAdmin() {
		return userService.getAllUser();
	}

//	@PutMapping("/unblockUser/{userId}")
//	public ResponseEntity<User> unblockStatusKhoa(@PathVariable("userId") int userId) {
//		User kiemTraTonTai = userService.getUserById(userId);
//		if (kiemTraTonTai != null) {
//			kiemTraTonTai.setActive(false);
//			userService.saveUser(kiemTraTonTai);
//			return ResponseEntity.ok(kiemTraTonTai);
//		}
//		return ResponseEntity.ok(kiemTraTonTai);
//	}
	// Phương thức lấy ngày giờ hiện tại
	public class DateTimeUtils {
		// Định dạng ngày giờ theo yêu cầu
		private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

		// Phương thức lấy ngày giờ hiện tại dưới dạng chuỗi
		public static String getCurrentDateTime() {
			LocalDateTime now = LocalDateTime.now();
			return now.format(DATE_TIME_FORMATTER);
		}
	}

	@PutMapping("/unblockUser/{userId}")
	public ResponseEntity<?> unblockStatusKhoa(@PathVariable("userId") int userId,
			@RequestParam("lyDoChan") String lyDoChan,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		User user = userService.getUserById(userId);
		String email = user.getEmail();
		String userName = user.getName();
		try {
			// Chuẩn bị nội dung HTML cho email
			String htmlContent = "<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\">"
					+ "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"
					+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
					+ "<title>Thông báo khóa tài khoản</title>" + "<style>"
					+ "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }"
					+ ".container { max-width: 100%; margin: 20px auto; padding: 20px; background-color: #ffffff; "
					+ "border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
					+ ".header { background-color: #66cc66; padding: 15px; text-align: left; color: #ffffff; font-size: 20px; font-weight: bold; }"
					+ ".content { font-size: 16px; color: #333; line-height: 1.6; margin-top: 20px; }"
					+ ".content p { margin: 10px 0; }" + ".footer { font-size: 14px; color: #666; margin-top: 20px; }"
					+ "</style></head>" + "<body>" + "<div class=\"container\">"
					+ "<div class=\"header\">E - Learning</div>" + "<div class=\"content\">" + "<p>Kính gửi <strong>"
					+ userName + "</strong>,</p>" + "<p>Tài khoản của bạn đã bị chặn vào lúc <strong>"
					+ DateTimeUtils.getCurrentDateTime() + "</strong> vì lý do sau:</p>" + "<p><strong>" + lyDoChan
					+ "</strong></p>"
					+ "<p>Chúng tôi rất tiếc vì điều này! Nếu bạn có bất kỳ câu hỏi nào, xin vui lòng liên hệ với tôi.</p>"
					+ "<p>Trân trọng,</p>" + "</div>" + "</div>" + "</body></html>";

			// Gửi email với file đính kèm nếu có
			String commentPath = null;
			if (file != null && !file.isEmpty()) {
				// Lưu tệp vào thư mục mong muốn
				String fileName = file.getOriginalFilename();
				commentPath = "C:\\Users\\Admin\\Downloads\\commentFile\\" + fileName; // Đường dẫn lưu tệp
				Files.copy(file.getInputStream(), Paths.get(commentPath), StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Tệp đã được lưu: " + fileName);
			}

			// Gửi email
			emailService.sendCommentFile(new MailInfo(email, "Phản hồi từ người dùng", htmlContent), commentPath);
			user.setActive(false);
			userService.saveUser(user);
			return ResponseEntity.ok(Collections.singletonMap("message", "Gửi phản hồi thành công"));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("message", "Lỗi gửi phản hồi"));
		}

	}

	@PutMapping("/blockUser/{userId}")
	public ResponseEntity<User> blockStatusKhoa(@PathVariable("userId") int userId) {
		User kiemTraTonTai = userService.getUserById(userId);
		if (kiemTraTonTai != null) {
			kiemTraTonTai.setActive(true);
			userService.saveUser(kiemTraTonTai);
			return ResponseEntity.ok(kiemTraTonTai);
		}
		return ResponseEntity.ok(kiemTraTonTai);
	}

//	
//	@GetMapping("/userAdmin/{userId}")
//	public ResponseEntity<User> getUserById(@PathVariable int userId) {
//	    User user = userService.getUserById(userId);
//	    if (user != null) {
//	        return ResponseEntity.ok(user); // Trả về người dùng nếu tìm thấy
//	    } else {
//	        return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy
//	    }
//	}

//	@GetMapping("/userAdmin/{user_id}")
//	public List<CourseProgress> fillListCourseCartByUserID(@PathVariable int user_id) {
//		List<CourseProgress> listUser = CPS.FillCourse(user_id);
//		return listUser;
//	}
//	@GetMapping("")
//	public List<User> getAllUserAdmin() {
//		return userService.getAllUser();
//	}

}
