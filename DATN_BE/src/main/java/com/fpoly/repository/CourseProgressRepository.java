package com.fpoly.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.Answer;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.User;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, Integer> {

//Learning Page
	Optional<CourseProgress> findByCourse(Course course);

//Quiz Page
	// Tìm tiến độ học của 1 user trong 1 course
	Optional<CourseProgress> findByUser_UserIdAndCourse_CourseId(int userId, int courseId);

	// Nếu cần lấy nhiều record (không thường dùng vì user-course chỉ nên có 1 tiến
	// độ)
	List<CourseProgress> findAllByUser_UserId(int userId);

	List<CourseProgress> findAllByCourse_CourseId(int courseId);

//MyCourse Page 
	CourseProgress findByUserAndCourse(User user, Course course);

//Dashboard
	/**
	 * Đếm số lượng CourseProgress đã hoàn thành (progressPercentage >= 100) trong
	 * một khoảng thời gian cụ thể (dựa vào thời gian tạo course progress, nhưng vì
	 * CourseProgress không có createAt, ta sẽ dựa vào thời gian tạo Course hoặc giả
	 * định thêm createAt vào CourseProgress). * GIẢ ĐỊNH: CourseProgress có trường
	 * 'createAt' để lọc thời gian, hoặc ta sẽ lọc dựa trên thời gian đăng ký khóa
	 * học (RegisteredCourse.createAt). * Vì CourseProgress là trạng thái của User
	 * với Course, ta sử dụng cú pháp đơn giản nhất dựa trên progressPercentage và
	 * giả định CourseProgress.createAt
	 */
	@Query("SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.progressPercentage >= 100.0 AND cp.user.createAt >= :startDate AND cp.user.createAt < :endDate")
	long countCompletedCourses(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

//Khác
	@Query("SELECT cp FROM CourseProgress cp JOIN cp.course ce JOIN cp.user u WHERE	 u.userId = :userId")
	List<CourseProgress> FillCourseKhoa(int userId);

	CourseProgress findByCourseProgressId(int id);

	// Code của HBao
	@Query("SELECT c FROM CourseProgress c WHERE c.progressPercentage = 100")
	List<CourseProgress> FillTotalCourseComplete();

	@Query("SELECT c FROM CourseProgress c WHERE c.progressPercentage = 100 AND c.progressStatus = 1")
	List<CourseProgress> FillTotalCourseCompleteCuaBao();

	//
	@Query("SELECT cp FROM CourseProgress cp WHERE cp.user.userId = :userId AND cp.course.courseId = :courseId")
	Optional<CourseProgress> findByCourseId(@Param("userId") int userId, @Param("courseId") int courseId);

	Optional<CourseProgress> findByCourse_CourseIdAndUser_UserId(int courseId, int userId);
}
