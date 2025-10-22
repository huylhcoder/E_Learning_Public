package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fpoly.entity.Lesson;
import com.fpoly.entity.Section;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
//Learning Page
	// Lesson → Section → Course → courseId
	Optional<Lesson> findFirstBySection_Course_CourseIdOrderByLessonIdAsc(int courseId);
	
	// Phương thức đếm tổng số Lesson của một khóa học (qua Section)
    int countBySection_Course_CourseId(int courseId);

//Section manager
	@Query("SELECT SUM(l.lessionDuration) FROM Lesson l WHERE l.section.sectionId = :sectionId")
	Float sumLessionDurationBySectionId(@Param("sectionId") int sectionId);

//Khác
	List<Lesson> findBySection(Section sectionEntity);

}
