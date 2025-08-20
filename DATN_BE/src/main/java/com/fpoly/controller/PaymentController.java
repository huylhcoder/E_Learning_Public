package com.fpoly.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.dto.RegisteredCourseWithPaymentDTO;
import com.fpoly.entity.Course;
import com.fpoly.entity.Payment;
import com.fpoly.entity.RegisteredCourse;
import com.fpoly.entity.User;
import com.fpoly.entity.Voucher;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.service.CourseService;
import com.fpoly.service.PaymentService;
import com.fpoly.service.RegisteredCourseService;
import com.fpoly.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin("*")
@RestController
@RequestMapping("${api.prefix}/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;
	@Autowired
	private UserService userService;
	@Autowired
	private CourseService courseService;
	@Autowired
	private RegisteredCourseService registeredCourseService;

	@Autowired
	private JwtTokenUtils jwtTokenUtils;

//PaymentResult + Checkout Page
	@GetMapping("/payment-latest")
	public ResponseEntity<RegisteredCourseWithPaymentDTO> getLatestPaymentCourses(
			@RequestHeader("Authorization") String token) {
		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", "").trim());
		Payment latestPayment = paymentService.findLatestPaymentByUserEmail(email);

		if (latestPayment == null) {
			return ResponseEntity.noContent().build(); // hoặc throw custom exception
		}

		List<Course> courseList = latestPayment.getListRegisteredCourse().stream().map(RegisteredCourse::getCourse)
				.collect(Collectors.toList());

		RegisteredCourseWithPaymentDTO dto = new RegisteredCourseWithPaymentDTO(courseList,
				latestPayment.isTransactionStatus());

		return ResponseEntity.ok(dto);
	}

//CartPage 
	// Tạo Payment mới
	@PostMapping("/add-payment")
	public ResponseEntity<?> doPost(@RequestHeader(value = "Authorization", required = false) String token,
			@RequestBody List<Integer> listCourseId) {
		try {

			if (listCourseId == null || listCourseId.isEmpty()) {
				return ResponseEntity.badRequest().body("Danh sách khóa học không được rỗng");
			}

			String email = jwtTokenUtils.extractEmail(token != null ? token.replace("Bearer ", "").trim() : "");
			User user = userService.getUserByEmailToan(email);
			if (user == null) {
				return ResponseEntity.badRequest().body("User không tồn tại");
			}

			// Xóa những đơn hàng chưa thanh toán trước đó
			paymentService.deleteAllUnpaidPaymentsByUserEmail(email);

			// Tạo một Payment mới
			Payment payment = new Payment();
			payment.setTransactionStatus(false);
			payment.setUser(user);
			payment.setCreateAt(new Date());
			Payment paymentSaved = paymentService.savePayment(payment);

			for (Integer courseId : listCourseId) {
				Course course = courseService.timKhoaHocTheoMaKhoaHocHuy(courseId).orElse(null);
				if (course == null) {
					return ResponseEntity.badRequest().body("Không tìm thấy khóa học ID: " + courseId);
				}

				RegisteredCourse registered = new RegisteredCourse();
				registered.setCourse(course);
				registered.setUser(user);
				registered.setPayment(paymentSaved);
				registered.setCreateAt(new Date());
				registered.setStatusPayment(false);
				registered.setPrice(course.getPrice());
				registeredCourseService.saverRgisteredCourse(registered); // Lưu lại
			}

			return ResponseEntity.ok("Tạo đơn hàng thành công");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống");
		}
	}

}