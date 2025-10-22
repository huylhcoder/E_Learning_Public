package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.Lesson;
import com.fpoly.entity.Section;
import com.fpoly.entity.Test;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {
	
//Learning
	// Phương thức đếm tổng số Test/Quiz của một khóa học (qua Section)
    int countBySection_Course_CourseId(int courseId);

	Test findByTestId(int testId);
	List<Test> findBySection(Section sectionEntity);

	// Tìm bài kiểm tra có mã nhỏ nhất của section
	// Trường hợp này do mối quan hệ 1 nhiều
	// Có thể sẽ tạo dư bài quiz
	// Nên lấy cái quiz có mã nhỏ nhất
	// Lấy một bài quiz thôi do một phần chỉ cần một quiz
	@Query("SELECT t FROM Test t WHERE t.section = :section ORDER BY t.testId ASC")
	Test findFirstBySectionOrderByTestIdAsc(@Param("section") Section section);

}
