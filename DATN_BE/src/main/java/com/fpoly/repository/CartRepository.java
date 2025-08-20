package com.fpoly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fpoly.entity.Cart;
import com.fpoly.entity.Course;

import jakarta.transaction.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
	
//CourseDetail
	boolean existsByUser_UserIdAndCourse_CourseId(int userId, int courseId);
	
	// Phần câu lệnh query của HBao
	
	// - hiển thị danh sách  khóa học trong giỏ hàng theo id khách hàng
	//@Query("SELECT c FROM Course c INNER JOIN Cart cart ON c.courseId = cart.course.courseId WHERE cart.user.id = ?1")
	@Query("SELECT ct FROM Cart ct JOIN ct.course ce WHERE ct.user.id = :userId AND ce.price > 0")
	List<Cart> fillListCourseCartByUserID(int userId);
	// Hết phần code của HBao

	Cart findByCartId(int id);

	@Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId AND c.course.id = :courseId")
    void deleteByUserIdAndCourseId(int userId, int courseId);
}
