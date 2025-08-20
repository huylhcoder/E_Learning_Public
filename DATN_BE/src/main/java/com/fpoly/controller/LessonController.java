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
import com.fpoly.entity.Lesson;
import com.fpoly.entity.User;
import com.fpoly.service.CourseService;
import com.fpoly.service.LessonService;
import com.fpoly.cloudinary.VideoService;

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

//Learning Page
	// Đánh dấu hoàn thành bài học
//	@PostMapping("/lesson-complete")
//	public ResponseEntity<?> completeLesson(@RequestBody LessonCompleteRequest req,
//			@AuthenticationPrincipal User user) {
//		if (!lessonCompleteRepo.existsByUserIdAndLessonId(user.getUserId(), req.getLessonId())) {
//			lessonCompleteRepo.save(new LessonComplete(null, lessonRepo.findById(req.getLessonId()).get(), user));
//			CourseProgress cp = courseProgressRepo.findByUserIdAndCourseId(user.getUserId(), req.getCourseId()).get();
//			cp.setTotalLessonComplete(cp.getTotalLessonComplete() + 1);
//			cp.setProgressPercentage(((float) cp.getTotalLessonComplete() / cp.getTotalLesson()) * 100);
//			courseProgressRepo.save(cp);
//		}
//		return ResponseEntity.ok("Lesson marked as complete");
//	}

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
