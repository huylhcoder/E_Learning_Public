package com.fpoly.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.dto.CourseNameSuggestionDTO;
import com.fpoly.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

//Home Page
	// Tổng số khóa học + trạng thái đã đăng
	@Query("SELECT COUNT(c.courseId) FROM Course c WHERE c.status = 1")
	Long countTotalCoursePublic();

	// Top 4 khóa học
	@Query("SELECT c FROM Course c WHERE c.status = 1 AND c.follow IS NOT NULL ORDER BY c.follow DESC")
	List<Course> getTopRegisteredCourses(Pageable pageable);

//Header
	// Gợi ý tên khóa học + chỉ gợi ý khóa học với trạng thái công khai
	@Query("SELECT new com.fpoly.dto.CourseNameSuggestionDTO(c.name) " + "FROM Course c "
			+ "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " + "AND c.status = 1")
	List<CourseNameSuggestionDTO> findCourseNamesByKeyword(@Param("keyword") String keyword);

	// ------------ CÂU TRUY VẤN SQL CỦA HBảo ----------------------------------
	// 1. LẤY DANH SÁCH KHÓA HỌC CÓ TRẠNG THÁI Status = 1
	// LẤY DANH SÁCH KHÓA TẤT CẢ KHÓA HỌC KHI NGƯỜI DÙNG "CHƯA ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c WHERE c.status = 1")
	List<Course> getAllCourseWithSatus();

	// LẤY DANH SÁCH KHÓA HỌC KHI NGƯỜI DÙNG "ĐÃ ĐĂNG NHẬP" VÀ LOẠI BỎ CÁC KHÓA HỌC
	// NGƯỜI DÙNG ĐÃ ĐĂNG KÝ
	@Query("SELECT c FROM Course c LEFT JOIN RegisteredCourse rc ON c.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.user.id IS NULL AND c.status = 1")
	List<Course> findUnregisteredCourse(@Param("userId") int userId);

	// 2. TÌM KIẾM KHÓA HỌC THEO TÊN NGƯỜI DÙNG NHẬP CÓ TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC THEO TÊN NGƯỜI DÙNG NHẬP KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c WHERE c.name LIKE CONCAT('%', :name, '%') AND c.status = 1")
	List<Course> findCourseTheoTen(@Param("name") String name);

	// TÌM KIẾM KHÓA HỌC THEO TÊN NGƯỜI DÙNG NHẬP KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c LEFT JOIN RegisteredCourse rc ON c.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND c.name LIKE CONCAT('%', :name, '%') AND c.status = 1")
	List<Course> findCourseTheoTenLoginTrue(@Param("userId") int userId, @Param("name") String name);

	// 3. TÌM KIẾM KHÓA HỌC MIỄN PHÍ VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC MIỄN PHÍ KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh WHERE kh.price = 0 AND kh.status = 1")
	List<Course> findCourseMienPhi();

	// TÌM KIẾM KHÓA HỌC MIỄN PHÍ KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh LEFT JOIN RegisteredCourse rc ON kh.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND kh.price = 0 AND kh.status = 1")
	List<Course> findCourseMienPhi(@Param("userId") int userId);

	// 4. TÌM KIẾM KHÓA HỌC CÓ PHÍ VỚI TRẠNG THÁI Status = 1
	@Query("SELECT kh FROM Course kh WHERE kh.price > 0 AND kh.status = 1")
	List<Course> findCourseCoPhi();

	// 5. TÌM KIẾM KHÓA HỌC CÓ GIÁ > 500 VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC CÓ GIÁ > 500 KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh WHERE kh.price > 500000 AND kh.status = 1")
	List<Course> findCourseGiaLon();

	// TÌM KIẾM KHÓA HỌC CÓ GIÁ > 500 KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh LEFT JOIN RegisteredCourse rc ON kh.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND kh.price > 500000 AND kh.status = 1")
	List<Course> findCourseGiaLon(@Param("userId") int userId);

	// 6. TÌM KIẾM KHÓA HỌC CÓ GIÁ < 500 VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC CÓ GIÁ < 500 KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh WHERE kh.price < 500000 AND kh.status = 1")
	List<Course> findCourseGiaBe();

	// TÌM KIẾM KHÓA HỌC CÓ GIÁ < 500 KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh LEFT JOIN RegisteredCourse rc ON kh.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND kh.price < 500000 AND kh.status = 1")
	List<Course> findCourseGiaBe(@Param("userId") int userId);

	// 7. TÌM KIẾM KHÓA HỌC THEO KHOẢNG GIÁ TỪ MAX ĐẾN MIN
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh WHERE kh.status = 1 AND kh.price BETWEEN ?1 AND ?2")
	List<Course> findCourseGiaMinMax(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh LEFT JOIN RegisteredCourse rc ON kh.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND kh.status = 1 AND kh.price BETWEEN :minPrice AND :maxPrice")
	List<Course> findCourseGiaMinMax(@Param("userId") int userId, @Param("minPrice") double minPrice,
			@Param("maxPrice") double maxPrice);

	// 8. TÌM KIẾM KHÓA HỌC THEO ĐÁNH GIÁ VỚI TRẠNG THÁI Status = 1
	// 8.1 TÌM KHÓA HỌC VỚI ĐÁNH GIÁ BẰNG 5
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh WHERE kh.averageRating = 5 AND kh.status = 1")
	List<Course> findCourseAverageRating5();

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh LEFT JOIN RegisteredCourse rc ON kh.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND kh.averageRating = 5 AND kh.status = 1")
	List<Course> findCourseAverageRating5(@Param("userId") int userId);

	// 8.2 TÌM KHÓA HỌC VỚI ĐÁNH GIÁ TỪ 4 ĐẾN 5
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh WHERE kh.averageRating >= 4 AND kh.status = 1")
	List<Course> findCourseAverageRating4();

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh LEFT JOIN RegisteredCourse rc ON kh.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND kh.averageRating >= 4 AND kh.status = 1")
	List<Course> findCourseAverageRating4(@Param("userId") int userId);

	// 8.3 TÌM KHÓA HỌC VỚI ĐÁNH GIÁ TỪ 3 ĐẾN 5
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh WHERE kh.averageRating >= 3 AND kh.status = 1")
	List<Course> findCourseAverageRating3();

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh LEFT JOIN RegisteredCourse rc ON kh.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND kh.averageRating >= 3 AND kh.status = 1")
	List<Course> findCourseAverageRating3(@Param("userId") int userId);

	// 8.4 TÌM KHÓA HỌC VỚI ĐÁNH GIÁ TỪ 2 ĐẾN 5
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh WHERE kh.averageRating >= 2 AND kh.status = 1")
	List<Course> findCourseAverageRating2();

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT kh FROM Course kh LEFT JOIN RegisteredCourse rc ON kh.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND kh.averageRating >= 2 AND kh.status = 1")
	List<Course> findCourseAverageRating2(@Param("userId") int userId);

	// 9 TÌM DANH MỤC KHÓA HỌC THEO CATEGORY_ID VÀ TRẠNG THÁI status = 1
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
//	@Query("SELECT c FROM Course c JOIN c.category ct WHERE ct.categoryId = :categoryId AND c.status = 1")
//	List<Course> findCourseByCategoryId(@Param("categoryId") int categoryId);

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
//	@Query("SELECT c FROM Course c LEFT JOIN RegisteredCourse rc ON c.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND c.category.categoryId = :categoryId AND c.status = 1")
//	List<Course> findCourseByCategoryId(@Param("userId") int userId, @Param("categoryId") int categoryId);

	// 10 SẮP XẾP KHÓA HỌC LƯỢT THEO ĐĂNG KÝ (THEO DÕI) VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
//	@Query("SELECT TOP 4 c FROM Course c WHERE  c.status = 1 AND c.follow IS NOT NULL ORDER BY c.follow DESC")
//	List<Course> getTopRegisteredCourses();

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c LEFT JOIN RegisteredCourse rc ON c.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND c.status = 1 AND c.follow IS NOT NULL ORDER BY c.follow DESC")
	List<Course> findCoursesByFollow(@Param("userId") int userId);

	// 11 SẮP XẾP KHÓA HỌC THEO ĐÁNH GIÁ VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c WHERE c.status = 1 ORDER BY c.averageRating DESC")
	List<Course> findCoursesByAverageRatingDesc();

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c LEFT JOIN RegisteredCourse rc ON c.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND c.status = 1 ORDER BY c.averageRating DESC")
	List<Course> findCoursesByAverageRatingDesc(@Param("userId") int userId);

	// 12 SẮP XẾP KHÓA HỌC THEO GIÁ TĂNG DẦN VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c WHERE c.status = 1 ORDER BY c.price ASC")
	List<Course> findCoursesByPriceAsc();

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c LEFT JOIN RegisteredCourse rc ON c.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND c.status = 1 ORDER BY c.price ASC")
	List<Course> findCoursesByPriceAsc(@Param("userId") int userId);

	// 13 SẮP XẾP KHÓA HỌC THEO GIÁ GIẢM DẦN VỚI TRẠNG THÁI Status = 1
	// TÌM KIẾM KHÓA HỌC KHI "CHƯA ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c WHERE c.status = 1 ORDER BY c.price DESC")
	List<Course> findCoursesByPriceDesc();

	// TÌM KIẾM KHÓA HỌC KHI "ĐÃ ĐĂNG NHẬP"
	@Query("SELECT c FROM Course c LEFT JOIN RegisteredCourse rc ON c.courseId = rc.course.courseId AND rc.user.id = :userId WHERE rc.course IS NULL AND c.status = 1 ORDER BY c.price DESC")
	List<Course> findCoursesByPriceDesc(@Param("userId") int userId);

	// 14 TÌM KIẾM KHÓA HỌC TRONG KHÓA HỌC ĐÃ ĐĂNG KÝ
	@Query("SELECT c FROM Course c " + "JOIN c.listRegisteredCourse rc " + "WHERE rc.course.courseId = :courseId "
			+ "AND rc.user.userId = :userId " + "AND rc.statusPayment = true ")
	List<Course> findCoursesOnRegisteredCourse(@Param("courseId") int courseId, @Param("userId") int userId);

	// ------------------------- HẾT CODE CỦA HBAO ------------------------

	// HIỂN THỊ DANH SÁCH KHÓA HỌC THEO HASHTAG
	@Query("SELECT htc.course FROM HashTagOfCourse htc WHERE htc.hashTag.hashTagId = :hashTagId")
	List<Course> findCoursesByHashTag(int hashTagId);

//	Course findByCourseId(int CourseId);

	// List<Course> findByCategoryNameIgnoreCase(String categoryName);

	// List<Course> findByCategory(Category category);

	// Cái này của thằng nào làm coi lại ở trên trùng tên nha
	// List<Course> findCoursesByAverageRatingDesc();

	Course findByCourseId(int CourseId);

	List<Course> findByStatusIn(List<Integer> statuses);

	// Tìm kiếm khóa học mới vừa tạo để hiển thị sang trang chi tiết
	@Query("SELECT c FROM Course c WHERE c.courseId = (SELECT MAX(c2.courseId) FROM Course c2)")
	Course findCourseWithMaxId();

	@Query(value = "SELECT TOP 4 * FROM course WHERE average_rating >= 4 AND status = 1 ORDER BY average_rating DESC", nativeQuery = true)
	List<Course> findTopRatedCourses();

	List<Course> findTop4ByStatusOrderByFollowDesc(int status);

	@Query(value = "SELECT CEILING(AVG(c.average_rating) * 10) / 10.0 FROM course c", nativeQuery = true)
	Double countStarAVG();

	@Query("SELECT COUNT(c.name) FROM User c")
	Long countUserNames();

}
