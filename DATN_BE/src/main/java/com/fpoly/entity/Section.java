package com.fpoly.entity;

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
@Table(name = "section")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "section_id")
	private int sectionId;

	@ManyToOne
	@JoinColumn(name = "course_id")
	Course course;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "content_description", columnDefinition = "NVARCHAR(MAX)")
	private String contentDescription; //Quill Text Editor
	
	@Column(name = "section_duration")
	private float sectionDuration;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")
	private Date createAt;

	@Temporal(TemporalType.DATE)
	@Column(name = "update_at")
	private Date updateAt;

	@JsonIgnore
	@OneToMany(mappedBy = "section")
	List<Lesson> listLesson;
	
	@JsonIgnore
	@OneToMany(mappedBy = "section")
	List<Material> listMaterial;
	
	@JsonIgnore
	@OneToMany(mappedBy = "section")
	List<Test> listTest;	
}
