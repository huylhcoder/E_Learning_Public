package com.fpoly.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.entity.FavoriteCourse;
import com.fpoly.entity.User;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.service.FavoriteCourseService;
import com.fpoly.service.UserService;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/favorite-course")
public class FavoriteCourseController {

	@Autowired
	private FavoriteCourseService favoriteCourseService;

	@Autowired
	private JwtTokenUtils jwtTokenUtils;

	@Autowired
	private UserService UserService;

//MyCoursePage
	
	//Danh sách khóa học yêu thích
	@GetMapping("")
	public List<FavoriteCourse> fillListCourseFcByUserIDHao(@RequestHeader("Authorization") String token) {
		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", "").trim());
		User user = UserService.getUserByEmailToan(email);
		return favoriteCourseService.fillListCourseFcByUserID(user.getUserId());
	}

	@PostMapping("/add/{courseId}")
	public ResponseEntity<Map<String, String>> addCourseToFavoriteCourse(@PathVariable int courseId,
			@RequestHeader("Authorization") String token) {
		Map<String, String> response = new HashMap<>();
		try {
			// Lấy userId từ token
			String email = jwtTokenUtils.extractEmail(token.substring(7)); // Loại bỏ "Bearer " khỏi token
			User user = UserService.getUserByEmailToan(email);
			int userId = user.getUserId(); // Lấy userId từ User object

			// Thực hiện thêm khóa học vào danh sách yêu thích
			favoriteCourseService.addCourseToFavoriteCourse(courseId, userId);

			response.put("message", "Course added to favorite successfully.");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("error", e.getMessage());
			return ResponseEntity.status(400).body(response); // Trả về thông báo lỗi nếu khóa học đã tồn tại
		} catch (Exception e) {
			response.put("error", "Error adding course to favorite: " + e.getMessage());
			return ResponseEntity.status(500).body(response);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<FavoriteCourse> removefavoriteCourseHao(@PathVariable("id") int id) {
		FavoriteCourse kiemTraTonTai = favoriteCourseService.timKiemFavoriteCourseTheoId(id);
		if (kiemTraTonTai != null) {
			favoriteCourseService.removeFavoriteCourse(id);
			return ResponseEntity.ok(kiemTraTonTai);
		}
		return ResponseEntity.ok(kiemTraTonTai);
	}
	// end code hào
}
