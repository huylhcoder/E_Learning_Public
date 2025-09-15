package com.fpoly.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.dto.CommentDTO;
import com.fpoly.entity.Comment;
import com.fpoly.entity.Course;
import com.fpoly.entity.Voucher;
import com.fpoly.entity.Reply;
import com.fpoly.entity.User;
import com.fpoly.repository.AnswerRepository;
import com.fpoly.repository.CommentRepository;
import com.fpoly.repository.CourseRepository;
import com.fpoly.repository.CommentRepository;;

@Service
public class CommentService {
	@Autowired
	CommentRepository commentRepository;

//Learning Page
	// Kiểm tra người dùng đã Comment chưa
	public List<CommentDTO> getUserComments(int courseId, int userId) {
		List<Comment> comments = commentRepository.findByCourse_CourseIdAndUser_UserId(courseId, userId);
		return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private CommentDTO convertToDTO(Comment comment) {
		CommentDTO dto = new CommentDTO();
		dto.setUser(comment.getUser());
		dto.setCourseId(comment.getCourse().getCourseId());
		dto.setStarRating(comment.getStarRating());
		dto.setContent(comment.getContent());
		return dto;
	}

	public List<Comment> hienThiDanhGiaTheoKhoaHoc(int courseId) {
		return commentRepository.findByCourseIdAndStatus(courseId, true); // true cho status = 1
	}

	public void addCommentToan(Comment cmt) {
		commentRepository.save(cmt);
	}

//Khác
	public List<Comment> getAllComment() {
		return commentRepository.findAll();
	}

	public List<Comment> findCommentByIdToan(Course course) {
		return commentRepository.findByCourse(course);
	}

	public List<Comment> findCommentByCourseToan(Course course) {
		return commentRepository.findByCourse(course);
	}

	public Comment getCommentById(int commentId) {
		return commentRepository.findById(commentId).orElse(null);
	}

	public Comment saveComment(Comment comment) {
		return commentRepository.save(comment);
	}

	public List<Comment> getAllUser() {
		return commentRepository.findAllRepliesWithComments();
	}

}
