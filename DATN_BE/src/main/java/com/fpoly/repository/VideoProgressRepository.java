package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.entity.VideoProgress;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, Integer> {

//Learing Page
	// Kiểm tra xem người dùng đã hoàn thành bài này chưa
	boolean existsByUserIdAndLessonId(int userId, int lessonId);

	Optional<VideoProgress> findByUserIdAndLessonId(int userId, int lessonId);
}
