package com.fpoly.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@Entity
@Table(name = "course")
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_id")
	private int courseId;
	
	@ManyToOne
	@JoinColumn(name = "users_id")
	User user;

	@ManyToOne
	@JoinColumn(name = "course_level_id")
	CourseLevel courseLevel;

	@Column(name = "avatar")
	private String avatar;

	@Column(name = "name")
	private String name;

	@Column(name = "topic")
	private String topic;

	@Column(name = "description")
	private String description;

	@Column(name = "path_video_demo")
	private String pathVideoDemo;

	@Column(name = "follow")
	private int follow;

	@Column(name = "total_rate")
	private float totalRate;

	@Column(name = "average_rating")
	private float averageRating;

	@Column(name = "number_of_lesson")
	private int numberOfLesson;

	@Column(name = "course_duration")
	private float courseDuration;

	// 0 => nháp, 1 => công khai, 2 => không công khai
	@Column(name = "status")
	private int status;

	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")
	private Date createAt;

	@Temporal(TemporalType.DATE)
	@Column(name = "update_at")
	private Date updateAt;

	@Column(name = "price")
	private float price;

	@JsonIgnore
	@OneToMany(mappedBy = "course")
	List<HashTagOfCourse> listHashTagOfCourse;

	@JsonIgnore
	@OneToMany(mappedBy = "course")
	List<Section> listSection;

	@JsonIgnore
	@OneToMany(mappedBy = "course")
	List<Cart> listcart;

	@JsonIgnore
	@OneToMany(mappedBy = "course")
	List<Comment> listComment;

	@JsonIgnore
	@OneToMany(mappedBy = "course")
	List<CourseProgress> listCourseProgress;

	@JsonIgnore
	@OneToMany(mappedBy = "course")
	List<FavoriteCourse> listFavouriteCourse;

	@JsonIgnore
	@OneToMany(mappedBy = "course")
	List<RegisteredCourse> listRegisteredCourse;

	@OneToMany(mappedBy = "course")
	@JsonIgnore
	private List<CourseCategory> courseCategories = new ArrayList<>();
}
