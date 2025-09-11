package com.fpoly.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fpoly.entity.Category;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseCategory;
import com.fpoly.entity.CourseCategoryId;

import com.fpoly.dto.CourseDetailManagerDTO;
import com.fpoly.dto.CourseDetailSearchDTO;
import com.fpoly.dto.CourseDetailSearchCategoryDTO;
import com.fpoly.dto.CourseDetailSearchSectionDTO;
import com.fpoly.dto.CourseNameSuggestionDTO;
import com.fpoly.dto.CourseSearchResponseDTO;
import com.fpoly.dto.FunFactDTO;
import com.fpoly.dto.learning.CourseDetailDTO;
import com.fpoly.dto.learning.LessonDTO;
import com.fpoly.dto.learning.SectionDTO;
import com.fpoly.dto.learning.TestDTO;
import com.fpoly.entity.CourseLevel;
import com.fpoly.entity.Lesson;
import com.fpoly.entity.Section;
import com.fpoly.entity.Test;
import com.fpoly.entity.User;
import com.fpoly.entity.Voucher;
import com.fpoly.repository.CartRepository;
import com.fpoly.repository.CategoryRepository;
import com.fpoly.repository.CommentRepository;
import com.fpoly.repository.CourseCategoryRepository;
import com.fpoly.repository.CourseLevelRepository;
import com.fpoly.repository.CourseRepository;
import com.fpoly.repository.CourseRepositoryCustom;
import com.fpoly.repository.LessonRepository;
import com.fpoly.repository.RegisteredCourseRepository;
import com.fpoly.repository.SectionRepository;
import com.fpoly.repository.TestRepository;
import com.fpoly.repository.UserRepository;
import com.fpoly.repository.VoucherRepository;
import com.fpoly.security.JwtTokenUtils;

@Service
public class CourseService {
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private CourseRepositoryCustom courseRepositoryCustom;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private CategoryRepository categoryRepo;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private CourseLevelRepository levelRepo;
	@Autowired
	private SectionRepository sectionRepository;
	@Autowired
	private LessonRepository lessonRepository;
	@Autowired
	private TestRepository testRepository;
	@Autowired
	private RegisteredCourseRepository registeredCourseRepository;
	@Autowired
	private CourseCategoryRepository courseCategoryRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtTokenUtils jwtTokenUtils;

//Home Page
	// Funfact
	public FunFactDTO getFunFact() {
		FunFactDTO funFactDTO = new FunFactDTO();
		funFactDTO.setTotalUsers(userRepository.countByRoleName("USER"));
		funFactDTO.setTotalCourses(courseRepository.countTotalCoursePublic());
		funFactDTO.setTotalCategories(categoryRepo.countTotalCategory());
		funFactDTO.setAverageRating(courseRepository.countStarAVG());
		return funFactDTO;
	}

	// Top 4 khóa học được đăng ký nhiều nhất
	public List<Course> getTop4RegisteredCourses() {
		Pageable pageable = PageRequest.of(0, 4);
		return courseRepository.getTopRegisteredCourses(pageable);
	}

	// Top 4 khóa học được đánh giá cao nhất
	public List<Course> getTopRatedCourses() {
		return courseRepository.findTopRatedCourses();
	}

//Header 
	// Gợi ý tên khóa học
	public List<CourseNameSuggestionDTO> getCourseNameSuggestions(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return List.of(); // Trả về danh sách rỗng nếu không có từ khóa
		}
		return courseRepository.findCourseNamesByKeyword(keyword.trim());
	}

//SearchCourse Page
	// Tìm kiếm khóa học có phân trang
	public Page<CourseSearchResponseDTO> searchCoursesWithFilters(String email, String category, String courseName,
			Boolean free, Float minPrice, Float maxPrice, Integer ratedStar, Integer levelId, Boolean priceASC,
			Boolean priceDESC, int page, int size) {
		Pageable pageable;
		if (Boolean.TRUE.equals(priceASC)) {
			pageable = PageRequest.of(page, size, Sort.by("price").ascending());
		} else if (Boolean.TRUE.equals(priceDESC)) {
			pageable = PageRequest.of(page, size, Sort.by("price").descending());
		} else {
			pageable = PageRequest.of(page, size,
					Sort.by("averageRating").descending().and(Sort.by("follow").descending()));
		}

		// Gọi repository tùy chỉnh để thực hiện truy vấn động
		Page<Course> coursePage = courseRepositoryCustom.searchCourses(category, courseName, free, minPrice, maxPrice,
				ratedStar, levelId, pageable);

		// Khai báo biến final để sử dụng trong lambda
		final Set<Integer> registeredCourseIds;

		if (email != null) {
			User user = userService.getUserByEmailToan(email);
			registeredCourseIds = registeredCourseRepository.findCourseIdsByUserId(user.getUserId());
		} else {
			registeredCourseIds = Collections.emptySet(); // không đăng nhập thì set rỗng
		}

		// Trả về kết quả sau khi map sang DTO
		return coursePage.map(course -> {
			boolean isRegistered = registeredCourseIds.contains(course.getCourseId());
			return new CourseSearchResponseDTO(course.getCourseId(), course.getName(), course.getAvatar(),
					course.getPrice(), course.getAverageRating(), course.getFollow(), isRegistered);
		});
	}

//CourseDetailPage
	public CourseDetailSearchDTO getCourseDetail(int courseId, String token) {
		Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

		int totalComments = commentRepository.countByCourse_CourseId(courseId);
		int totalRegistered = registeredCourseRepository.countByCourse_CourseIdAndStatusPaymentTrue(courseId);

		List<CourseDetailSearchCategoryDTO> categories = courseCategoryRepository.findByCourse_CourseId(courseId)
				.stream()
				.map(cc -> new CourseDetailSearchCategoryDTO(cc.getCategory().getSlug(), cc.getCategory().getName()))
				.collect(Collectors.toList());

		List<CourseDetailSearchSectionDTO> sections = sectionRepository.findByCourse_CourseId(courseId).stream()
				.map(section -> new CourseDetailSearchSectionDTO(section.getName(), section.getDescription(),
						section.getContentDescription()))
				.collect(Collectors.toList());

		// Xử lý trạng thái thanh toán
		boolean isPaid = false;
		boolean isInCart = false;
		if (token != null && token.startsWith("Bearer ")) {
			try {
				String jwt = token.replace("Bearer ", "").trim();
				String email = jwtTokenUtils.extractEmail(jwt);
				User user = userService.getUserByEmailToan(email);
				isPaid = registeredCourseRepository
						.existsByUser_UserIdAndCourse_CourseIdAndStatusPaymentTrue(user.getUserId(), courseId);
				isInCart = cartRepository.existsByUser_UserIdAndCourse_CourseId(user.getUserId(), courseId);
			} catch (Exception e) {
				System.err.println("Lỗi xử lý token: " + e.getMessage());
			}
		}

		return new CourseDetailSearchDTO(course.getCourseId(), course.getAvatar(), course.getName(),
				course.getAverageRating(), course.getDescription(), course.getContentDescription(), course.getPrice(),
				course.getCourseLevel() != null ? course.getCourseLevel().getName() : null, totalComments,
				totalRegistered, isPaid, // boolean
				isInCart, categories, sections);

	}

	// Tìm kiếm khóa học liên quan
	   public List<Course> getRelatedCourses(int courseId) {
	        Course currentCourse = courseRepository.findById(courseId)
	                .orElseThrow(() -> new RuntimeException("Course not found"));

	        List<Course> result = new ArrayList<>();
	        int limit = 10;

	        // 1. Lấy theo danh mục
	        List<Integer> categoryIds = currentCourse.getCourseCategories()
	                .stream()
	                .map(cc -> cc.getCategory().getCategoryId())
	                .collect(Collectors.toList());

	        if (!categoryIds.isEmpty()) {
	            result.addAll(courseRepository.findRelatedByCategories(categoryIds, courseId));
	        }

	        // 2. Nếu chưa đủ -> tìm theo tên
	        if (result.size() < limit) {
	            List<Course> byName = courseRepository.findByNameLike(currentCourse.getName(), courseId);
	            // Loại bỏ trùng
	            byName.removeAll(result);
	            result.addAll(byName);
	        }

	        // 3. Nếu vẫn chưa đủ -> fallback: lấy top popular
	        if (result.size() < limit) {
	            List<Course> popular = courseRepository.findTopPopularCourses(PageRequest.of(0, limit * 2));
	            // Loại bỏ trùng
	            popular.removeAll(result);
	            result.addAll(popular);
	        }

	        // Trả về đúng 10 hoặc ít hơn nếu DB không còn
	        return result.stream().limit(limit).collect(Collectors.toList());
	    }

//Learning
	public CourseDetailDTO getCourseDetail(int courseId) {
		Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Not found"));

		List<SectionDTO> sectionDTOs = course.getListSection().stream().map(section -> {
			List<LessonDTO> lessons = section.getListLesson().stream().map(l -> new LessonDTO(l.getLessonId(),
					l.getName(), l.getDescription(), null, l.getLessionDuration(), null, false)).toList();

			List<TestDTO> tests = section.getListTest().stream()
					.map(t -> new TestDTO(t.getTestId(), t.getTitle(), null, null, 0)).toList();

			return new SectionDTO(section.getSectionId(), section.getName(), lessons, tests);
		}).toList();

		return new CourseDetailDTO(course.getCourseId(), course.getName(), course.getTopic(), course.getDescription(),
				sectionDTOs);
	}

//Method cũ

	public Optional<Course> timKhoaHocTheoMaKhoaHocHuy(int courseId) {
		return courseRepository.findById(courseId);
	}

	public Course timKhoaHocTheoMaKhoaHocToan(int courseId) {
		return courseRepository.findByCourseId(courseId);
	}

	public List<Course> getAllCourse() {
		return courseRepository.findAll();
	}

	public Optional<Course> timKhoaHocTheoMaKhoaHocTam(int courseId) {
		return courseRepository.findById(courseId);
	}

	// HBảo code
	// LẤY DANH SÁCH KHÓA HỌC KHI NGƯỜI DÙNG ĐÃ ĐĂNG NHẬP VÀ LOẠI BỎ CÁC KHÓA HỌC
	// - 1. LẤY DANH SÁCH KHÓA HỌC CÓ TRẠNG THÁI Status = 1
	// + LẤY DANH SÁCH KHÓA TẤT CẢ KHÓA HỌC KHI NGƯỜI DÙNG "CHƯA ĐĂNG NHẬP"
	public List<Course> getAllCourseWithSatus() {
		return courseRepository.getAllCourseWithSatus();
	}

	// + LẤY DANH SÁCH KHÓA HỌC KHI NGƯỜI DÙNG "ĐÃ ĐĂNG NHẬP" VÀ LOẠI BỎ CÁC KHÓA
	public List<Course> getUnregisteredCourse(int userId) {
		return courseRepository.findUnregisteredCourse(userId);
	}

	// - 2. TÌM KIẾM KHÓA HỌC THEO TÊN NGƯỜI DÙNG NHẬP CÓ TRẠNG THÁI Status = 1
	// + TÌM KIẾM KHÓA HỌC THEO TÊN NGƯỜI DÙNG NHẬP KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> searchCoursesByName(String name) {
		return courseRepository.findCourseTheoTen(name);
	}

	// + TÌM KIẾM KHÓA HỌC THEO TÊN NGƯỜI DÙNG NHẬP KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> searchCoursesByNameLoginTrue(int userId, String name) {
		return courseRepository.findCourseTheoTenLoginTrue(userId, name);
	}

	// - 3. TÌM KIẾM KHÓA HỌC MIỄN PHÍ VỚI TRẠNG THÁI Status = 1
	// + TÌM KIẾM KHÓA HỌC MIỄN PHÍ KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCourseMienPhi() {
		return courseRepository.findCourseMienPhi();
	}

	// + TÌM KIẾM KHÓA HỌC MIỄN PHÍ KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCourseMienPhi(int userId) {
		return courseRepository.findCourseMienPhi(userId);
	}

	// - 4. TÌM KIẾM KHÓA HỌC CÓ PHÍ VỚI TRẠNG THÁI Status = 1
	public List<Course> findCourseCoPhi() {
		return courseRepository.findCourseCoPhi();
	}

	// - 5. TÌM KIẾM KHÓA HỌC CÓ GIÁ > 500 VỚI TRẠNG THÁI Status = 1
	// + TÌM KIẾM KHÓA HỌC CÓ GIÁ > 500 KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCourseGiaLon() {
		return courseRepository.findCourseGiaLon();
	}

	// + TÌM KIẾM KHÓA HỌC CÓ GIÁ > 500 KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCourseGiaLon(int userId) {
		return courseRepository.findCourseGiaLon(userId);
	}

	// - 6. TÌM KIẾM KHÓA HỌC CÓ GIÁ < 500 VỚI TRẠNG THÁI Status = 1
	// + TÌM KIẾM KHÓA HỌC CÓ GIÁ < 500 KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCourseGiaBe() {
		return courseRepository.findCourseGiaBe();
	}

	// + TÌM KIẾM KHÓA HỌC CÓ GIÁ < 500 KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCourseGiaBe(int userId) {
		return courseRepository.findCourseGiaBe(userId);
	}

	// - 7. TÌM KIẾM KHÓA HỌC THEO KHOẢNG GIÁ TỪ MAX ĐẾN MIN
	// + TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCourseGiaMinMax(double minPrice, double maxPrice) {
		return courseRepository.findCourseGiaMinMax(minPrice, maxPrice);
	}

	// + TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCourseGiaMinMax(int userId, double minPrice, double maxPrice) {
		return courseRepository.findCourseGiaMinMax(userId, minPrice, maxPrice);
	}

	// - 8. TÌM KIẾM KHÓA HỌC THEO ĐÁNH GIÁ VỚI TRẠNG THÁI Status = 1
	// - 8.1 TÌM KHÓA HỌC VỚI ĐÁNH GIÁ BẰNG 5
	// + TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCourseAverageRating5() {
		return courseRepository.findCourseAverageRating5();
	}

	// + TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCourseAverageRating5(int userId) {
		return courseRepository.findCourseAverageRating5(userId);
	}

	// - 8.2 TÌM KHÓA HỌC VỚI ĐÁNH GIÁ TỪ 4 ĐẾN 5
	// + TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCourseAverageRating4() {
		return courseRepository.findCourseAverageRating4();
	}

	// + TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCourseAverageRating4(int userId) {
		return courseRepository.findCourseAverageRating4(userId);
	}

	// - 8.3 TÌM KHÓA HỌC VỚI ĐÁNH GIÁ TỪ 3 ĐẾN 5
	// + TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCourseAverageRating3() {
		return courseRepository.findCourseAverageRating3();
	}

	// + TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCourseAverageRating3(int userId) {
		return courseRepository.findCourseAverageRating3(userId);
	}

	// - 8.4 TÌM KHÓA HỌC VỚI ĐÁNH GIÁ TỪ 2 ĐẾN 5
	// + TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCourseAverageRating2() {
		return courseRepository.findCourseAverageRating2();
	}

	// + TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCourseAverageRating2(int userId) {
		return courseRepository.findCourseAverageRating2(userId);
	}

	// - 9. TÌM DANH MỤC KHÓA HỌC THEO CATEGORY_ID VÀ TRẠNG THÁI status = 1
	// - 9.1 LOAD DANH MỤC
	public List<Category> getAllCategory() {
		return categoryRepository.findAll();
	}

	// - 9.2 TÌM KHÓA HỌC THEO Category_Id
	// + TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
//	public List<Course> getCoursesByCategoryId(int categoryId) {
//		return courseRepository.findCourseByCategoryId(categoryId);
//	}

	// + TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
//	public List<Course> getCoursesByCategoryId(int userId, int categoryId) {
//		return courseRepository.findCourseByCategoryId(userId, categoryId);
//	}

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCoursesByFollow(int userId) {
		return courseRepository.findCoursesByFollow(userId);
	}

	// - 11. SẮP XẾP KHÓA HỌC THEO ĐÁNH GIÁ VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCoursesByAverageRatingDesc() {
		return courseRepository.findCoursesByAverageRatingDesc();
	}

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCoursesByAverageRatingDesc(int userId) {
		return courseRepository.findCoursesByAverageRatingDesc(userId);
	}

	// - 12. SẮP XẾP KHÓA HỌC THEO GIÁ TĂNG DẦN VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCoursesByPriceAsc() {
		return courseRepository.findCoursesByPriceAsc();
	}

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCoursesByPriceAsc(int userId) {
		return courseRepository.findCoursesByPriceAsc(userId);
	}

	// - 13. SẮP XẾP KHÓA HỌC THEO GIÁ GIẢM DẦN VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	public List<Course> findCoursesByPriceDesc() {
		return courseRepository.findCoursesByPriceDesc();
	}

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	public List<Course> findCoursesByPriceDesc(int userId) {
		return courseRepository.findCoursesByPriceDesc(userId);
	}

	// - 14. TÌM KIẾM KHÓA HỌC TRONG KHÓA HỌC ĐÃ ĐĂNG KÝ
	public List<Course> findCoursesOnRegisteredCourse(int courseId, int userId) {
		return courseRepository.findCoursesOnRegisteredCourse(courseId, userId);
	}

	// TEST ĐÃ MUA
	public boolean checkIfRegistered(int courseId, int userId) {
		return registeredCourseRepository.existsByCourseIdAndUserId(courseId, userId);
	}

	// ------------------------- HẾT CODE CỦA HBAO ------------------------

	public List<Course> getHashTagsByCourseId(int hashTagId) {
		return courseRepository.findCoursesByHashTag(hashTagId);
	}

	public Course hienThiKhoaHocTheoIdHao(int id) {
		return courseRepository.findByCourseId(id);
	}

//	public List<Course> findCoursesByCategory(String categoryName) {
//		return courseRepository.findByCategoryNameIgnoreCase(categoryName);
//	}

//	public List<Course> getCoursesByCategory(Category category) {
//		return courseRepository.findByCategory(category);
//	}

	public List<Course> hienThiKhoaHocTheoTrangThaiKhoaHoc_Huy(List<Integer> trangThai) {
		return courseRepository.findByStatusIn(trangThai);
	}

	// Tìm kiếm khóa học mới được tạo
	public Course getLatestCourse() {
		return courseRepository.findCourseWithMaxId();
	}

	public Course themKhoaHocMoi_Huy(Course course) {
		return courseRepository.save(course);
	}

//	public CourseDetailManagerDTO luuThongTinKhoaHoc(CourseDetailManagerDTO courseDTO) {
//		Course courseEntity = new Course();
//		courseEntity.setCourseId(courseDTO.getCourseId());
//		courseEntity.setName(courseDTO.getName());
//		courseEntity.setStatus(courseDTO.getStatus());
//		courseEntity.setDescription(courseDTO.getDescription());
//		courseEntity.setAvatar(courseDTO.getAvatar());
//		courseEntity.setPrice(courseDTO.getPrice());
//		courseEntity.setTopic(courseDTO.getTopic());
//		Category category = categoryRepo.findByCategoryId(courseDTO.getCategoryId());
////		courseEntity.setCategory(category);
//		CourseLevel level = levelRepo.findById(courseDTO.getLevelId()).orElse(null);
//		courseEntity.setCourseLevel(level);
//		courseEntity.setUpdateAt(new Date());
//		courseRepository.save(courseEntity);
//		// Trả về DTo cho để hiển thị
//		return courseDTO;
//	}

	@Transactional
	public CourseDetailManagerDTO luuThongTinKhoaHoc(CourseDetailManagerDTO courseDTO) {
		Course courseEntity;

		if (courseDTO.getCourseId() != 0) {
			courseEntity = courseRepository.findById(courseDTO.getCourseId())
					.orElseThrow(() -> new RuntimeException("Course not found"));
		} else {
			courseEntity = new Course();
			courseEntity.setCreateAt(new Date());
		}

		courseEntity.setName(courseDTO.getName());
		courseEntity.setStatus(courseDTO.getStatus());
		courseEntity.setDescription(courseDTO.getDescription());
		courseEntity.setContentDescription(courseDTO.getContentDescription());
		courseEntity.setAvatar(courseDTO.getAvatar());
		courseEntity.setPrice(courseDTO.getPrice());
		courseEntity.setTopic(courseDTO.getTopic());
		courseEntity.setUpdateAt(new Date());

		// Level
		CourseLevel level = levelRepo.findById(courseDTO.getLevelId()).orElse(null);
		courseEntity.setCourseLevel(level);

		// Lưu course
		Course savedCourse = courseRepository.save(courseEntity);

		// === Xử lý category ===
		List<Integer> inputCategoryIds = courseDTO.getCategoryIds();
		if (inputCategoryIds != null) {
			// Lấy danh sách các category từ DB
			List<Category> newCategories = categoryRepo.findAllById(inputCategoryIds);

			// Lấy danh sách courseCategory hiện tại từ DB
			List<CourseCategory> currentCourseCategories = courseCategoryRepository.findByCourse(savedCourse);

			// Danh sách categoryId hiện có
			Set<Integer> existingCategoryIds = currentCourseCategories.stream()
					.map(cc -> cc.getCategory().getCategoryId()).collect(Collectors.toSet());

			// Xóa các course_category không còn nằm trong danh sách mới
			for (CourseCategory cc : currentCourseCategories) {
				if (!inputCategoryIds.contains(cc.getCategory().getCategoryId())) {
					courseCategoryRepository.delete(cc);
				}
			}

			// Thêm mới nếu chưa có
			for (Category category : newCategories) {
				if (!existingCategoryIds.contains(category.getCategoryId())) {
					CourseCategory cc = new CourseCategory();
					cc.setId(new CourseCategoryId(savedCourse.getCourseId(), category.getCategoryId()));
					cc.setCourse(savedCourse);
					cc.setCategory(category);
					courseCategoryRepository.save(cc);
				}
			}
		}

		return courseDTO;
	}

	public List<Course> getTopFollowCourses() {
		return courseRepository.findTop4ByStatusOrderByFollowDesc(1);
	}

	// Xóa khóa học nháp
	@Transactional
	public void removeDraftCourse(int courseId) {
		// Tìm khóa học
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));

		// Xóa tất cả các phần liên quan đến khóa học
		for (Section section : course.getListSection()) {
			// Xóa tất cả các bài học trong phần
			for (Lesson lesson : section.getListLesson()) {
				lessonRepository.delete(lesson);
			}
			// Xóa tất cả các bài kiểm tra trong phần
			for (Test test : section.getListTest()) {
				testRepository.delete(test);
			}
			// Xóa phần
			sectionRepository.delete(section);
		}

		// Cuối cùng, xóa khóa học
		courseRepository.delete(course);
	}

}
