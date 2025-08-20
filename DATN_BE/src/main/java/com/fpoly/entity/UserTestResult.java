package com.fpoly.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "user_test_result")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class UserTestResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_test_result_id")
	private int userTestResultId;

	@ManyToOne
	@JoinColumn(name = "users_id")
	User user;

	@ManyToOne
	@JoinColumn(name = "test_id")
	Test test;

	@Column(name = "score")
	private float score;

	@Column(name = "completion_time")
	private float completionTime;

	@Column(name = "number_of_correct_answer")
	private int numberOfCorrectAnswer;

	@Column(name = "status")
	private boolean status;

	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")
	private Date createAt;

	@Temporal(TemporalType.DATE)
	@Column(name = "update_at")
	private Date updateAt;
	
	@Column(name = "max_score")
	private float maxScore;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_time")
	private Date startTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_time")
	private Date endTime;

}
