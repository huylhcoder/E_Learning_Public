package com.fpoly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.Answer;
import com.fpoly.entity.Course;
import com.fpoly.entity.Question;
import com.fpoly.entity.Section;
import com.fpoly.entity.User;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {

//CourseDetailPage
	List<Section> findByCourse_CourseId(int courseId);

//Section Manager
	// Cập nhật thời lượng tổng cho toàn khóa học
	@Query("SELECT SUM(s.sectionDuration) FROM Section s WHERE s.course.courseId = :courseId")
	Float sumSectionDurationByCourseId(@Param("courseId") int courseId);

//Các method v1

	List<Section> findByCourse(Course course);

	// Tìm kiếm khóa học mới vừa tạo để hiển thị sang trang chi tiết
	@Query("SELECT c FROM Section c WHERE c.sectionId = (SELECT MAX(c2.sectionId) FROM Section c2)")
	Section findSectionWithMaxId();
}
