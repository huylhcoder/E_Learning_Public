package com.fpoly.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lesson")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lesson {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lesson_id")
	private int lessonId;
	
	@ManyToOne
	@JoinColumn(name = "section_id")
	Section section;
	
	@Column(name = "lesson_duration")
	private float lessionDuration;
	
	@Column(name = "path_video")
	private String pathVideo;

	@Column(name = "description")
	private String description;
	
	@Column(name = "content_description", columnDefinition = "NVARCHAR(MAX)")
	private String contentDescription; //Quill Text Editor

	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")
	private Date createAt;

	@Temporal(TemporalType.DATE)
	@Column(name = "update_at")
	private Date updateAt;
	
	@Column(name = "title")
	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "lesson")
	List<LessonComplete> listLessonComplete;	
	
	@JsonIgnore
	@OneToMany(mappedBy = "currentLesson")
	List<CourseProgress> listCourseProgress;	
}
