package com.fpoly.vnpay;

import java.io.IOException;
import java.util.HashMap;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import com.fpoly.service.VoucherService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@CrossOrigin("*")
@RestController
@RequestMapping("${api.prefix}")
public class AjaxVnpay {

	@Autowired
	private JwtTokenUtils jwtTokenUtils;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private UserService userService;
	@Autowired
	private CourseService courseService;
	@Autowired
	private RegisteredCourseService registeredCourseService;
	@Autowired
	private VoucherService voucherService;

//CheckoutPage
	/*
	 * Xác thực token người dùng Kiểm tra voucher khuyến mãi (nếu có) Lưu thông tin
	 * thanh toán và khóa học đăng ký với setTransactionStatus(false) Tạo URL thanh
	 * toán VNPay và trả về cho frontend
	 */
	@PostMapping("/vnpayajax")
	public void doPost(HttpServletRequest req, HttpServletResponse resp,
			@RequestHeader(value = "Authorization", required = false) String token,
			@RequestParam("listCourseId") String listCourseIdJson,
			@RequestParam(value = "voucherCode", required = false) String voucherCodeOpt)
			throws IOException, JSONException {

		System.out.println("----- VNPay Ajax Start -----");
		System.out.println("Giá trị listCourseIdJson: " + listCourseIdJson);
		System.out.println("Giá trị voucherCodeOpt: " + voucherCodeOpt);

		// Parse courseId JSON
		List<Integer> listCourseId = new ArrayList<>();
		JSONArray jsonArray = new JSONArray(listCourseIdJson);
		for (int i = 0; i < jsonArray.length(); i++) {
			listCourseId.add(jsonArray.getInt(i));
		}

		// Token xử lý
		String tokenValue = token != null ? token.replace("Bearer ", "").trim() : "";
		String email;
		try {
			email = jwtTokenUtils.extractEmail(tokenValue);
		} catch (Exception e) {
			System.out.println("Token không hợp lệ");
			resp.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
			return;
		}

		User user = userService.getUserByEmailToan(email);
		if (user == null) {
			System.out.println("User null");
			resp.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
			return;
		}

		// Xử lý voucher nếu có
		String voucherCode = (voucherCodeOpt != null) ? voucherCodeOpt.trim() : "";
		boolean hasVoucher = !voucherCode.isEmpty();

		Voucher voucher = null;

		if (hasVoucher) {
			voucher = voucherService.findVoucherByCode_Huy(voucherCode);
			if (voucher == null || voucher.getQuantity() == 0 || !voucher.isStatus()
					|| new Date().before(voucher.getStartDate()) || new Date().after(voucher.getEndDate())) {
				System.out.println("Voucher không hợp lệ hoặc hết hạn");
				resp.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
				return;
			}
		}

		// Tạo payment mới
		Payment payment = new Payment();
		payment.setTransactionStatus(false); // Chưa thanh toán
		payment.setUser(user);
		payment.setCreateAt(new Date());
		Payment paymentSaved = paymentService.savePayment(payment);

		float totalAmount = 0;

		for (Integer courseId : listCourseId) {
			Course course = courseService.timKhoaHocTheoMaKhoaHocHuy(courseId).orElse(null);
			if (course == null) {
				System.out.println("Course null: " + courseId);
				resp.sendRedirect("http://localhost:3000/payment-result?vnPaymentStatus=false");
				return;
			}

			float coursePrice = course.getPrice();

			if (hasVoucher) {
				coursePrice -= (coursePrice * voucher.getPercentSale() / 100);
			}

			totalAmount += coursePrice;

			RegisteredCourse registered = new RegisteredCourse();
			registered.setCourse(course);
			registered.setUser(user);
			registered.setPayment(paymentSaved);
			registered.setCreateAt(new Date());
			registered.setStatusPayment(false);
			registered.setPrice(coursePrice); // Giá tại thời điểm thanh toán

			registeredCourseService.saverRgisteredCourse(registered);
		}

		// Cập nhật lại tổng tiền vào bảng payment
		paymentSaved.setAmount(totalAmount);
		paymentService.savePayment(paymentSaved);

		// Tạo URL thanh toán VNPAY
		long amount = (long) (totalAmount * 100); // nhân 100 vì VNPAY yêu cầu
		String vnp_TxnRef = Config.getRandomNumber(8);
		String vnp_IpAddr = Config.getIpAddress(req);

		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", "2.1.0");
		vnp_Params.put("vnp_Command", "pay");
		vnp_Params.put("vnp_TmnCode", Config.vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(amount));
		vnp_Params.put("vnp_CurrCode", "VND");

		String bankCode = req.getParameter("bankCode");
		if (bankCode != null && !bankCode.isEmpty()) {
			vnp_Params.put("vnp_BankCode", bankCode);
		}

		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef);
		vnp_Params.put("vnp_OrderType", "other");

		String locale = req.getParameter("language");
		vnp_Params.put("vnp_Locale", (locale != null && !locale.isEmpty()) ? locale : "vn");

		String returnUrl = Config.vnp_ReturnUrl + "?user=" + URLEncoder.encode(tokenValue, StandardCharsets.UTF_8)
				+ "&paymentId=" + paymentSaved.getPaymentId() + "&voucherCode="
				+ (voucherCode != null ? URLEncoder.encode(voucherCode, StandardCharsets.UTF_8) : "");

		vnp_Params.put("vnp_ReturnUrl", returnUrl);

//	    vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl + "?user=" + tokenValue + "&paymentId=" +
//	            paymentSaved.getPaymentId() + "&voucherCode=" + voucherCode);
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		vnp_Params.put("vnp_CreateDate", formatter.format(cal.getTime()));
		cal.add(Calendar.MINUTE, 15);
		vnp_Params.put("vnp_ExpireDate", formatter.format(cal.getTime()));

		// Build query & hash
		List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
		Collections.sort(fieldNames);

		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		for (Iterator<String> it = fieldNames.iterator(); it.hasNext();) {
			String key = it.next();
			String value = vnp_Params.get(key);
			if (value != null && !value.isEmpty()) {
				hashData.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
				query.append(URLEncoder.encode(key, StandardCharsets.US_ASCII)).append('=')
						.append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
				if (it.hasNext()) {
					hashData.append('&');
					query.append('&');
				}
			}
		}

		String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
		query.append("&vnp_SecureHash=").append(vnp_SecureHash);

		String paymentUrl = Config.vnp_PayUrl + "?" + query;

		JsonObject responseJson = new JsonObject();
		responseJson.addProperty("code", "00");
		responseJson.addProperty("message", "success");
		responseJson.addProperty("data", paymentUrl);

		resp.getWriter().write(new Gson().toJson(responseJson));
		System.out.println("----- VNPay Ajax End -----");
	}

}
