package com.fpoly.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.dto.MyVoucherDTO;
import com.fpoly.dto.VoucherResponseDTO;
import com.fpoly.entity.MyVoucher;
import com.fpoly.entity.User;
import com.fpoly.entity.Voucher;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.service.MyVoucherService;
import com.fpoly.service.UserService;
import com.fpoly.service.VoucherService;

@CrossOrigin("*")
@RestController
@RequestMapping("${api.prefix}/voucher")
public class VoucherController {

	@Autowired
	UserService userService;
	@Autowired
	VoucherService voucherService;
	@Autowired
	MyVoucherService myVoucherService;

	@Autowired
	JwtTokenUtils jwtTokenUtils;

// Checkout Page
	// List My Voucher
//	@GetMapping("/myvoucher")
//	public ResponseEntity<?> getMyVouchers(@RequestHeader("Authorization") String authHeader) {
//		try {
//			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//				return ResponseEntity.badRequest().body("Authorization header không hợp lệ.");
//			}
//
//			String token = authHeader.substring(7).trim();
//			String email = jwtTokenUtils.extractEmail(token);
//
//			User user = userService.getUserByEmailToan(email);
//			if (user == null) {
//				return ResponseEntity.status(404).body("Người dùng không tồn tại.");
//			}
//
//			List<Voucher> vouchers = voucherService.getVouchersByEmail(email);
//			if (vouchers.isEmpty()) {
//				return ResponseEntity.status(404).body("Không tìm thấy voucher nào cho người dùng này.");
//			}
//
//			List<VoucherResponseDTO> voucherDTOs = vouchers.stream()
//					.map(v -> new VoucherResponseDTO(v.getVoucherId(), v.getName(), v.getDescription(),
//							v.getVoucherCode(), v.getPercentSale(), v.getStartDate(), v.getEndDate()))
//					.toList();
//
//			return ResponseEntity.ok(voucherDTOs);
//
//		} catch (Exception e) {
//			return ResponseEntity.status(400).body("Token không hợp lệ.");
//		}
//	}

	@GetMapping("/myvoucher")
	public ResponseEntity<?> getMyVouchers(@RequestHeader("Authorization") String authHeader) {
		try {
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return ResponseEntity.badRequest().body("Authorization header không hợp lệ.");
			}

			String token = authHeader.substring(7).trim();
			String email = jwtTokenUtils.extractEmail(token);

			User user = userService.getUserByEmailToan(email);
			if (user == null) {
				return ResponseEntity.status(404).body("Người dùng không tồn tại.");
			}

			List<Voucher> vouchers = voucherService.getVouchersByEmail(email);
			if (vouchers.isEmpty()) {
				return ResponseEntity.status(404).body("Không tìm thấy voucher nào cho người dùng này.");
			}

			Date now = new Date();

			// Lọc chỉ voucher còn hạn, còn lượt, còn hoạt động
			List<VoucherResponseDTO> voucherDTOs = vouchers.stream().filter(v -> v.isStatus()) // còn hoạt động
					.filter(v -> v.getQuantity() > 0) // còn lượt dùng
					.filter(v -> v.getEndDate() == null || v.getEndDate().after(now)) // còn hạn hoặc chưa có hạn
					.map(v -> new VoucherResponseDTO(v.getVoucherId(), v.getName(), v.getDescription(),
							v.getVoucherCode(), v.getPercentSale(), v.getStartDate(), v.getEndDate()))
					.toList();

			if (voucherDTOs.isEmpty()) {
				return ResponseEntity.status(404).body("Không có voucher nào còn hạn hoặc còn lượt dùng.");
			}

			return ResponseEntity.ok(voucherDTOs);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(400).body("Token không hợp lệ.");
		}
	}

	// Check Voucher
	@GetMapping("/check-voucher/{voucherCode}")
	public ResponseEntity<?> checkVoucherCode(@PathVariable("voucherCode") String voucherCode) {
		try {
			// Tìm kiếm mã khuyến mãi
			Voucher voucher = voucherService.findVoucherByCode_Huy(voucherCode.trim());

			// Không tìm thấy
			if (voucher == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Collections.singletonMap("message", "Không tìm thấy khuyến mãi!"));
			}

			// Hết số lượng
			if (voucher.getQuantity() == 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Collections.singletonMap("message", "Số lượng khuyến mãi đã giới hạn!"));
			}

			// Kiểm tra hiệu lực
			Date currentDate = new Date();
			if (!voucher.isStatus() || currentDate.before(voucher.getStartDate())
					|| currentDate.after(voucher.getEndDate())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Collections.singletonMap("message", "Khuyến mãi không còn hiệu lực!"));
			}

			// Chuyển về DTO để trả ra FE
			VoucherResponseDTO voucherDTO = new VoucherResponseDTO(voucher.getVoucherId(), voucher.getName(),
					voucher.getDescription(), voucher.getVoucherCode(), voucher.getPercentSale(),
					voucher.getStartDate(), voucher.getEndDate());

			return ResponseEntity.ok(voucherDTO);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("message", "Đã xảy ra lỗi ở server!"));
		}
	}

//Discount Manager
	@GetMapping("voucher-manager")
	public List<Voucher> getAllSales() {
		return voucherService.getAllVoucher();
	}

	// Hiển thị chi tiết voucher
	@GetMapping("/voucher-manager/{id}")
	public Voucher viewVoucher(@PathVariable("id") int id) {
		return voucherService.findVoucherByIdToan(id);
	}

	@PostMapping
	public ResponseEntity<?> addSaleToan(@RequestBody Voucher a) {
		try {
			if (voucherService.existsByVoucherCode(a.getVoucherCode())) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Mã khuyến mãi đã tồn tại");
			}
			if (a.getPercentSale() > 90) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phần trăm giảm giá không được vượt quá 90%");
			}
			a.setStatus(true);
			if (a.getQuantity() == 0) {
				a.setStatus(false);
			}
			if (a.getStartDate().after(new Date())) {
				a.setStatus(false);
			}
			a.setCreateAt(new Date());
			a.setUpdateAt(new Date());
			voucherService.addVoucherToan(a);
			return ResponseEntity.ok(a);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi không thể thêm mã khuyến mãi");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateSaleToan(@PathVariable("id") int id, @RequestBody Voucher voucher) {
		Voucher kiemTraTonTai = voucherService.findVoucherByIdToan(id);
		if (voucher.getPercentSale() > 90) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phần trăm giảm giá không được vượt quá 90%");
		}
		if (kiemTraTonTai != null) {
			if (voucher.getQuantity() == 0) {
				voucher.setStatus(false);
			} else {
				voucher.setStatus(true);
			}

			if (voucher.getEndDate().before(new Date()) || voucher.getStartDate().after(new Date())) {
				voucher.setStatus(false);
			}
			voucher.setUpdateAt(new Date());
			voucher.setVoucherId(id);
			voucherService.updateSaleToan(voucher);
			return ResponseEntity.ok(voucher);
		}
		return ResponseEntity.ok(voucher);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Voucher> deleteSaleToan(@PathVariable("id") int id) {
		Voucher kiemTraTonTai = voucherService.findVoucherByIdToan(id);
		if (kiemTraTonTai != null) {
			voucherService.deleteVoucherToan(id);
			return ResponseEntity.ok(kiemTraTonTai);
		}
		return ResponseEntity.ok(kiemTraTonTai);
	}

//MyVoucher Page
	@GetMapping("/rdVoucher")
	public ResponseEntity<List<Voucher>> getRDVoucher() {
		List<Voucher> voucher = voucherService.getRDVoucher();
		return ResponseEntity.ok(voucher);
	}
//Khác

	@GetMapping("/user/vouchers")
	public ResponseEntity<?> getMyVouchersByToken(@RequestHeader("Authorization") String authorizationHeader) {
		String token;
		String email;

		try {
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(400).body("Authorization header không hợp lệ.");
			}
			token = authorizationHeader.substring(7);
			email = jwtTokenUtils.extractEmail(token);
		} catch (Exception e) {
			return ResponseEntity.status(400).body("Token không hợp lệ.");
		}

		User user = userService.getUserByEmailToan(email);
		if (user == null) {
			return ResponseEntity.status(404).body("Người dùng không tồn tại.");
		}

		List<MyVoucherDTO> myVouchers = myVoucherService.getMyVouchersByUserId(user.getUserId());
		if (myVouchers.isEmpty()) {
			return ResponseEntity.status(404).body("Không tìm thấy voucher nào.");
		}

		// Lọc danh sách voucher còn sử dụng được
		Date currentDate = new Date();
		List<MyVoucherDTO> validVouchers = myVouchers
				.stream().filter(voucher -> voucher.getStartDate().before(currentDate)
						&& voucher.getEndDate().after(currentDate) && voucher.isStatus() == true)
				.collect(Collectors.toList());

		if (validVouchers.isEmpty()) {
			return ResponseEntity.status(404).body("Không tìm thấy voucher còn sử dụng nào.");
		}

		return ResponseEntity.ok(validVouchers);
	}

	@PostMapping("/collect/{voucherId}")
	public ResponseEntity<?> collectVoucher(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("voucherId") int voucherId) {
		String token;
		String email;

		try {
			// Kiểm tra Authorization Header
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization header không hợp lệ.");
			}

			// Trích xuất token và email
			token = authorizationHeader.substring(7);
			email = jwtTokenUtils.extractEmail(token);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ.");
		}

		// Lấy thông tin người dùng từ email
		User user = userService.getUserByEmailToan(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại.");
		}

		// Tìm voucher theo ID
		Voucher voucher = voucherService.getVoucherById(voucherId);
		if (voucher == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher không tồn tại.");
		}

		// Kiểm tra số lượng voucher còn lại
		if (voucher.getQuantity() <= 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Voucher đã hết số lượng.");
		}

		// Giảm số lượng voucher đi 1
		voucher.setQuantity(voucher.getQuantity() - 1);
		voucherService.save(voucher);

		// Thêm voucher vào bảng my_voucher
		MyVoucher myVoucher = new MyVoucher();
		myVoucher.setUser(user);
		myVoucher.setVoucher(voucher);
		myVoucher.setStatus(true); // trạng thái mặc định là true
		myVoucherService.save(myVoucher);

		return ResponseEntity.ok("Thu thập voucher thành công.");
	}

}
