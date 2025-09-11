package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.fpoly.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	@Query("SELECT p FROM Payment p WHERE p.user.email = :email AND p.transactionStatus = true ORDER BY p.createAt DESC")
	List<Payment> findSuccessfulPaymentsByEmail(@Param("email") String email);

	// Tìm kiếm Payment mới nhất thanh toán thành công
	default Optional<Payment> findLatestSuccessfulPayment(String email) {
		List<Payment> payments = findSuccessfulPaymentsByEmail(email);
		return payments.isEmpty() ? Optional.empty() : Optional.of(payments.get(0));
	}

//Payment History Page
	// Lấy tất cả Payment thành công theo userId
	List<Payment> findByUser_UserIdAndTransactionStatusTrue(int userId);

//Checkout + Payment return
	// Tìm kiếm payment mới nhất thôi
	Optional<Payment> findTopByUserEmailOrderByPaymentIdDesc(String email);

	// Xóa tất cả đơn hàng với trạng thái thanh toán chưa thành công của người dùng
	@Modifying
	@Transactional
	@Query("DELETE FROM Payment p WHERE p.user.email = :email AND p.transactionStatus = false")
	void deleteAllUnpaidPaymentsByUserEmail(@Param("email") String email);

	List<Payment> findByUserEmailAndTransactionStatusFalse(String email);

}
