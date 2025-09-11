package com.fpoly.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.dto.CourseProgressRequest;
import com.fpoly.dto.MonthlyRevenueDTO;
import com.fpoly.dto.UpdateLessonRequest;
import com.fpoly.dto.UserMonthlyStatsDTO;
import com.fpoly.dto.UserYearStatsDTO;
import com.fpoly.dto.YearRevenueDTO;
import com.fpoly.dto.learning.CourseProgressDTO;
import com.fpoly.dto.learning.LessonDTO;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.Lesson;
import com.fpoly.entity.RegisteredCourse;
import com.fpoly.entity.User;

import com.fpoly.repository.CourseProgressRepository;
import com.fpoly.repository.CourseRepository;
import com.fpoly.repository.UserRepository;

import com.fpoly.security.JwtTokenUtils;

import com.fpoly.service.CourseProgressService;
import com.fpoly.service.CourseService;
import com.fpoly.service.RegisteredCourseService;
import com.fpoly.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/course-progress")
public class CourseProgressController {
	@Autowired
	private UserService userService;
	@Autowired
	private RegisteredCourseService registeredCourseService;
	@Autowired
	private CourseProgressService courseProgressService;
	@Autowired
	private CourseService courseService;
	@Autowired
	private JwtTokenUtils jwtTokenUtils;

//Learning Page
	// Hiển thị tiến độ
	@GetMapping("/progress")
	public ResponseEntity<CourseProgressDTO> getCourseProgress(
			@RequestHeader(value = "Authorization", required = false) String token,
			@RequestParam("courseId") int courseId) {

		CourseProgressDTO dto = courseProgressService.getCourseProgress(courseId, token);
		return ResponseEntity.ok(dto);
	}

	// Hiển thị bài học hiện taị
	@GetMapping("/current-lesson")
	public ResponseEntity<LessonDTO> getCurrentLesson(
			@RequestHeader(value = "Authorization", required = false) String token,
			@RequestParam(value = "courseId") int courseId) {

		LessonDTO dto = courseProgressService.getCurrentLessonDTO(courseId, token);
		return ResponseEntity.ok(dto);
	}

	// Cập nhật bài học hiện tại
	@PutMapping("/update-current-lesson")
	public ResponseEntity<Lesson> updateCurrentLesson(@RequestBody UpdateLessonRequest request) {
		Lesson updatedLesson = courseProgressService.updateCurrentLesson(request);
		return ResponseEntity.ok(updatedLesson);
	}

//Dashboard Page

	//Hiển thị danh sách người dùng
	@GetMapping("/user-admin")
	public List<User> fillAllUserRole2() {
		return userService.fillAllUserRole2();
	}

	// số khóa học hoàn thành để phát chứng chỉ
	@GetMapping("/total-complete")
	public List<CourseProgress> FillTotalCourseComplete() {
		return courseProgressService.FillTotalCourseComplete();
	}

	// Tổng Doanh Thu
	@GetMapping("/total-revenue")
	public List<RegisteredCourse> GetRegisteredCourse() {
		return userService.GetRegisteredCourse();
	}

//Khác

	@GetMapping("/{userId}")
	public List<CourseProgress> getCourseProgressByUserId(@PathVariable int userId) {
		List<CourseProgress> listCourseProgress = courseProgressService.FillCourseKhoa(userId);
		if (listCourseProgress == null || listCourseProgress.isEmpty()) {
			return new ArrayList<>(); // Trả về danh sách rỗng thay vì null
		}
		return listCourseProgress;
	}

	@PutMapping("/{userId}")
	public ResponseEntity<User> updateStatusKhoa(@PathVariable("userId") int userId, @RequestBody User user) {
		User kiemTraTonTai = userService.getUserById(userId);
		if (kiemTraTonTai != null) {
			user.setUserId(userId);
			userService.saveUser(user);
			return ResponseEntity.ok(user);
		}
		return ResponseEntity.ok(user);
	}

	// CODE của HBao
	// Khóa học đã đăng ký theo userid

//	@GetMapping("/courseRegister/{userId}")
//	public ResponseEntity<?> getRegisteredCoursesByUserId(@PathVariable int userId,
//			@RequestHeader("Authorization") String token) {
//		String FToken = token.replace("Bearer ", "").trim();
//		System.out.println("Token: " + token);
//		String email = "";
//		try {
//			email = jwtTokenUtils.extractEmail(FToken);
//			System.out.println("Email: " + email);
//		} catch (Exception e) {
//			System.err.println("Lỗi trích xuất Email từ token: " + e.getMessage());
//			return ResponseEntity.badRequest().body(null);
//		}
//		User user = userService.getUserByEmailToan(email);
//		Integer userID = user.getUserId();
//		if (userID == null) {
//			return ResponseEntity.ok(registeredCourseService.findRegisterCourseByUserId(userId));
//		}
//		if (userID != null) {
//			return ResponseEntity.ok(registeredCourseService.findRegisterCourseByUserId(userId));
//		} else {
//			return ResponseEntity.badRequest().body(null);
//		}
//		// return registeredCourseService.findRegisterCourseByUserId(userId);
//	}

	// Danh sách khóa học đã bán và số lượt bán
	@GetMapping("/listRegisteredCourse")
	public List<Object[]> getRegisteredCourses() {
		return registeredCourseService.getRegisteredCourses();
	}

	@GetMapping("/courseRegister/{userId}")
	public List<RegisteredCourse> getRegisteredCoursesByUserId(@PathVariable int userId) {
		return registeredCourseService.findRegisterCourseByUserId(userId);
	}

	// Biểu đồ người dùng theo tháng
	@GetMapping("/userStatsByMonth")
	public ResponseEntity<List<UserMonthlyStatsDTO>> getUserStatsByMonth() {
		List<UserMonthlyStatsDTO> userStats = userService.getUserStatsByMonth();
		return ResponseEntity.ok(userStats);
	}

	// Biểu đồ người dùng theo năm
	@GetMapping("/userStatsByYear")
	public ResponseEntity<List<UserYearStatsDTO>> getUserStatsByYear() {
		List<UserYearStatsDTO> userStats = userService.getUserStatsByYear();
		return ResponseEntity.ok(userStats);
	}

	// Biểu đồ doanh thu theo tháng
	@GetMapping("/courseStatsByMonth")
	public ResponseEntity<List<MonthlyRevenueDTO>> getCourseStatsByMonth() {
		List<MonthlyRevenueDTO> courseStats = userService.getCourseStatsByMonth();
		return ResponseEntity.ok(courseStats);
	}

	// Biểu đồ doanh thu theo năm
	@GetMapping("/courseStatsByYear")
	public ResponseEntity<List<YearRevenueDTO>> getCourseStatsByYear() {
		List<YearRevenueDTO> courseStats = userService.getCourseStatsByYear();
		return ResponseEntity.ok(courseStats);
	}

//	// Tạo bảng tiến độ Bài học
//	@PostMapping("/update-progress/{courseId}")
//	public ResponseEntity<?> updateCourseProgress(@RequestHeader("Authorization") Optional<String> token,
//			@PathVariable("courseId") int courseId, @RequestBody CourseProgressRequest courseProgressRequest) { // Sử
//		String tokenValue = token.map(t -> t.replace("Bearer ", "")).orElse("");
//		String email;
//
//		try {
//			email = jwtTokenUtils.extractEmail(tokenValue);
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("{\"message\": \"Token không hợp lệ!\"}");
//		}
//
//		User user = userService.getUserByEmailToan(email);
//		if (user == null) {
//			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("{\"message\": \"Email không tồn tại!\"}");
//		}
//
//		CourseProgress courseProgress = courseProgressService.getCourseProgress(courseId, user.getUserId());
//
//		if (courseProgress == null) {
//			// Nếu không tìm thấy tiến độ, tạo mới
//			courseProgress = new CourseProgress();
//			courseProgress.setUser(user);
//			courseProgress.setCourse(courseService.timKhoaHocTheoMaKhoaHocTam(courseId).orElse(null));
//			if (courseProgress.getCourse() == null) {
//				return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
//						.body("{\"message\": \"Khóa học không tồn tại!\"}");
//			}
//
//			// Gán các giá trị nhận được từ frontend vào đối tượng courseProgress
//			courseProgress.setCurrentLessionId(courseProgressRequest.getCurrentLessionId());
//			courseProgress.setTotalLession(courseProgressRequest.getTotalLession());
//			courseProgress.setTotalQuiz(courseProgressRequest.getTotalQuiz());
//			courseProgress.setTotalLessionComplete(courseProgressRequest.getTotalLessionComplete());
//			courseProgress.setTotalTestComplete(courseProgressRequest.getTotalTestComplete());
//			courseProgress.setProgressPercentage(courseProgressRequest.getProgressPercentage());
//			courseProgress.setProgressStatus(courseProgressRequest.getProgressStatus());
//
//			// Lưu tiến độ khóa học mới
//			courseProgress = courseProgressService.createOrUpdateCourseProgress(courseProgress);
//			return ResponseEntity.status(HttpStatus.SC_CREATED).body(courseProgress);
//		} else {
//			// Nếu tiến độ đã tồn tại, cập nhật tiến độ
//			courseProgress.setCurrentLessionId(courseProgressRequest.getCurrentLessionId());
//			courseProgress.setTotalLession(courseProgressRequest.getTotalLession());
//			courseProgress.setTotalQuiz(courseProgressRequest.getTotalQuiz());
//			courseProgress.setTotalLessionComplete(courseProgressRequest.getTotalLessionComplete());
//			courseProgress.setTotalTestComplete(courseProgressRequest.getTotalTestComplete());
//			courseProgress.setProgressPercentage(courseProgressRequest.getProgressPercentage());
//			courseProgress.setProgressStatus(courseProgressRequest.getProgressStatus());
//
//			courseProgress = courseProgressService.updateProgressPercentage(courseProgress);
//			return ResponseEntity.ok(courseProgress);
//		}
//	}

//	@GetMapping("/learn/{courseId}")
//	public ResponseEntity<?> getCourseProgress(@RequestHeader("Authorization") String authorizationHeader,
//			@PathVariable int courseId) {
//		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Authorization header không hợp lệ.");
//		}
//
//		String token = authorizationHeader.substring(7);
//		String email;
//
//		try {
//			email = jwtTokenUtils.extractEmail(token);
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Token không hợp lệ.");
//		}
//
//		User user = userService.getUserByEmailToan(email);
//		if (user == null) {
//			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Người dùng không tồn tại.");
//		}
//
//		CourseProgress courseProgress = courseProgressService.getCourseProgress(user.getUserId(), courseId);
//		if (courseProgress == null) {
//			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Tiến độ khóa học không tồn tại.");
//		}
//
//		return ResponseEntity.ok(courseProgress);
//	}

}