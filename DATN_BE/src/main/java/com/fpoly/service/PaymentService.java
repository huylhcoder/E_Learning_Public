package com.fpoly.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpoly.dto.PaymentDTO;
import com.fpoly.entity.Course;
import com.fpoly.entity.Payment;
import com.fpoly.entity.RegisteredCourse;
import com.fpoly.repository.AnswerRepository;
import com.fpoly.repository.CourseRepository;
import com.fpoly.repository.PaymentRepository;
import com.fpoly.repository.RegisteredCourseRepository;

@Service
public class PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private RegisteredCourseRepository registeredCourseRepository;

	public Payment savePayment(Payment payment) {
		return paymentRepository.save(payment);
	}

	public Optional<Payment> getPaymentById(int paymentId) {
		return paymentRepository.findById(paymentId);
	}

//Checkout + vnPayReturn
	// Tìm kiếm Payment mới nhất của người dùng
	public Payment findLatestPaymentByUserEmail(String email) {
		return paymentRepository.findTopByUserEmailOrderByPaymentIdDesc(email)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán nào cho người dùng: " + email));
	}

	// Xóa tất cả Payment chưa thanh toán
	@Transactional
	public void deleteAllUnpaidPaymentsByUserEmail(String email) {
		List<Payment> payments = paymentRepository.findByUserEmailAndTransactionStatusFalse(email);
		if (payments.isEmpty())
			return;

		List<Integer> ids = payments.stream().map(Payment::getPaymentId).collect(Collectors.toList());

		// 1) Xóa tất cả RegisteredCourse tham chiếu đến những payment này
		registeredCourseRepository.deleteByPaymentIds(ids);

		// 2) Xóa payments
		paymentRepository.deleteAll(payments);
	}
}
