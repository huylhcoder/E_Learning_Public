package com.fpoly.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fpoly.dto.MonthlyRevenueDTO;
import com.fpoly.dto.UserMonthlyStatsDTO;
import com.fpoly.dto.UserYearStatsDTO;
import com.fpoly.dto.YearRevenueDTO;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.RegisteredCourse;
import com.fpoly.entity.User;
import com.fpoly.repository.CourseProgressRepository;
import com.fpoly.repository.RegisteredCourseRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fpoly.dto.UserRegisterDTO;
import com.fpoly.entity.Role;
import com.fpoly.entity.User;
import com.fpoly.exceptions.AccountBlockException;
import com.fpoly.exceptions.PermissionDenyException;
import com.fpoly.exceptions.ResourceNotFoundException;
import com.fpoly.repository.RoleRepository;
import com.fpoly.repository.UserRepository;
import com.fpoly.security.JwtTokenUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import jakarta.servlet.jsp.el.NotFoundELResolver;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenUtils jwtTokenUtil;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private CourseProgressRepository courseProgressRepository;
	// Tổng Doanh Thu
	@Autowired
	RegisteredCourseRepository RCR;

	// HBao Code
	@Autowired
	private JavaMailSender mailSender;

//Register Page
	// Tạo ra một người dùng mới
	// Đăng ký một người dùng mới
//	public User createUser(UserRegisterDTO userDTO) throws Exception {
//		String email = userDTO.getEmail();
//		// Kiểm tra email người dùng đã tồn tại chưa
//		if (userRepository.existsByEmail(email)) {
//			throw new DataIntegrityViolationException("User service - Email đã tồn tại");
//		}
//
//		// Chuyển userDTO => User
//		User newUser = new User();
//		newUser.setName(userDTO.getFullname());
//		newUser.setEmail(userDTO.getEmail());
//		newUser.setActive(true);
//		newUser.setCreateAt(new Date());
//		newUser.setUpdateAt(new Date());
//		Role role = roleRepository.findByName("USER")
//				.orElseThrow(() -> new DataIntegrityViolationException("Không tìm thấy Role"));
//		if (role.getName().toUpperCase().equals(Role.ADMIN)) {
//			throw new PermissionDenyException("Bạn không thể đăng ký tài khoản admin");
//		}
//		newUser.setRole(role);
//		// Kiểm tra nếu có accountId, không yêu cầu password
//		if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
//			String password = userDTO.getPassword();
//			String encodedPassword = passwordEncoder.encode(password);
//			System.out.println(encodedPassword);
//			// Insert cái mật khẩu nó đã mã hóa vào
//			// Nó không mã hóa ngược lại được
//			// Nhớ rõ mật khẩu tí đăng nhập
//			newUser.setPassword(encodedPassword);
//		}
//		return userRepository.save(newUser);
//	}
	
	public User createUser(UserRegisterDTO userDTO) throws Exception {
	    String email = userDTO.getEmail();

	    // Kiểm tra email có tồn tại trong DB chưa
	    if (userRepository.existsByEmail(email)) {
	        throw new DataIntegrityViolationException("Email đã tồn tại");
	    }

	    // Tạo đối tượng User mới
	    User newUser = new User();
	    newUser.setName(userDTO.getFullname());
	    newUser.setEmail(userDTO.getEmail());
	    newUser.setActive(true);
	    newUser.setCreateAt(new Date());
	    newUser.setUpdateAt(new Date());

	    // Gán Role mặc định = USER
	    Role role = roleRepository.findByName("USER")
	            .orElseThrow(() -> new DataIntegrityViolationException("Không tìm thấy Role USER"));

	    if (Role.ADMIN.equalsIgnoreCase(role.getName())) {
	        throw new PermissionDenyException("Bạn không thể đăng ký tài khoản ADMIN");
	    }
	    newUser.setRole(role);

	    // Nếu không dùng social login thì phải lưu mật khẩu
	    if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
	        String password = userDTO.getPassword();
	        String encodedPassword = passwordEncoder.encode(password);
	        newUser.setPassword(encodedPassword);
	    }

	    return userRepository.save(newUser);
	}


//Khác

	public List<User> getAllUser() {
		return userRepository.findAll();
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public User getUserByEmailToan(String email) {
		// TODO Auto-generated method stub
		User user = new User();
		user = userRepository.timKiemUserTheoEmailToan(email);
		return user;
	}

//	public boolean existsByUsername(String username) {
//		// Kiểm tra xem tên người dùng có tồn tại không
//		boolean exists = userRepository.existsByUsername(username);
//		System.out.println("Kiểm tra tồn tại tên người dùng: " + username + " - Kết quả: "
//				+ (exists ? "Đã đăng ký" : "Chưa đăng ký"));
//		return exists;
//	}

	public boolean existsByEmail(String email) {
		// Kiểm tra xem email có tồn tại không
		boolean exists = userRepository.existsByEmail(email);
		System.out.println("User service - Kiểm tra tồn tại email: " + email + " - Kết quả: "
				+ (exists ? "Đã đăng ký" : "Chưa đăng ký"));
		return exists;
	}

	public User getUserById(int userId) {
		return userRepository.findById(userId).orElse(null);
	}

	public List<User> fillAllUserRole2() {
		return userRepository.fillAllUserRole2();
	}

	// Kiểm tra email có tồn tại trong cơ sở dữ liệu hay không
	public boolean checkEmailExists(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		return user.isPresent();
	}

	// Gửi mã OTP đến email
	public void sendOtpToEmail(String email, String otp) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(email);
		helper.setSubject("Mã OTP của bạn");
		helper.setText("Mã OTP của bạn là: " + otp);
		mailSender.send(message);
	}

	// Cập nhật mật khẩu
	public void updatePassword(String email, String newPassword) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			String encodedPassword = passwordEncoder.encode(newPassword); // Mã hóa mật khẩu
			user.setPassword(encodedPassword); // Cập nhật mật khẩu đã mã hóa
			// user.setPassword(newPassword); // Cập nhật mật khẩu
			userRepository.save(user); // Lưu thay đổi vào cơ sở dữ liệu
		} else {
			// throw new UserNotFoundException("Người dùng không tồn tại");
		}
	}

	// Cập nhật trạng thái tiến độ
	public void updateStatusProgress(Integer userId, Integer CourseId) {
		Optional<CourseProgress> optionalCP = courseProgressRepository.findByCourseId(userId, CourseId);
		if (optionalCP.isPresent()) {
			CourseProgress CP = optionalCP.get();
			CP.setProgressStatus(1);
			courseProgressRepository.save(CP);
		} else {
			// throw new UserNotFoundException("Người dùng không tồn tại");
		}
	}

	// Lấy tiến độ khóa họ theo userId và CourseId
	public Optional<CourseProgress> findByCourseId(int userId, int courseId) {
		return courseProgressRepository.findByCourseId(userId, courseId);
	}

	public List<RegisteredCourse> GetRegisteredCourse() {
		return RCR.GetRegisteredCourse();
	}

	// Biểu đồ người dùng Theo Tháng
	public List<UserMonthlyStatsDTO> getUserStatsByMonth() {
		List<Object[]> results = userRepository.countUsersByMonth();
		List<UserMonthlyStatsDTO> userStatsList = new ArrayList<>();

		for (Object[] result : results) {
			if (result[0] != null && result[1] != null) { // Kiểm tra null
				Integer month = ((Number) result[0]).intValue();
				Long userCount = ((Number) result[1]).longValue();
				userStatsList.add(new UserMonthlyStatsDTO(month, userCount));
			} else {
				// Xử lý nếu `result[0]` hoặc `result[1]` là null, ví dụ:
				System.out.println("Warning: Result contains null values, skipping...");
			}
		}
		return userStatsList;
	}

	// Biểu đồ người dùng Theo năm
	public List<UserYearStatsDTO> getUserStatsByYear() {
		List<Object[]> results = userRepository.countUsersByYear();
		List<UserYearStatsDTO> userStatsList = new ArrayList<>();

		for (Object[] result : results) {
			if (result[0] != null && result[1] != null) { // Kiểm tra null
				Integer year = ((Number) result[0]).intValue();
				Long userCount = ((Number) result[1]).longValue();
				userStatsList.add(new UserYearStatsDTO(year, userCount));
			} else {
				// Xử lý nếu `result[0]` hoặc `result[1]` là null, ví dụ:
				System.out.println("Warning: Result contains null values, skipping...");
			}
		}
		return userStatsList;
	}

	// Biểu đồ doanh thu theo tháng
	public List<MonthlyRevenueDTO> getCourseStatsByMonth() {
		List<Object[]> results = userRepository.countCoursesByMonth();
		List<MonthlyRevenueDTO> courseStatsList = new ArrayList<>();

		for (Object[] result : results) {
			if (result[0] != null && result[1] != null) { // Kiểm tra null
				Integer month = ((Number) result[0]).intValue();
				Long recordCount = ((Number) result[1]).longValue();
				courseStatsList.add(new MonthlyRevenueDTO(month, recordCount));
			} else {
				// Xử lý nếu `result[0]` hoặc `result[1]` là null, ví dụ:
				System.out.println("Warning: Result contains null values, skipping...");
			}
		}
		return courseStatsList;
	}

	// Biểu đồ doanh thu theo năm
	public List<YearRevenueDTO> getCourseStatsByYear() {
		List<Object[]> results = userRepository.countCoursesByYear();
		List<YearRevenueDTO> courseStatsList = new ArrayList<>();

		for (Object[] result : results) {
			if (result[0] != null && result[1] != null) { // Kiểm tra null
				Integer year = ((Number) result[0]).intValue();
				Long recordCount = ((Number) result[1]).longValue();
				courseStatsList.add(new YearRevenueDTO(year, recordCount));
			} else {
				// Xử lý nếu `result[0]` hoặc `result[1]` là null, ví dụ:
				System.out.println("Warning: Result contains null values, skipping...");
			}
		}
		return courseStatsList;
	}
	// ---------

	// Tôi muốn nó trả về cái chuỗi JWT
	// Chúng ta cần một cái Class để nó gôm code trả về cái Token
	public String login(String email, String password) throws Exception {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		// Nếu không tìm thấy ném ra một cái ngoại lệ
		if (optionalUser.isEmpty()) {
			throw new ResourceNotFoundException("User service 1- Sai tài khoản hoặc mật khẩu");
		}
		User existingUser = optionalUser.get();

		if (existingUser.getFacebookAccountId() == 0 && existingUser.getGooogleAccountId() == 0) {
			// Kiểm tra mật khẩu có đúng không
			// Kiểm tra mật khẩu có trùng với mật khẩu đã mã hóa trong User hay không
			// Khúc này là mật khẩu chưa mã hóa 123456 có trùng với mật khẩu mã hóa không
			// @@@@@
			if (!passwordEncoder.matches(password, existingUser.getPassword())) {
				throw new BadCredentialsException("User service 2- Sai tài khoản hoặc mật khẩu");
			}
		}

		// Check tài khoản có bị khóa không
		if (!existingUser.isActive()) {
			throw new AccountBlockException("Account has been blocked");
		} else {
			System.out.println("Tài khoản được phép sử dụng");
		}
		// Authentication xác thực với Spring Security
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
				password, existingUser.getAuthorities());

		// Tạo ra một cái Token sau khi đã kiểm tra
		authenticationManager.authenticate(authenticationToken);

		// Truyền User vào
		return jwtTokenUtil.generateToken(existingUser);
	}

}
