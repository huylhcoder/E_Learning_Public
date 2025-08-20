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
@Table(name = "comment")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private int commentId;

	@ManyToOne
	@JoinColumn(name = "course_id")
	Course course;

	@ManyToOne
	@JoinColumn(name = "users_id")
	User user;

	@Column(name = "contents")
	private String content;

	@Column(name = "start_rating")
	private float starRating;

	@Column(name = "status")
	private boolean status;

	@Column(name = "reply_status")
	private boolean replyStatus;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")
	private Date createAt;

	@JsonIgnore
	@OneToMany(mappedBy = "comment")
	List<Reply> reply;
	
}
