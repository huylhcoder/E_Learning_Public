package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.LessonComplete;

@Repository
public interface LessonCompleteRepository extends JpaRepository<LessonComplete, Integer> {
    boolean existsByUser_UserIdAndLesson_LessonId(int userId, int lessonId);
}
