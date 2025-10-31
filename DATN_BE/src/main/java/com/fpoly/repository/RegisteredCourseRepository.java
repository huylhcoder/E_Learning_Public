package com.fpoly.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.fpoly.entity.Course;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.Payment;
import com.fpoly.entity.RegisteredCourse;
import com.fpoly.entity.User;

public interface RegisteredCourseRepository extends JpaRepository<RegisteredCourse, Integer> {
//Search Course
	// Hỗ trợ cho tìm kiếm khóa học - hỗ trợ cho course service
	@Query("SELECT rc.course.courseId FROM RegisteredCourse rc WHERE rc.user.userId = :userId AND rc.statusPayment = true")
	Set<Integer> findCourseIdsByUserId(@Param("userId") int userId);

	@Query("SELECT CASE WHEN COUNT(rc) > 0 THEN true ELSE false END FROM RegisteredCourse rc WHERE rc.course.courseId = :courseId AND rc.user.userId = :userId")
	boolean existsByCourseIdAndUserId(@Param("courseId") int courseId, @Param("userId") int userId);

//My Course
	@Query("SELECT cp FROM CourseProgress cp WHERE cp.user.userId = :userId AND cp.course.courseId = :courseId")
	CourseProgress findByUserAndCourse(@Param("userId") int userId, @Param("courseId") int courseId);

//CourseDetail
	// Đếm số lượng học viên của khóa học
	int countByCourse_CourseIdAndStatusPaymentTrue(int courseId);

	// Kiểm tra học viên đã đăng ký khóa học này chưa
	boolean existsByUser_UserIdAndCourse_CourseIdAndStatusPaymentTrue(Integer userId, Integer courseId);

	// Hỗ trợ cho hàm xóa Payment chưa được thanh toán
	@Modifying
	@Transactional
	@Query("DELETE FROM RegisteredCourse rc WHERE rc.payment.paymentId IN :paymentIds")
	void deleteByPaymentIds(@Param("paymentIds") List<Integer> paymentIds);

//Learning
	// Cách 1: Dùng object User và Course
	Optional<RegisteredCourse> findByUserAndCourse(User user, Course course);

	// Cách 2: Dùng nested property (Spring Data JPA hỗ trợ)
	Optional<RegisteredCourse> findByUser_UserIdAndCourse_CourseId(int userId, int courseId);

//Dashboard
	// 1. Dùng @Query để tính tổng doanh thu trong một khoảng thời gian
	@Query("SELECT SUM(rc.price) FROM RegisteredCourse rc WHERE rc.statusPayment = true AND rc.createAt BETWEEN :startDate AND :endDate")
	Float sumRevenueByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	// 2. Dùng @Query để lấy dữ liệu biểu đồ (ví dụ: Doanh thu theo ngày)
	@Query("SELECT FUNCTION('DATE_FORMAT', rc.createAt, '%Y-%m-%d') as date, SUM(rc.price) as revenue "
			+ "FROM RegisteredCourse rc WHERE rc.statusPayment = true AND rc.createAt BETWEEN :startDate AND :endDate "
			+ "GROUP BY date ORDER BY date")
	List<Object[]> findRevenueByDay(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	/**
	 * Truy vấn dữ liệu doanh thu, nhóm theo định dạng thời gian (ngày, tháng). Sử
	 * dụng Native Query hoặc JPQL FUNCTION để định dạng ngày tháng. * @param
	 * startDate Ngày bắt đầu khoảng thời gian
	 * 
	 * @param endDate Ngày kết thúc khoảng thời gian
	 * @param format  Định dạng ngày tháng (ví dụ: '%Y-%m-%d' cho MySQL)
	 * @return Danh sách các Object[], mỗi Object[] chứa [Nhãn_Thời_Gian,
	 *         Tổng_Doanh_Thu]
	 */
//	@Query(value = "SELECT FUNCTION('DATE_FORMAT', rc.createAt, :format) as dateLabel, SUM(rc.price) as revenue "
//			+ "FROM RegisteredCourse rc WHERE rc.statusPayment = true AND rc.createAt >= :startDate AND rc.createAt < :endDate "
//			+ "GROUP BY dateLabel ORDER BY dateLabel", nativeQuery = false) // Sử dụng 'nativeQuery = false' nếu bạn
//																			// dùng JPQL FUNCTION
//	List<Object[]> findRevenueGroupedBy(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
//			@Param("format") String format);
	// Sửa lại Query để sử dụng hàm FORMAT của SQL Server
	@Query(value = "SELECT FUNCTION('FORMAT', rc.createAt, :format) as dateLabel, SUM(rc.price) as revenue "
			+ "FROM RegisteredCourse rc WHERE rc.statusPayment = true AND rc.createAt >= :startDate AND rc.createAt < :endDate "
			+ "GROUP BY dateLabel ORDER BY dateLabel", nativeQuery = false) // nativeQuery = false dùng để sử dụng JPQL
																			// FUNCTION
	List<Object[]> findRevenueGroupedBy(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
			@Param("format") String format);

// Các method khác...
	@Query("SELECT rc FROM RegisteredCourse rc WHERE rc.user.userId = :userId")
	List<RegisteredCourse> findByUser(@Param("userId") int userId);

	@Query("SELECT rc FROM RegisteredCourse rc WHERE rc.user.email = :email")
	List<RegisteredCourse> findByUserEmail(@Param("email") String email);

	@Query("SELECT rc FROM RegisteredCourse rc WHERE rc.user.userId = :userId AND rc.statusPayment = true")
	List<RegisteredCourse> findActiveCoursesByUser(@Param("userId") int userId);

	@Query("SELECT rc FROM RegisteredCourse rc WHERE rc.user.userId = :userId")
	List<RegisteredCourse> findCourseRegisteredByUserId(@Param("userId") int userId);

	// Code Của HBao
	// Lấy danh sách khóa học đã đăng ký tính tổng doanh thu
//	@Query("SELECT rc FROM RegisteredCourse rc JOIN rc.payment p WHERE p.transactionStatus = true")
	@Query("SELECT rc FROM RegisteredCourse rc JOIN rc.course c WHERE c.status = 1 AND rc.statusPayment = true")
	List<RegisteredCourse> GetRegisteredCourse();

	// Biểu đồ doanh thu theo tháng
//	@Query("SELECT rc FROM RegisteredCourse rc WHERE rc.user.id = :userId AND rc.statusPayment = true ORDER BY rc.createAt DESC")
//	List<RegisteredCourse> findByUser(@Param("userId") int userId);
//
//	// danh sách khóa hoạc user đã đăng ký
//	// @Query("SELECT rc FROM RegisteredCourse rc WHERE rc.user.id = :userId")
//	// List<RegisteredCourse> findCourseByUser(@Param("userId") int userId);
//
//	@Query("SELECT rc FROM RegisteredCourse rc JOIN rc.course c WHERE c.status = 1 AND rc.user.id = :userId AND rc.statusPayment = true")
//	List<RegisteredCourse> findActiveCoursesByUser(@Param("userId") int userId);

	// Danh sách các khóa học đã được bán
	@Query("SELECT c.courseId, c.name, c.follow, rc.price, COUNT(rc.course.id)" + "FROM RegisteredCourse rc "
			+ "JOIN rc.course c " + "WHERE c.status = 1 AND rc.statusPayment = true "
			+ "GROUP BY c.courseId, c.name, c.follow, rc.price")
	List<Object[]> findRegisteredCourses();

	// hiển thị đã mua nếu đã mua
//	@Query("SELECT CASE WHEN COUNT(rc) > 0 THEN true ELSE false END " + "FROM RegisteredCourse rc "
//			+ "WHERE rc.course.courseId = :courseId AND rc.user.userId = :userId AND rc.statusPayment = true")
//	boolean existsByCourseIdAndUserId(@Param("courseId") int courseId, @Param("userId") int userId);

	//
//	@Query("SELECT rc FROM RegisteredCourse rc WHERE rc.user.id = :userId")
//	List<RegisteredCourse> findCourseRegisteredByUserId(int userId);
//	//
//
//	List<RegisteredCourse> findByUserEmail(String email);

	List<RegisteredCourse> findByPayment(Payment payment);
}
