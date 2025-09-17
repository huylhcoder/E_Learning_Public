package com.fpoly.vnpay;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fpoly.entity.Payment;
import com.fpoly.entity.RegisteredCourse;
import com.fpoly.entity.User;
import com.fpoly.entity.Voucher;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.service.CartService;
import com.fpoly.service.PaymentService;
import com.fpoly.service.RegisteredCourseService;
import com.fpoly.service.UserService;
import com.fpoly.service.VoucherService;

@CrossOrigin("*")
@RestController
@RequestMapping("${api.prefix}")
public class ThanhToanThanhCong {
	@Autowired
	private JwtTokenUtils jwtTokenUtils;
	@Autowired
	private UserService userService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private VoucherService voucherService;
	@Autowired
	private CartService cartService;
	@Autowired
	private RegisteredCourseService registeredCourseService;

//	@GetMapping("/vnpayreturn")
//	public void vnpayReturn(@RequestParam("user") String tokenString, @RequestParam int paymentId,
//			@RequestParam String voucherCode, @RequestParam("vnp_Amount") long amount,
//			@RequestParam("vnp_BankCode") String bankCode, @RequestParam("vnp_CardType") String cardType,
//			@RequestParam("vnp_OrderInfo") String orderInfo, @RequestParam("vnp_PayDate") String payDate,
//			@RequestParam("vnp_TmnCode") String tmnCode, @RequestParam("vnp_TransactionNo") long transactionNo,
//			@RequestParam("vnp_TransactionStatus") String transactionStatus, @RequestParam("vnp_TxnRef") long txnRef,
//			@RequestParam("vnp_SecureHash") String secureHash, HttpServletResponse response, HttpServletRequest request)
//			throws IOException {
//
//		System.out.println("----- VNPay Return -----");
//
//		boolean isVoucherUsed = voucherCode != null && !voucherCode.equalsIgnoreCase("no");
//		Voucher voucher = null;
//
//		// Xác thực token
//		String email;
//		try {
//			email = jwtTokenUtils.extractEmail(tokenString);
//		} catch (Exception e) {
//			System.out.println("Token không hợp lệ");
//			response.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
//			return;
//		}
//
//		// Tìm user
//		User user = userService.getUserByEmailToan(email);
//		if (user == null) {
//			System.out.println("User không tồn tại");
//			response.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
//			return;
//		}
//
//		// Tìm payment
//		Payment payment = paymentService.getPaymentById(paymentId).orElse(null);
//		if (payment == null) {
//			System.out.println("Payment không tồn tại");
//			response.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
//			return;
//		}
//
//		// Cập nhật thông tin thanh toán
//		payment.setAmount(amount);
//		payment.setBankCode(bankCode);
//		payment.setTransactionNo(String.valueOf(transactionNo));
//		payment.setTxnRef(String.valueOf(txnRef));
//		payment.setTransactionStatus(true); // ✅ Thành công
//		payment.setUser(user);
//		paymentService.savePayment(payment);
//
//		// Cập nhật trạng thái khóa học đã đăng ký
//		// Giá đã được cập nhật bên vnPayAjax rồi không cần kiểm tra voucher và update
//		// giá
//		List<RegisteredCourse> registeredCourses = registeredCourseService.findRegisterCourseByPayment(payment);
//		for (RegisteredCourse course : registeredCourses) {
//			course.setStatusPayment(true);
//			registeredCourseService.saverRgisteredCourse(course);
//
//			// Xóa khóa học trong giỏ hàng
//			int courseId = course.getCourse().getCourseId();
//			cartService.deleteCartsByUserIdAndCourseId(user.getUserId(), courseId);
//		}
//
//		// Cập nhật số lượng voucher nếu có
//		if (isVoucherUsed) {
//			voucher = voucherService.findVoucherByCode_Huy(voucherCode);
//			if (voucher != null) {
//				voucher.setQuantity(voucher.getQuantity() - 1);
//				voucherService.save(voucher);
//			} else {
//				System.out.println("Voucher không hợp lệ hoặc không tồn tại: " + voucherCode);
//			}
//		}
//		
//		//Xóa tất cả Payment chưa thanh toán thành công theo Email
//		paymentService.deleteAllUnpaidPaymentsByUserEmail(email);
//
//		// Chuyển hướng đến trang thông báo kết quả
//		response.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=true");
//		System.out.println("----- VNPay Return End -----");
//	}
	
	@GetMapping("/vnpayreturn")
	public void vnpayReturn(@RequestParam int paymentId,
	        @RequestParam(required = false, defaultValue = "no") String voucherCode,
	        @RequestParam("vnp_Amount") long amount,
	        @RequestParam("vnp_BankCode") String bankCode,
	        @RequestParam("vnp_CardType") String cardType,
	        @RequestParam("vnp_OrderInfo") String orderInfo,
	        @RequestParam("vnp_PayDate") String payDate,
	        @RequestParam("vnp_TmnCode") String tmnCode,
	        @RequestParam("vnp_TransactionNo") long transactionNo,
	        @RequestParam("vnp_TransactionStatus") String transactionStatus,
	        @RequestParam("vnp_TxnRef") long txnRef,
	        @RequestParam("vnp_SecureHash") String secureHash,
	        HttpServletResponse response) throws IOException {

	    System.out.println("----- VNPay Return -----");

	    boolean isVoucherUsed = voucherCode != null && !voucherCode.equalsIgnoreCase("no");

	    // ✅ Tìm payment
	    Payment payment = paymentService.getPaymentById(paymentId).orElse(null);
	    if (payment == null) {
	        System.out.println("Payment không tồn tại");
	        response.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
	        return;
	    }

	    User user = payment.getUser();
	    if (user == null) {
	        System.out.println("Payment không gắn user");
	        response.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
	        return;
	    }

	    // ✅ Kiểm tra trạng thái giao dịch từ VNPay
	    if (!"00".equals(transactionStatus)) {
	        System.out.println("Giao dịch thất bại. transactionStatus = " + transactionStatus);

	        // Cập nhật lại Payment thành thất bại
	        payment.setTransactionStatus(false);
	        paymentService.savePayment(payment);

	        response.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
	        return;
	    }

	    // ✅ Cập nhật thông tin thanh toán khi thành công
	    payment.setAmount(amount);
	    payment.setBankCode(bankCode);
	    payment.setTransactionNo(String.valueOf(transactionNo));
	    payment.setTxnRef(String.valueOf(txnRef));
	    payment.setTransactionStatus(true); // chỉ set true khi transactionStatus = "00"
	    paymentService.savePayment(payment);

	    // ✅ Cập nhật RegisteredCourse
	    List<RegisteredCourse> registeredCourses = registeredCourseService.findRegisterCourseByPayment(payment);
	    for (RegisteredCourse course : registeredCourses) {
	        course.setStatusPayment(true);
	        registeredCourseService.saverRgisteredCourse(course);

	        // Xóa khóa học trong giỏ hàng
	        cartService.deleteCartsByUserIdAndCourseId(user.getUserId(), course.getCourse().getCourseId());
	    }

	    // ✅ Trừ số lượng voucher nếu có
	    if (isVoucherUsed) {
	        Voucher voucher = voucherService.findVoucherByCode_Huy(voucherCode);
	        if (voucher != null) {
	            voucher.setQuantity(voucher.getQuantity() - 1);
	            voucherService.save(voucher);
	        } else {
	            System.out.println("Voucher không hợp lệ hoặc không tồn tại: " + voucherCode);
	        }
	    }

	    // ✅ Xóa các payment chưa thanh toán thành công
	    paymentService.deleteAllUnpaidPaymentsByUserEmail(user.getEmail());

	    // ✅ Redirect về FE
	    response.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=true");
	    System.out.println("----- VNPay Return End -----");
	}


}
