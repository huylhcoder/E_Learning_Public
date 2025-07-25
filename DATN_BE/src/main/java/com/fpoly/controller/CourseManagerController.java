package com.fpoly.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fpoly.cloudinary.CloudinaryService;
import com.fpoly.dto.CourseDetailManagerDTO;
import com.fpoly.dto.CourseManagerDTO;
import com.fpoly.entity.Course;
import com.fpoly.entity.User;
import com.fpoly.service.CourseService;
import com.fpoly.service.LessonService;
import com.fpoly.service.SectionService;
import com.fpoly.service.UserService;

import io.jsonwebtoken.io.IOException;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/course-manager")
public class CourseManagerController {
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

	// Hiển thị danh sách khóa học đang public + danh sách khóa học không công khai
	@GetMapping("/posted-course")
	public ResponseEntity<?> postedCourses() {
		// Tìm kiểm khóa học đã đăng công khai(1) và không công khai (2)
		List<Course> listCourse = courseService.hienThiKhoaHocTheoTrangThaiKhoaHoc_Huy(Arrays.asList(1, 2));
		List<CourseManagerDTO> listCourseDTO = new ArrayList<CourseManagerDTO>();
		for (Course item : listCourse) {
			CourseManagerDTO courseDTO = new CourseManagerDTO();
			courseDTO.setCourseId(item.getCourseId());
			courseDTO.setAvatar(item.getAvatar());
			courseDTO.setName(item.getName());
			courseDTO.setCourseDuration(item.getCourseDuration());
			courseDTO.setStatus(item.getStatus());
			courseDTO.setCreateAt(item.getCreateAt());
			courseDTO.setNumberOfComment(0);
			courseDTO.setRevenue(0);
			//
			listCourseDTO.add(courseDTO);
		}
		return ResponseEntity.ok(listCourseDTO);
	}

	// Hiển thị khóa học nháp
	@GetMapping("/draft-course")
	public ResponseEntity<?> draftCourses() {
		List<Course> listCourse = courseService.hienThiKhoaHocTheoTrangThaiKhoaHoc_Huy(Arrays.asList(0));
		System.out.println(listCourse);
		List<CourseManagerDTO> listCourseDTO = new ArrayList<CourseManagerDTO>();
		for (Course item : listCourse) {
			CourseManagerDTO courseDTO = new CourseManagerDTO();
			courseDTO.setCourseId(item.getCourseId());
			courseDTO.setAvatar(item.getAvatar());
			courseDTO.setName(item.getName());
			courseDTO.setCourseDuration(item.getCourseDuration());
			courseDTO.setStatus(item.getStatus());
			courseDTO.setCreateAt(item.getCreateAt());
			courseDTO.setNumberOfComment(0);
			courseDTO.setRevenue(0);
			//
			listCourseDTO.add(courseDTO);
		}
		return ResponseEntity.ok(listCourseDTO);
	}

	// Tạo khóa học nháp
	@PostMapping("/add-draf-course")
	public ResponseEntity<?> addCourse() {
		// Tạo khóa học mới
		Course cousre = new Course();
		User user = userService.getUserByEmailToan("lehoanghuycoder@gmail.com");
		cousre.setUser(user);
		cousre.setStatus(0);
		cousre.setCreateAt(new Date());
		cousre.setUpdateAt(new Date());
		courseService.themKhoaHocMoi_Huy(cousre);
		// Khóa học mới tạo
		Course latestCourse = courseService.getLatestCourse();
		return ResponseEntity.ok(latestCourse.getCourseId());
	}

	// Xóa khóa học nháp
	@DeleteMapping("/draft-course/remove-course/{courseId}")
	public ResponseEntity<?> removeDraftCourses(@PathVariable int courseId) {
		try {
			courseService.removeDraftCourse(courseId);
			return ResponseEntity.ok("{\"message\": \"Xóa khóa học thành công!\"}");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Có lỗi xảy ra: " + e.getMessage());
		}
	}

	// Tìm khóa học theo mã khóa hoc
	@GetMapping("/{courseId}")
	public ResponseEntity<?> courseDetails(@PathVariable("courseId") int courseId) {
		Course course = new Course();
		course = courseService.timKhoaHocTheoMaKhoaHocHuy(courseId).orElse(null);
		//
		if (course != null) {
			CourseDetailManagerDTO courseDTO = new CourseDetailManagerDTO();
			courseDTO.setCourseId(course.getCourseId());
			courseDTO.setName(course.getName());
			courseDTO.setStatus(course.getStatus());
			courseDTO.setDescription(course.getDescription());
			courseDTO.setAvatar(course.getAvatar());
			courseDTO.setPrice(course.getPrice());
			courseDTO.setTopic(course.getTopic());
			courseDTO.setAvatar(course.getAvatar());
//				courseDTO.setCategoryId(course.getCategory().getCategoryId());
			courseDTO.setLevelId(course.getCourseLevel().getCourseLevelId());
			return ResponseEntity.ok(courseDTO);
		}
		return ResponseEntity.ok("Chi tiết khóa học");
	}

	//Cập nhật khóa học + danh sách danh mục + ảnh đại diện mới (nếu có)
	@PutMapping(value = "/update-course/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateCourseDetailInfo(@PathVariable("courseId") int courseId,
			@RequestParam("name") Optional<String> name,
			@RequestParam("status") Optional<Integer> status, @RequestParam("description") Optional<String> description,
			@RequestParam("avatar") Optional<String> avatar, @RequestParam("price") Optional<Float> price,
			@RequestParam("topic") Optional<String> topic, @RequestParam("listCategoryId") List<Integer> listCategoryId,
			@RequestParam("levelId") Optional<Integer> levelId,
			@RequestParam(value = "file", required = false) MultipartFile file)
			throws IOException, java.io.IOException {

		// Bắt lỗi trống
		// Bắt lỗi trạng thái nếu đang công khai thì không hiển thị

		if (file != null && !file.isEmpty()) {
			MultipartFile fileUpToCloudinary = file;
			Map<?, ?> data = this.cloudinaryService.upload(fileUpToCloudinary);
			avatar = Optional.ofNullable((String) data.get("url").toString());
		}

		try {
			CourseDetailManagerDTO courseDTO = new CourseDetailManagerDTO();
			courseDTO.setCourseId(courseId);
			courseDTO.setName(name.orElse(null));
			courseDTO.setStatus(status.orElse(0));
			courseDTO.setDescription(description.orElse(null));
			courseDTO.setAvatar(avatar.orElse(null));
			courseDTO.setPrice(price.orElse((float) 0));
			courseDTO.setTopic(topic.orElse(null));
			courseDTO.setCategoryIds(listCategoryId);
			courseDTO.setLevelId(levelId.orElse(null));
			return ResponseEntity.ok(courseService.luuThongTinKhoaHoc(courseDTO));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	
	//Xóa phần của khóa học
	@DeleteMapping("/{courseId}/remove-section/{sectionId}")
	public ResponseEntity<?> removSection(@PathVariable("courseId") int courseId, @PathVariable("sectionId") int sectionId) {
		System.out.println(sectionId);
		try {
			sectionService.removeSection(sectionId);
			return ResponseEntity.ok("{\"message\": \"Xóa khóa học thành công!\"}");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Có lỗi xảy ra: " + e.getMessage());
		}
	}
}
