package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.Category;
import com.fpoly.entity.Course;
import com.fpoly.entity.User;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
//Home Page
	//Tổng số danh mục
	@Query("SELECT COUNT(c.name) FROM Category c")
	Long countTotalCategory();
	
	Category findByCategoryId(int categoryId);

	List<Category> findByParentIsNull();

	List<Category> findByParent(Category parent);

	@Query("SELECT c.id AS id, COUNT(cc) FROM Category c LEFT JOIN c.courseCategories cc GROUP BY c.id")
	List<Object[]> countCoursesByCategoryForTree();

	// Lấy toàn bộ danh mục + cha (nếu có)
	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent")
	List<Category> findAllWithParent();

	// Đếm số khóa học trong từng danh mục
	@Query("SELECT cc.category.categoryId, COUNT(cc.course.courseId) "
			+ "FROM CourseCategory cc GROUP BY cc.category.categoryId")
	List<Object[]> countCoursesByCategory();
}
