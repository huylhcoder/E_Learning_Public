package com.fpoly.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.controller.OtpController.DateTimeUtils;
import com.fpoly.dto.CommentDTO;
import com.fpoly.entity.Comment;
import com.fpoly.entity.Course;
import com.fpoly.entity.MailInfo;
import com.fpoly.entity.User;
import com.fpoly.entity.Voucher;
import com.fpoly.security.JwtTokenUtils;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.entity.Comment;
import com.fpoly.entity.Course;
import com.fpoly.entity.Reply;
import com.fpoly.entity.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.entity.Comment;
import com.fpoly.entity.Course;
import com.fpoly.service.CommentService;
import com.fpoly.service.CourseService;
import com.fpoly.service.EmailService;
import com.fpoly.service.ReplyService;
import com.fpoly.service.UserService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/comment")
public class CommentController {
	@Autowired
	private CommentService cmtservice;
	@Autowired
	private UserService userService;
	@Autowired
	private CourseService courseService;

	@Autowired
	private JwtTokenUtils jwtTokenUtils;

//Learning Page
	// Kiểm tra User có bình luận khóa học này chưa
	@GetMapping("/check/{courseId}")
	public ResponseEntity<?> checkUserComments(@PathVariable int courseId,
			@RequestHeader("Authorization") String token) {
		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", "").trim());
		User currentUser = userService.getUserByEmailToan(email);

		List<CommentDTO> comments = cmtservice.getUserComments(courseId, currentUser.getUserId());
		return ResponseEntity.ok(comments); // Trả về list (có thể rỗng nếu chưa bình luận)
	}

	// Hiển thị bình luận của khóa học hiện tại
	@GetMapping("/course/{courseId}")
	public List<Comment> findCommentByIdAndStatusHao(@PathVariable("courseId") int courseId) {
		return cmtservice.hienThiDanhGiaTheoKhoaHoc(courseId);
	}

	// Tạo bình luận mới
//	@PostMapping("")
//	public ResponseEntity<?> addComment(@RequestHeader("Authorization") String token, @RequestBody CommentDTO cmtDTO) {
//		String email = "";
//		try {
//			email = jwtTokenUtils.extractEmail(token.replace("Bearer ", "").trim());
//			System.out.println("Email: " + email);
//		} catch (Exception e) {
//			return ResponseEntity.status(400).body("Token không hợp lệ.");
//		}
//
//		// Lấy thông tin người dùng từ email
//		User user = userService.getUserByEmailToan(email);
//		if (user == null) {
//			return ResponseEntity.status(404).body("Người dùng không tồn tại.");
//		}
//
//		try {
//			// Lấy thông tin khoá học từ courseId trong CommentDTO
//			Course course = courseService.timKhoaHocTheoMaKhoaHocToan(cmtDTO.getCourseId());
//			if (course == null) {
//				return ResponseEntity.status(404).body("Khóa học không tồn tại.");
//			}
//
//			// Tạo mới Comment
//			Comment cmt = new Comment();
//			cmt.setCourse(course);
//			cmt.setUser(user); // Sử dụng người dùng từ email trong token
//			cmt.setStarRating(cmtDTO.getStarRating());
//			cmt.setContent(cmtDTO.getContent());
//			cmt.setCreateAt(new Date());
//
//			// Lưu Comment vào cơ sở dữ liệu
//			cmtservice.addCommentToan(cmt);
//
//			return ResponseEntity.ok(cmtDTO);
//		} catch (Exception e) {
//			e.printStackTrace(); // Debug thông báo lỗi
//			return ResponseEntity.status(500).body("Đã xảy ra lỗi trong quá trình thêm bình luận.");
//		}
//	}

	@PostMapping("")
	public ResponseEntity<?> addComment(@RequestHeader("Authorization") String token, @RequestBody CommentDTO cmtDTO) {
		String email = "";
		try {
			email = jwtTokenUtils.extractEmail(token.replace("Bearer ", "").trim());
			System.out.println("Email: " + email);
		} catch (Exception e) {
			return ResponseEntity.status(400).body("Token không hợp lệ.");
		}

		// Lấy thông tin người dùng từ email
		User user = userService.getUserByEmailToan(email);
		if (user == null) {
			return ResponseEntity.status(404).body("Người dùng không tồn tại.");
		}

		try {
			// Lấy thông tin khoá học
			Course course = courseService.timKhoaHocTheoMaKhoaHocToan(cmtDTO.getCourseId());
			if (course == null) {
				return ResponseEntity.status(404).body("Khóa học không tồn tại.");
			}

			// Tạo mới Comment
			Comment cmt = new Comment();
			cmt.setCourse(course);
			cmt.setUser(user);
			cmt.setStarRating(cmtDTO.getStarRating());
			cmt.setContent(cmtDTO.getContent());
			cmt.setCreateAt(new Date());

			// Lưu comment
			cmtservice.addCommentToan(cmt);

			// --- 🔽 TÍNH LẠI AVERAGE RATING & TOTAL RATE ---
			List<Comment> comments = cmtservice.findCommentByCourseToan(course);
			if (comments != null && !comments.isEmpty()) {
				double totalStars = comments.stream().mapToDouble(Comment::getStarRating).sum();
				int totalCount = comments.size();
				float avgRating = (float) (totalStars / totalCount);

				course.setTotalRate(totalCount);
				course.setAverageRating(avgRating);
				course.setUpdateAt(new Date());
				courseService.save(course); // cập nhật vào DB
			} else {
				// Nếu chưa có comment nào, reset lại
				course.setTotalRate(0);
				course.setAverageRating(0);
				courseService.save(course);
			}

			return ResponseEntity.ok("Thêm bình luận thành công và cập nhật điểm trung bình!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Đã xảy ra lỗi trong quá trình thêm bình luận.");
		}
	}

//Khác
	@GetMapping("")
	public List<Comment> getAllComments() {
		return cmtservice.getAllComment();
	}

	@PutMapping("/changeStatus/{commentId}")
	public ResponseEntity<Comment> changeStatusKhoa(@PathVariable("commentId") int commentId) {
		Comment kiemTraTonTai = cmtservice.getCommentById(commentId);
		if (kiemTraTonTai != null) {
			kiemTraTonTai.setStatus(!kiemTraTonTai.isStatus()); // Chuyển đổi trạng thái
			cmtservice.saveComment(kiemTraTonTai); // Lưu đối tượng đã cập nhật
			return ResponseEntity.ok(kiemTraTonTai); // Trả về đối tượng đã cập nhật
		}
		return ResponseEntity.notFound().build();
	}

//	 @Autowired
//	    private CommentService commentService;
//
//	    // Phương thức để lấy bình luận theo ID khóa học
//	    @GetMapping("/course/{courseId}")
//	    public List<Comment> getCommentsByCourseId(@PathVariable int courseId) {
//	        return commentService.hienThiDanhGiaTheoKhoaHoc(courseId);
//	    }

	@Autowired
	private EmailService emailService;

	@PostMapping("/cc")
	public ResponseEntity<String> sendVerificationCode(@RequestBody String email, String feedback) {
		String htmlContent = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">"
				+ "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
				+ "<title>Mã xác minh tạo tài khoản</title>"
				+ "<style>body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }"
				+ ".container { max-width: 900px; margin: 20px auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
				+ ".header { background-color: #66cc66; padding: 10px; text-align: left; color: #ffffff; font-size: 25px; font-weight: bold; }"
				+ ".title { font-size: 24px; color: #4CAF50; margin: 20px 0; text-align: center; }"
				+ ".content { font-size: 16px; color: #333; line-height: 1.6; }"
				+ ".otp { font-size: 36px; color: #4CAF50; text-align: center; margin: 20px 0; font-weight: bold; }"
				+ ".footer { font-size: 14px; color: #666; margin-top: 20px; }</style></head>"
				+ "<body><div class=\"container\"><div class=\"header\">E - Learning</div>"
				+ "<div class=\"title\">Mã xác minh tạo tài khoản mới</div>"
				+ "<div class=\"content\"><p>Xin chào bạn </p>"
				+ "<p>Chúng tôi nhận được yêu cầu tạo tài khoản mới của bạn vào lúc "
				+ DateTimeUtils.getCurrentDateTime() + "</p>"
				+ "<p>Dưới đây là mã xác nhận để tiếp tục yêu cầu của bạn:</p></div>" + "<div class=\"otp\">[ "
				+ feedback + " ]</div>" // Replace '123456' with the actual OTP value
				+ "<div class=\"footer\"><p>Vui lòng không chia mã xác nhận ra bên ngoài</p>"
				+ "<p>Nếu bạn không yêu cầu tiếp tục tạo tài khoản, vui lòng bỏ qua email này.</p>"
				+ "<p>Xin cảm ơn.</p></div></div></body></html>";
		emailService.sendOTP(new MailInfo(email, "Đặt lại mật khẩu", htmlContent));
		return ResponseEntity.ok(feedback);
	}
}
