package com.fpoly.controller;

import java.io.Console;
import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fpoly.cloudinary.CloudinaryService;
import com.fpoly.dto.CourseDetailDTO;
import com.fpoly.dto.CourseDetailManagerDTO;
import com.fpoly.entity.Course;
import com.fpoly.service.CourseService;
import com.fpoly.service.LessonService;
import com.fpoly.service.SectionService;
import com.fpoly.service.UserService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/course-manager-detail")
public class CourseDetailManagerController {
	@Autowired
	private CourseService courseService;
	@Autowired
	private SectionService sectionService;
	@Autowired
	private LessonService lessonService;
	@Autowired
	private UserService userService;

	@Autowired
	private CloudinaryService cloudinaryService;

	// Tìm khóa học theo mã khóa hoc
//	@GetMapping("/{courseId}")
//	public ResponseEntity<?> courseDetails(@PathVariable("courseId") int courseId) {
//	    Course course = courseService.timKhoaHocTheoMaKhoaHocHuy(courseId).orElse(null);
//	    if (course != null) {
//	        CourseDetailManagerDTO courseDTO = new CourseDetailManagerDTO();
//	        courseDTO.setCourseId(course.getCourseId());
//	        courseDTO.setName(course.getName());
//	        courseDTO.setStatus(course.getStatus());
//	        courseDTO.setDescription(course.getDescription());
//	        courseDTO.setContentDescription(course.getContentDescription()); // ✅ thêm vào
//	        courseDTO.setAvatar(course.getAvatar());
//	        courseDTO.setPrice(course.getPrice());
//	        courseDTO.setTopic(course.getTopic());
//	        courseDTO.setLevelId(course.getCourseLevel().getCourseLevelId());
//
//	        // Lấy list categoryId từ bảng trung gian
//	        List<Integer> categoryIds = course.getCourseCategories().stream()
//	                .map(cc -> cc.getCategory().getCategoryId())
//	                .collect(Collectors.toList());
//	        courseDTO.setCategoryIds(categoryIds);
//
//	        return ResponseEntity.ok(courseDTO);
//	    }
//	    return ResponseEntity.notFound().build();
//	}
	@GetMapping("/{courseId}")
	public ResponseEntity<?> courseDetails(@PathVariable("courseId") int courseId) {
		Course course = courseService.timKhoaHocTheoMaKhoaHocHuy(courseId).orElse(null);

		if (course != null) {
			CourseDetailManagerDTO courseDTO = new CourseDetailManagerDTO();
			courseDTO.setCourseId(course.getCourseId());

			// Các trường có thể là null
			courseDTO.setName(course.getName());
			courseDTO.setDescription(course.getDescription());
			courseDTO.setContentDescription(course.getContentDescription());
			courseDTO.setAvatar(course.getAvatar());
			courseDTO.setPrice(course.getPrice());
			courseDTO.setTopic(course.getTopic());

			// 1. Xử lý CourseLevel (Nguyên nhân lỗi NullPointerException)
			// Nếu CourseLevel là null (khóa học nháp), đặt levelId là null hoặc 0
			if (course.getCourseLevel() != null) {
				courseDTO.setLevelId(course.getCourseLevel().getCourseLevelId());
			} else {
				courseDTO.setLevelId(0); // Hoặc 0 nếu bạn muốn một giá trị mặc định số
			}

			courseDTO.setStatus(course.getStatus());

			// 2. Xử lý CourseCategories (có thể null hoặc rỗng)
			// Nếu CourseCategories null, trả về danh sách rỗng để tránh
			// NullPointerException khi stream
			if (course.getCourseCategories() != null && !course.getCourseCategories().isEmpty()) {
				List<Integer> categoryIds = course.getCourseCategories().stream()
						.map(cc -> cc.getCategory().getCategoryId()).collect(Collectors.toList());
				courseDTO.setCategoryIds(categoryIds);
			} else {
				courseDTO.setCategoryIds(Collections.emptyList());
			}

			return ResponseEntity.ok(courseDTO);
		}
		return ResponseEntity.notFound().build();
	}

	// Cập nhật nội dung khóa học
	@PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateCourseDetailInfo(@PathVariable("courseId") int courseId,
			@RequestParam("courseId") Optional<Integer> id, @RequestParam("name") Optional<String> name,
			@RequestParam("status") Optional<Integer> status, @RequestParam("description") Optional<String> description,
			@RequestParam("contentDescription") Optional<String> contentDescription,
			@RequestParam("avatar") Optional<String> avatar, @RequestParam("price") Optional<Float> price,
			@RequestParam("topic") Optional<String> topic,
			@RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
			@RequestParam("levelId") Optional<Integer> levelId,
			@RequestParam(value = "file", required = false) MultipartFile file)
			throws IOException, java.io.IOException {

		if (file != null && !file.isEmpty()) {
			Map<?, ?> data = cloudinaryService.upload(file);
			avatar = Optional.ofNullable((String) data.get("url"));
		}

		try {
			CourseDetailManagerDTO courseDTO = new CourseDetailManagerDTO();
			courseDTO.setCourseId(courseId);
			courseDTO.setName(name.orElse(null));
			courseDTO.setStatus(status.orElse(0));
			courseDTO.setDescription(description.orElse(null));
			courseDTO.setContentDescription(contentDescription.orElse(null)); // ✅
			courseDTO.setAvatar(avatar.orElse(null));
			courseDTO.setPrice(price.orElse(0f));
			courseDTO.setTopic(topic.orElse(null));
			courseDTO.setCategoryIds(categoryIds); // ✅
			courseDTO.setLevelId(levelId.orElse(null));

			return ResponseEntity.ok(courseService.luuThongTinKhoaHoc(courseDTO));
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
		}
	}

	// Xóa phần
	@DeleteMapping("/remove-section/{sectionId}")
	public ResponseEntity<?> removSection(@PathVariable("sectionId") int sectionId) {
		System.out.println(sectionId);
		try {
			sectionService.removeSection(sectionId);
			return ResponseEntity.ok("{\"message\": \"Xóa khóa học thành công!\"}");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Có lỗi xảy ra: " + e.getMessage());
		}
	}

}
