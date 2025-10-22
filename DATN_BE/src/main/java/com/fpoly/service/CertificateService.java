package com.fpoly.service;

import com.fpoly.controller.OtpController.DateTimeUtils;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.MailInfo;
import com.fpoly.entity.User;
import com.fpoly.repository.CourseProgressRepository;
import com.fpoly.repository.CourseRepository;
import com.fpoly.repository.UserRepository;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

@Service
public class CertificateService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private CourseRepository courseReponsitory;

	@Autowired
	private CourseProgressRepository courseProgressRepository;

	@Autowired
	private JwtTokenUtils jwtTokenUtil;

	@Autowired
	private UserRepository userRepository;

	@Value("${spring.mail.username}")
	private String senderEmail;

	private static final String TEMP_CERT_DIR = "temp_certificates/";

	/**
	 * Xử lý logic tạo, gửi và lưu chứng chỉ (nhận token thay vì User object)
	 * * @param courseId ID khóa học
	 * 
	 * @param token JWT token đầy đủ (ví dụ: "Bearer eyJ...")
	 * @return Byte array của ảnh chứng chỉ
	 */
	public byte[] generateAndSendCertificate(int courseId, String token) throws Exception {

		// --- 1. LẤY USER TỪ TOKEN ---
		String jwt = token.replace("Bearer ", "");
		String email = jwtTokenUtil.extractEmail(jwt);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));

		// --- 2. KIỂM TRA TIẾN ĐỘ ---
		Optional<CourseProgress> progressOptional = courseProgressRepository
				.findByUser_UserIdAndCourse_CourseId(user.getUserId(), courseId);

		if (progressOptional.isEmpty()) {
			throw new IllegalArgumentException("Người dùng chưa đăng ký khóa học này.");
		}

		CourseProgress progress = progressOptional.get();

		if (progress.getProgressPercentage() < 100.0f) {
			throw new IllegalStateException("Tiến độ khóa học chưa đạt 100%.");
		}

		// Bạn có thể thêm logic kiểm tra `if (progress.isHasCertificate())` ở đây
		// để quyết định có gửi email lại hay không.

		// --- 3. LẤY THÔNG TIN CẦN THIẾT ---
		Course course = courseReponsitory.findByCourseId(courseId);

		if (course == null) {
			throw new IllegalArgumentException("Không tìm thấy khóa học với ID: " + courseId);
		}

		String courseName = course.getName();
		String userName = user.getName();
		String userEmail = user.getEmail();

		// --- 4. TẠO ẢNH CHỨNG CHỈ VÀ LƯU TẠM THỜI ---
		byte[] certificateImageBytes = generateCertificateImage(userName, courseName);
		String certificatePath = null;
		Path tempFilePath = null;

		try {
			Path tempDir = Paths.get(TEMP_CERT_DIR);
			if (!Files.exists(tempDir)) {
				Files.createDirectories(tempDir);
			}

			// Tạo tên file tạm thời và lưu
			String tempFileName = "certificate_" + userName.replaceAll("\\s+", "_") + "_" + courseId + ".png";
			tempFilePath = tempDir.resolve(tempFileName);
			Files.write(tempFilePath, certificateImageBytes);
			certificatePath = tempFilePath.toString();

			// --- 5. GỬI EMAIL CHÚC MỪNG ---
			String htmlContent = generateCertificateEmailHtml(userName, courseName);

			// Tạo MailInfo (Gửi từ email cấu hình tới email của User)
			MailInfo mailInfo = new MailInfo(userEmail,
					"Chúc mừng! Chứng chỉ hoàn thành khóa học " + courseName + " 🎓", htmlContent);
			mailInfo.setFrom(senderEmail); // Thiết lập người gửi

			emailService.sendCertificateWithAttachment(mailInfo, certificatePath);

		} catch (IOException e) {
			System.err.println("Lỗi khi tạo/lưu file hoặc gửi email: " + e.getMessage());
			throw new RuntimeException("Lỗi hệ thống khi cấp phát chứng chỉ.", e);
		} finally {
			// RẤT QUAN TRỌNG: Dọn dẹp file tạm
			if (tempFilePath != null) {
				Files.deleteIfExists(tempFilePath);
			}
		}

		// --- 6. TRẢ VỀ byte[] ẢNH CHỨNG CHỈ CHO FRONTEND ---
		return certificateImageBytes;
	}

	/**
	 * PHƯƠNG THỨC TẠO HTML EMAIL
	 */
	private String generateCertificateEmailHtml(String userName, String courseName) {
		String htmlContent = "<!DOCTYPE html><html lang=\"vi\"><head><meta charset=\"UTF-8\">"
				+ "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
				+ "<title>Chúc mừng hoàn thành khóa học</title>" + "<style>"
				+ "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }"
				+ ".container { max-width: 100%; margin: 20px auto; padding: 20px; background-color: #ffffff; "
				+ "border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); border-top: 5px solid #ffcc00; }"
				+ ".header { background-color: #ffcc00; padding: 15px; text-align: left; color: #333; font-size: 20px; font-weight: bold; }"
				+ ".content { font-size: 16px; color: #333; line-height: 1.6; margin-top: 20px; }"
				+ ".content p { margin: 10px 0; }" + ".footer { font-size: 14px; color: #666; margin-top: 20px; }"
				+ ".highlight { color: #007bff; font-weight: bold; }" + "</style></head>" + "<body>"
				+ "<div class=\"container\">"
				+ "<div class=\"header\">🏆 E - Learning - CHÚC MỪNG HOÀN THÀNH KHÓA HỌC</div>"
				+ "<div class=\"content\">" + "<p>Kính gửi <strong class=\"highlight\">" + userName + "</strong>,</p>"
				+ "<p>Chúng tôi vô cùng vui mừng thông báo rằng bạn đã <strong class=\"highlight\">hoàn thành xuất sắc khóa học "
				+ courseName + "</strong> vào lúc <strong>" + DateTimeUtils.getCurrentDateTime() + "</strong>.</p>"
				+ "<p>Đây là một thành tựu đáng tự hào!</p>"
				+ "<p>Chứng chỉ hoàn thành khóa học của bạn đã được đính kèm trong email này.</p>"
				+ "<p>Hãy tiếp tục hành trình học tập và khám phá các khóa học khác nhé!</p>" + "<p>Trân trọng,</p>"
				+ "<p>Đội ngũ E-Learning.</p>" + "</div>" + "</div>" + "</body></html>";
		return htmlContent;
	}

	/**
	 * PHƯƠNG THỨC TẠO ẢNH CHỨNG CHỈ (Cần hiện thực) * @param userName Tên người
	 * dùng để in lên chứng chỉ
	 * 
	 * @param courseName Tên khóa học để in lên chứng chỉ
	 * @return Byte array của ảnh PNG/JPG chứng chỉ
	 */
//	private byte[] generateCertificateImage(String userName, String courseName) throws IOException {
//		// 💡 TODO: Đây là nơi bạn cần triển khai logic thực tế để tạo ra file ảnh/PDF
//		// của chứng chỉ (ví dụ: dùng Java2D để vẽ lên template hoặc dùng iText/PDFBox).
//
//		System.out.println("DEBUG: Tạo chứng chỉ cho " + userName + " - Khóa học: " + courseName);
//
//		// Tạm thời trả về mảng byte rỗng.
//		return new byte[0];
//	}
	/**
	 * PHƯƠNG THỨC TẠO ẢNH CHỨNG CHỈ Lấy ảnh khung (canvas) và điền thông tin người
	 * dùng, khóa học, và chữ ký (Huy). * @param userName Tên người dùng để in lên
	 * chứng chỉ
	 * 
	 * @param courseName Tên khóa học để in lên chứng chỉ
	 * @return Byte array của ảnh PNG chứng chỉ
	 */
	private byte[] generateCertificateImage(String userName, String courseName) throws IOException {
		// Kích thước chứng chỉ (Nguyên tắc: Tỉ lệ 7:5 hoặc 4:3, ở đây dùng 1000x700)
		final int WIDTH = 1000;
		final int HEIGHT = 700;

		BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		// Bật chế độ Anti-aliasing để chữ không bị răng cưa
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// --- 1. VẼ NỀN VÀ KHUNG VIỀN ---

		// Nền trắng kem
		g2d.setColor(new Color(255, 253, 245));
		g2d.fillRect(0, 0, WIDTH, HEIGHT);

		// Khung viền màu vàng đồng (Gold/Bronze)
		Color goldColor = new Color(196, 164, 132); // #C4A484
		g2d.setColor(goldColor);
		g2d.setStroke(new BasicStroke(10)); // Độ dày viền 10px
		g2d.drawRect(20, 20, WIDTH - 40, HEIGHT - 40);

		// --- 2. VẼ NỘI DUNG TĨNH ---

		// Tiêu đề: Tên Website
		g2d.setFont(new Font("Serif", Font.BOLD, 30));
		g2d.setColor(new Color(30, 30, 30));
		g2d.drawString("E-Learning", 50, 70);

		// Tên chứng chỉ
		g2d.setFont(new Font("Serif", Font.BOLD, 48));
		String certTitle = "CERTIFICATE OF COMPLETION";
		FontMetrics fmTitle = g2d.getFontMetrics();
		int xTitle = (WIDTH - fmTitle.stringWidth(certTitle)) / 2;
		g2d.drawString(certTitle, xTitle, 150);

		// Dòng giới thiệu
		g2d.setFont(new Font("SansSerif", Font.PLAIN, 20));
		String subtitle = "This certifies that";
		FontMetrics fmSub = g2d.getFontMetrics();
		int xSub = (WIDTH - fmSub.stringWidth(subtitle)) / 2;
		g2d.drawString(subtitle, xSub, 220);

		// --- 3. VẼ NỘI DUNG ĐỘNG ---

		// Tên người dùng
		g2d.setFont(new Font("SansSerif", Font.BOLD, 40));
		g2d.setColor(new Color(0, 123, 255)); // Màu xanh blue nổi bật
		FontMetrics fmUser = g2d.getFontMetrics();
		int xUser = (WIDTH - fmUser.stringWidth(userName)) / 2;
		g2d.drawString(userName, xUser, 320);

		// Đã hoàn thành khóa học
		g2d.setColor(new Color(30, 30, 30));
		g2d.setFont(new Font("SansSerif", Font.PLAIN, 20));
		String achievement = "has successfully completed the course";
		FontMetrics fmAchieve = g2d.getFontMetrics();
		int xAchieve = (WIDTH - fmAchieve.stringWidth(achievement)) / 2;
		g2d.drawString(achievement, xAchieve, 380);

		// Tên khóa học
		g2d.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 30));
		g2d.setColor(new Color(255, 140, 0)); // Màu cam
		String xCourse = courseName.toUpperCase();
		FontMetrics fmCourse = g2d.getFontMetrics();
		int xCourseX = (WIDTH - fmCourse.stringWidth(xCourse)) / 2;
		g2d.drawString(xCourse, xCourseX, 450);

		// --- 4. CHỮ KÝ VÀ NGÀY THÁNG ---

		// Ngày cấp
		g2d.setColor(new Color(30, 30, 30));
		g2d.setFont(new Font("SansSerif", Font.PLAIN, 18));
		String dateLabel = "Date Issued: " + DateTimeUtils.getCurrentDateTime();
		g2d.drawString(dateLabel, 100, 600);

		// Chữ ký (Signature) - Dòng kẻ
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(1));
		g2d.drawLine(700, 600, 900, 600);

		// Tên người ký (Huy)
		g2d.setFont(new Font("Monospaced", Font.BOLD | Font.ITALIC, 24));
		g2d.drawString("Huy", 750, 585);

		// Chức danh
		g2d.setFont(new Font("SansSerif", Font.PLAIN, 16));
		g2d.drawString("CEO, E-Learning", 730, 620);

		// --- 5. CHUYỂN THÀNH BYTE ARRAY ---

		g2d.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Ghi ảnh vào stream dưới định dạng PNG
		ImageIO.write(bufferedImage, "png", baos);

		return baos.toByteArray();
	}
}
