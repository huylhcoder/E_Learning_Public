package com.fpoly.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.dto.RegisteredCourseDTO;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.Payment;
import com.fpoly.entity.RegisteredCourse;
import com.fpoly.entity.User;
import com.fpoly.entity.Voucher;
import com.fpoly.repository.AnswerRepository;
import com.fpoly.repository.CartRepository;
import com.fpoly.repository.CourseProgressRepository;
import com.fpoly.repository.CourseRepository;
import com.fpoly.repository.RegisteredCourseRepository;
import com.fpoly.repository.UserRepository;

@Service
public class RegisteredCourseService {
	@Autowired
	RegisteredCourseRepository registeredCourseSv;
	@Autowired
	CourseRepository courseRepository;
	@Autowired
	CourseProgressRepository courseProgressRepository;
	@Autowired
	UserRepository userRepository;

//MyCourse Page
	public List<RegisteredCourseDTO> getRegisteredCoursesWithProgress(int userId) {
		List<RegisteredCourse> registeredCourses = registeredCourseRepository.findByUser(userId);

		return registeredCourses.stream().map(rc -> {
			RegisteredCourseDTO dto = new RegisteredCourseDTO();
			dto.setRegisteredCourseId(rc.getRegisteredCourseId());
			dto.setCreateAt(rc.getCreateAt());

			// Lấy thông tin khóa học
			Course c = rc.getCourse();
			dto.setCourseId(c.getCourseId());
			dto.setCourseName(c.getName());
			dto.setAvatar(c.getAvatar());
			dto.setPrice(c.getPrice());
			dto.setAverageRating(c.getAverageRating());
			dto.setNumberOfLesson(c.getNumberOfLesson());
			dto.setCourseDuration(c.getCourseDuration());

			// Lấy tiến độ
			CourseProgress cp = courseProgressRepository.findByUserAndCourse(rc.getUser(), c);
			if (cp != null) {
				dto.setTotalLession(cp.getTotalLession());
				dto.setTotalQuiz(cp.getTotalQuiz());
				dto.setTotalLessionComplete(cp.getTotalLessionComplete());
				dto.setTotalTestComplete(cp.getTotalTestComplete());
				dto.setProgressPercentage(cp.getProgressPercentage());
				dto.setProgressStatus(cp.getProgressStatus());
			}

			return dto;
		}).collect(Collectors.toList());
	}

	public List<RegisteredCourse> findRegisteredCourseByUserHao(int userId) {
		return registeredCourseSv.findByUser(userId);
	}

//Khác
	// Code của HBao
	// Danh sách khóa học đã đăng ký
	public List<RegisteredCourse> findRegisterCourseByUserId(int userId) {
		return registeredCourseSv.findActiveCoursesByUser(userId);
	}

	// Danh sách khóa học đã bán
	public List<Object[]> getRegisteredCourses() {
		return registeredCourseSv.findRegisteredCourses();
	}

	// thêm khóa học vào khóa học của tôi
	public RegisteredCourse addRegisteredCourse(int courseId, int userId) {
		// Kiểm tra xem khóa học có tồn tại không
		Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

		// Kiểm tra xem người dùng có tồn tại không
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User  not found"));

		// Kiểm tra xem khóa học đã được đăng ký hay chưa
		List<RegisteredCourse> existingCourses = registeredCourseSv.findCourseRegisteredByUserId(userId);
		for (RegisteredCourse registeredCourse : existingCourses) {
			if (registeredCourse.getCourse().getCourseId() == courseId) {
				throw new RuntimeException("Course already registered");
			}
		}
		Payment payment = new Payment();
		payment.setPaymentId(1);

		// Nếu không có, thêm khóa học vào bảng đăng ký
		RegisteredCourse registeredCourse = new RegisteredCourse();
		registeredCourse.setUser(user);
		registeredCourse.setCourse(course);
		registeredCourse.setPayment(payment); // payment_id = NULL
		registeredCourse.setPrice(0); // price = 0
		registeredCourse.setStatusComment(true); // status_comment = 1
		registeredCourse.setCreateAt(new Date()); // create_at = ngày hiện tại
		registeredCourse.setStatusPayment(true);
		return registeredCourseSv.save(registeredCourse);
	}
	// hết code của HBao

//	public List<RegisteredCourse> findRegisteredCourseByUserHao(int userId) {
//		return registeredCourse.findByUser(userId);
//	}

	@Autowired
	private RegisteredCourseRepository registeredCourseRepository; // Repository để truy vấn dữ liệu

	public boolean isUserEnrolled(String email, Integer courseId) {
		// Lấy danh sách các khóa học mà người dùng đã đăng ký
		List<RegisteredCourse> registeredCourses = registeredCourseRepository.findByUserEmail(email);

		// Kiểm tra xem có khóa học nào có courseId tương ứng không
		return registeredCourses.stream()
				.anyMatch(registeredCourse -> registeredCourse.getCourse().getCourseId() == courseId);
	}

	// thêm khóa học vào khóa học của tôi
	public RegisteredCourse saverRgisteredCourse(RegisteredCourse registeredCourse) {
		// Kiểm tra xem khóa học đã được đăng ký hay chưa
		return registeredCourseSv.save(registeredCourse);
	}

	// Tìm kiếm danh sách khóa học đã đăng ký theo Payment
	public List<RegisteredCourse> findRegisterCourseByPayment(Payment payment) {
		return registeredCourseRepository.findByPayment(payment);
	}

}
