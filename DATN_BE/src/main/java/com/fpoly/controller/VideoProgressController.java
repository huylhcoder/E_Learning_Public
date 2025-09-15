package com.fpoly.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.dto.MonthlyRevenueDTO;
import com.fpoly.dto.UserMonthlyStatsDTO;
import com.fpoly.dto.UserYearStatsDTO;
import com.fpoly.dto.YearRevenueDTO;
import com.fpoly.dto.learning.VideoProgressRequest;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.RegisteredCourse;
import com.fpoly.entity.User;
import com.fpoly.entity.VideoProgress;
import com.fpoly.repository.RegisteredCourseRepository;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.service.CourseProgressService;
import com.fpoly.service.CourseService;
import com.fpoly.service.RegisteredCourseService;
import com.fpoly.service.UserService;
import com.fpoly.service.VideoProgressService;

import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/video-progress")
public class VideoProgressController {
	@Autowired
	private VideoProgressService videoProgressService;
	@Autowired
	private UserService UserService;

	@Autowired
	private RegisteredCourseRepository registeredCourseRepository;

	@Autowired
	private JwtTokenUtils jwtTokenUtils;

//Learning Page
	// Lưu tiến độ video
	@PostMapping("/save")
	public ResponseEntity<VideoProgress> saveOrUpdateProgress(@RequestBody VideoProgressRequest request,
			@RequestHeader("Authorization") String token) {

		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", "").trim());
		User user = UserService.getUserByEmailToan(email);

		if (user == null) {
			return ResponseEntity.status(HttpStatus.SC_FORBIDDEN).build();
		}

		// Tìm registeredCourse theo userId + courseId
		RegisteredCourse rc = registeredCourseRepository
				.findByUser_UserIdAndCourse_CourseId(user.getUserId(), request.getCourseId())
				.orElseThrow(() -> new IllegalArgumentException("User chưa đăng ký khóa học"));

		VideoProgress progress = new VideoProgress();
		progress.setUserId(user.getUserId());
		progress.setRegisteredCourseId(rc.getRegisteredCourseId()); // BE tự lấy
		progress.setLessonId(request.getLessonId());
		progress.setPathVideo(request.getPathVideo());
		progress.setVideoProgress(request.getVideoProgress());
		progress.setUpdate_at(LocalDateTime.now());

		VideoProgress savedProgress = videoProgressService.saveOrUpdateVideoProgress(progress, user.getUserId());

		return ResponseEntity.ok(savedProgress);
	}

	// API để lấy tiến độ video của người dùng theo userId và lessonId
	@GetMapping("/{lessonId}")
	public ResponseEntity<?> getProgress(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable int lessonId) {
		System.out.println("Authorization header: " + authorizationHeader);

		String token;
		String email;

		try {
			// Kiểm tra header Authorization
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(400).body("Authorization header không hợp lệ.");
			}

			// Trích xuất token từ Authorization header
			token = authorizationHeader.substring(7); // Bỏ "Bearer " để lấy token

			// Trích xuất email từ token
			email = jwtTokenUtils.extractEmail(token);
			System.out.println("Extracted Email: " + email);
			System.out.println("Token: " + token);
		} catch (Exception e) {
			return ResponseEntity.status(400).body("Token không hợp lệ.");
		}

		// Kiểm tra thông tin người dùng từ email
		User user = UserService.getUserByEmailToan(email);
		if (user == null) {
			return ResponseEntity.status(404).body("Người dùng không tồn tại.");
		}
		return videoProgressService.getVideoProgressByUserAndLesson(user.getUserId(), lessonId).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());

	}

}