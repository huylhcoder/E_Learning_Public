package com.fpoly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.Lesson;
import com.fpoly.entity.LessonComplete;
import com.fpoly.entity.User;
import com.fpoly.repository.CourseProgressRepository;
import com.fpoly.repository.LessonCompleteRepository;
import com.fpoly.repository.LessonRepository;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.service.CourseService;
import com.fpoly.service.LessonService;
import com.fpoly.service.UserService;
import com.fpoly.cloudinary.VideoService;
import com.fpoly.dto.learning.LessonCompleteRequest;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/lesson")
public class LessonController {
	@Autowired
	private LessonService lessonService;
	@Autowired
	private CourseService CourseService;
	@Autowired
	private VideoService videoService;
	@Autowired
	private UserService userService;

	@Autowired
	private LessonRepository lessonRepo;
	@Autowired
	private LessonCompleteRepository lessonCompleteRepo;
	@Autowired
	private CourseProgressRepository courseProgressRepo;

	@Autowired
	private JwtTokenUtils jwtTokenUtils;

//Learning Page
	// Đánh dấu hoàn thành bài học
	@PostMapping("/lesson-complete")
	public ResponseEntity<?> completeLesson(@RequestBody LessonCompleteRequest req,
			@RequestHeader("Authorization") String token) {

		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", "").trim());
		User user = userService.getUserByEmailToan(email);

		if (user == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		// Kiểm tra lesson đã hoàn thành chưa
		boolean exists = lessonCompleteRepo.existsByUser_UserIdAndLesson_LessonId(user.getUserId(), req.getLessonId());
		if (!exists) {
			Lesson lesson = lessonRepo.findById(req.getLessonId())
					.orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

			// Lưu lessonComplete
			LessonComplete lc = new LessonComplete();
			lc.setLesson(lesson);
			lc.setUser(user);
			lessonCompleteRepo.save(lc);

			// Cập nhật CourseProgress
			CourseProgress cp = courseProgressRepo
					.findByUser_UserIdAndCourse_CourseId(user.getUserId(), req.getCourseId())
					.orElseThrow(() -> new IllegalArgumentException("CourseProgress not found"));

			cp.setCurrentLesson(lesson);
			cp.setTotalLessionComplete(cp.getTotalLessionComplete() + 1);

			// 🔑 Tính lại tiến độ dựa trên cả lesson + quiz
			int totalUnits = cp.getTotalLession() + cp.getTotalQuiz();
			int completedUnits = cp.getTotalLessionComplete() + cp.getTotalTestComplete();

			float percentage = totalUnits > 0 ? ((float) completedUnits / totalUnits) * 100 : 0;
			cp.setProgressPercentage(percentage);

			courseProgressRepo.save(cp);
		}

		return ResponseEntity.ok("Lesson marked as complete");
	}

//	@GetMapping("/course/{id}")
//    public ResponseEntity<List<Lesson>> getLessionByCourse(@PathVariable("id") int id) {
//        Course course = CourseService.getCourseID(id).orElse(null);
//        if (course == null) {
//            return ResponseEntity.notFound().build();
//        }
//        List<Lesson> listLession = LessonService.getLessonByCourse(course);
//        return ResponseEntity.ok(listLession);
//    }

	// useSecureVideo hook
	@GetMapping("/video/{lessonId}")
	public ResponseEntity<?> loadVideoForLesson(@RequestHeader("Authorization") String authHeader,
			@PathVariable("lessonId") int lessonId, @RequestParam("courseId") Optional<Integer> courseId) {

		// Check token header
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Thiếu hoặc sai token!\"}");
		}
		String token = authHeader.substring(7); // cắt "Bearer "

		// (Tuỳ chọn) Validate token và courseId có quyền xem video không
//		    boolean hasPermission = videoService.checkUserAccess(token, courseId.get());
//		    if (!hasPermission) {
//		        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//		                .body("{\"message\": \"Bạn không có quyền xem video này!\"}");
//		    }

		// Lấy lesson
		Optional<Lesson> lessonOpt = lessonService.getLessonById_Huy(lessonId);
		if (lessonOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Bài học không tồn tại!\"}");
		}

		Lesson lesson = lessonOpt.get();
		String videoUrl = lesson.getPathVideo();

		// Check tham số
		if (videoUrl == null || videoUrl.isBlank()) {
			return ResponseEntity.badRequest().body("{\"message\": \"Thiếu thông tin cần thiết!\"}");
		}

		// Tải video
		try {
			byte[] videoBytes = videoService.downloadVideo(videoUrl);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(videoBytes);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"message\": \"Server không thể tải video!\"}");
		}
	}
}
