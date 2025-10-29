package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
//Home page
	// Tổng số học viên tham gia
	@Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName")
	long countByRoleName(@Param("roleName") String roleName);

	@Query("SELECT u FROM User u WHERE u.email = :email")
	User timKiemUserTheoEmailToan(@Param("email") String email);

//Login - Google login
	// Trả về một cái Optional User
	Optional<User> findByEmail(String email);

	Optional<User> findByGoogleAccountId(String googleAccountId);
//Khác
//	User findByEmail(String email);

//	Optional<User> findByEmail(String email);

	// Phương thức kiểm tra xem tên người dùng đã tồn tại hay chưa
//	boolean existsByUsername(String username);
	// Phương thức kiểm tra xem tên người dùng đã tồn tại hay chưa
	// boolean existsByUsername(String username); //Xóa cái tìm kiếm theo Username

	// Phương thức kiểm tra xem email đã tồn tại hay chưa
//	boolean existsByEmail(String email);

	User findByUserId(int userId);

	@Query("SELECT u FROM User u WHERE u.role.roleId = 2")
	List<User> fillAllUserRole2();

	// --- PHẦN CODE CỦA HBAO ---
	// - CODE Của HBao Biểu đồ người dùng theo tháng
	@Query("SELECT MONTH(u.createAt) AS month, COUNT(u) AS userCount " + "FROM User u "
			+ "WHERE u.createAt IS NOT NULL AND YEAR(u.createAt) = YEAR(CURRENT_DATE)" + // Loại bỏ các bản ghi null
			"GROUP BY MONTH(u.createAt) " + "ORDER BY month")
	List<Object[]> countUsersByMonth();

	// - CODE Của HBao Biểu đồ người dùng theo năm
	@Query("SELECT YEAR(u.createAt) AS year, COUNT(u) AS userCount " + "FROM User u " + "WHERE u.createAt IS NOT NULL "
			+ // Loại bỏ các bản ghi null
			"GROUP BY YEAR(u.createAt) " + "ORDER BY year")
	List<Object[]> countUsersByYear();

	// - CODE Của HBao Biểu đồ doanh thu theo tháng
//	@Query("SELECT MONTH(RC.createAt) AS month, SUM(RC.price) AS recordCount " + "FROM RegisteredCourse RC "
//			+ "WHERE YEAR(RC.createAt) = YEAR(CURRENT_DATE)"
//			+ "GROUP BY MONTH(RC.createAt) " + "ORDER BY month")
//	List<Object[]> countCoursesByMonth();
	@Query("SELECT MONTH(RC.createAt) AS month, SUM(RC.price) AS recordCount " + "FROM RegisteredCourse RC "
			+ "JOIN RC.course c "
			+ "WHERE YEAR(RC.createAt) = YEAR(CURRENT_DATE) AND c.status = 1 AND RC.statusPayment = true "
			+ "GROUP BY MONTH(RC.createAt) " + "ORDER BY month")
	List<Object[]> countCoursesByMonth();

	// - CODE Của HBao Biểu đồ doanh thu theo năm
//	@Query("SELECT YEAR(RC.createAt) AS year, SUM(RC.price) AS recordCount " + "FROM RegisteredCourse RC "
//			+ "GROUP BY YEAR(RC.createAt) " + "ORDER BY year")
//	List<Object[]> countCoursesByYear();
	@Query("SELECT YEAR(RC.createAt) AS year, SUM(RC.price) AS recordCount " + "FROM RegisteredCourse RC "
			+ "JOIN RC.course c WHERE c.status = 1 AND RC.statusPayment = true " + "GROUP BY YEAR(RC.createAt) "
			+ "ORDER BY year")
	List<Object[]> countCoursesByYear();

	// --- HẾT ---
	// Phương thức kiểm tra xem email đã tồn tại hay chưa
	boolean existsByEmail(String email);

}
