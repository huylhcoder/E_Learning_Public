package com.fpoly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course_progress")
public class CourseProgress {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_progress_id")
	private int courseProgressId;

	@ManyToOne
	@JoinColumn(name = "course_id")
	Course course;

	@ManyToOne
	@JoinColumn(name = "users_id")
	User user;
	
//	@Column(name = "current_lession_id")
//	private int currentLessionId;
	// Cái này mình chưa liên kết khóa ngoại 
	// Tránh phải query thủ công lấy lesson
	@ManyToOne
	@JoinColumn(name = "current_lession_id")
	private Lesson currentLesson;

	
	@Column(name = "total_lession")
	private int totalLession;
	
	@Column(name = "total_quiz")
	private int totalQuiz;
	
	@Column(name = "total_lession_complete")
	private int totalLessionComplete;
	
	@Column(name = "total_test_complete")
	private int totalTestComplete;
	
	@Column(name = "progress_percentage")
	private float progressPercentage;
	
	@Column(name = "progress_status")
	private int progressStatus;

}
