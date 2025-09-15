package com.fpoly.dto;

import java.util.Date;

import com.fpoly.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class CommentDTO {
//
//	private int userId;
//	private int courseId;
//	private int starRating;
//	private String content;
//
//}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
	private int commentId;
	private User user;

	private int courseId;
	private String courseName;

	private float starRating;
	private String content;

	private Date createdDate;
	private boolean status;
}
