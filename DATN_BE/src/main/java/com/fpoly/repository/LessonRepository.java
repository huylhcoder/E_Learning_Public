package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.entity.Lesson;
import com.fpoly.entity.Section;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
//Learning Page
	//Lesson → Section → Course → courseId
	Optional<Lesson> findFirstBySection_Course_CourseIdOrderByLessonIdAsc(int courseId);
	
//Khác
	List<Lesson> findBySection(Section sectionEntity);
	
}
