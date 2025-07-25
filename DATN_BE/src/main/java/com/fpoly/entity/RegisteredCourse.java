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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registered_course")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class RegisteredCourse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "registered_course_id")
	private int registeredCourseId;

	@ManyToOne
	@JoinColumn(name = "users_id")
	User user;

	@ManyToOne
	@JoinColumn(name = "course_id")
	Course course;

	@ManyToOne
	@JoinColumn(name = "payment_id")
	Payment payment;

	@Column(name = "price")
	private float price;

	@Column(name = "status_comment")
	private boolean statusComment;

	@Column(name = "status_payment")
	private boolean statusPayment;

	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")
	private Date createAt;
}
