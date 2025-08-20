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
@Table(name = "test")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class Test {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "test_id")
	private int testId;

	@ManyToOne
	@JoinColumn(name = "section_id")
	Section section;

	@Column(name = "title")
	private String title;

	@Column(name = "number_of_question")
	private String numberOfQuestion;

	@Column(name = "description")
	private String description;

	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")
	private Date createAt;

	@Temporal(TemporalType.DATE)
	@Column(name = "update_at")
	private Date updateAt;
	
	//Đồng hồ điểm ngược tính theo giây
	@Column(name = "countdown_timer")
	private int countdownTimer;

	@JsonIgnore
	@OneToMany(mappedBy = "test")
	List<UserTestResult> listUserTestResult;
	 	
	@JsonIgnore
	@OneToMany(mappedBy = "test")
	List<Question> listQuestion;
	
	@JsonIgnore
	@OneToMany(mappedBy = "test")
	List<UserAnswerHistory> listUserAnswerHistory;
}
