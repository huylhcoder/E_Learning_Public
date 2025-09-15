package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.Comment;
import com.fpoly.entity.Course;
import com.fpoly.entity.Reply;
import com.fpoly.entity.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

//CourseDetailPage
	int countByCourse_CourseId(int courseId);

//Learning Page
	//Lấy list thay vì lấy Object vì khi test đang trả về nhiều hơn một 
	//Nếu lấy object mà tìm ra list là nó lỗi liền
	List<Comment> findByCourse_CourseIdAndUser_UserId(int courseId, int userId);


	@Query("SELECT cm FROM Comment cm WHERE cm.course.id = :courseId AND cm.status = :status")
	List<Comment> findByCourseIdAndStatus(@Param("courseId") int courseId, @Param("status") boolean status);

//	@Query("SELECT u FROM Comment u WHERE u.course = :course")
//	Comment findByCourseId(@Param("course") int course);
	List<Comment> findByCourse(Course course);

	@Query("SELECT r FROM Reply r JOIN r.comment c WHERE r.comment.commentId = c.commentId")
	List<Comment> findAllRepliesWithComments();

}
