package com.fpoly.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.entity.VideoProgress;
import com.fpoly.repository.VideoProgressRepository;

@Service
public class VideoProgressService {

	@Autowired
	private VideoProgressRepository videoProgressRepository;

	// Lưu hoặc cập nhật tiến độ video
	public VideoProgress saveOrUpdateVideoProgress(VideoProgress progress, int userId) {
		Optional<VideoProgress> existingProgressOpt = videoProgressRepository.findByUserIdAndLessonId(userId,
				progress.getLessonId());

		// Kiểm tra nếu đã có tiến độ video
		if (existingProgressOpt.isPresent()) {
			VideoProgress existingProgress = existingProgressOpt.get();

			// Kiểm tra nếu tiến độ mới lớn hơn hoặc bằng tiến độ đã lưu
			if (progress.getVideoProgress() >= existingProgress.getVideoProgress()) {
				// Cập nhật tiến độ video và thời gian cập nhật
				existingProgress.setVideoProgress(progress.getVideoProgress());
				existingProgress.setUpdate_at(progress.getUpdate_at());

				return videoProgressRepository.save(existingProgress);
			} else {
				// Nếu tiến độ mới nhỏ hơn tiến độ đã lưu, không cập nhật
				return existingProgress;
			}
		} else {
			// Nếu chưa có tiến độ video, tạo mới
			return videoProgressRepository.save(progress);
		}
	}

	// Lấy tiến độ video của người dùng và bài học
	public Optional<VideoProgress> getVideoProgressByUserAndLesson(int userId, int lessonId) {
		return videoProgressRepository.findByUserIdAndLessonId(userId, lessonId);
	}
}
