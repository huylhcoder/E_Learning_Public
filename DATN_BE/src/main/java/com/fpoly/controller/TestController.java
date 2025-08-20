package com.fpoly.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fpoly.dto.AnswerDTO;
import com.fpoly.dto.QuestionDTO;
import com.fpoly.dto.TestDTO;
import com.fpoly.dto.quiz.SaveAnswerRequest;
import com.fpoly.dto.quiz.TestQuestionsDTO;
import com.fpoly.dto.quiz.TestResultDTO;
import com.fpoly.entity.Answer;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.Question;
import com.fpoly.entity.Test;
import com.fpoly.entity.User;
import com.fpoly.entity.UserAnswerHistory;
import com.fpoly.entity.UserTestResult;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.service.AnswerService;
import com.fpoly.service.CourseProgressService;
import com.fpoly.service.QuestionService;
import com.fpoly.service.TestService;
import com.fpoly.service.UserAnswerHistoryService;
import com.fpoly.service.UserService;
import com.fpoly.service.UserTestResultService;

@CrossOrigin("*")
@RestController
@RequestMapping("${api.prefix}/test")
public class TestController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserService usService;
	@Autowired
	private JwtTokenUtils jwtTokenUtils;
	@Autowired
	private TestService testService;
	@Autowired
	private QuestionService questionService;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private UserAnswerHistoryService userAnswerHistoryService;
	@Autowired
	private UserTestResultService userTestResultService;
	@Autowired
	private CourseProgressService courseProgressService;

//Quiz Page
	//Kêt quả bài kiểm tra của người dùng
	@GetMapping("/{testId}/result")
	public ResponseEntity<UserTestResult> getTestResult(@PathVariable Integer testId,
			@RequestHeader("Authorization") String token) {
		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", ""));
		return ResponseEntity.ok(testService.getOrCreateTestResult(testId, email));
	}
	
	//Hiển thị đáp án cho người dùng theo dõi
	@GetMapping("/{testId}/answers")
	public ResponseEntity<TestResultDTO> getTestResultWithAnswers(
	        @PathVariable Integer testId,
	        @RequestHeader("Authorization") String token) {
	    String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", ""));
	    return ResponseEntity.ok(testService.getTestResultWithAnswers(testId, email));
	}
	
	//Hiển thị danh sách câu hỏi cho ngươi dùng làm
	@GetMapping("/{testId}/questions")
	public ResponseEntity<TestQuestionsDTO> getTestQuestions(@PathVariable Integer testId,
			@RequestHeader("Authorization") String token) {
		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", ""));
		return ResponseEntity.ok(testService.getTestQuestions(testId, email));
	}

	//Lưu đáp án mà người dùng chọn
	@PostMapping("/{testId}/answer")
	public ResponseEntity<Void> saveAnswer(@PathVariable Integer testId, @RequestBody SaveAnswerRequest request,
			@RequestHeader("Authorization") String token) {
		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", ""));
		testService.saveUserAnswer(testId, request, email);
		return ResponseEntity.ok().build();
	}
	
	//Nộp bài kiểm tra, thực hiện chấm điểm
	@PostMapping("/{testId}/submit")
	public ResponseEntity<UserTestResult> submitTest(@PathVariable Integer testId,
			@RequestHeader("Authorization") String token) {
		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", ""));
		return ResponseEntity.ok(testService.submitTest(testId, email));
	}

	//Set score =  0  và xóa danh sách đáp án mà người dùng đã chọn
	@DeleteMapping("/{testId}/reset")
	public ResponseEntity<Void> resetTest(@PathVariable Integer testId, @RequestHeader("Authorization") String token) {
		String email = jwtTokenUtils.extractEmail(token.replace("Bearer ", ""));
		testService.resetTest(testId, email);
		return ResponseEntity.ok().build();
	}

//Khác
	// Kiểm tra đã làm bài test hay chưa => Hiển thị bài kiểm tra hoặc hiển thị kết
	// quả
	@GetMapping("/check-if-the-user-has-taken-the-quiz/{testId}")
	public ResponseEntity<?> timKiemKetQuaBaiKiemTraCuaNguoiDung(@RequestHeader("Authorization") Optional<String> token,
			@PathVariable("testId") int testId) {
		String tokenValue = token.map(t -> t.replace("Bearer ", "")).orElse("");
		String email;
		try {
			email = jwtTokenUtils.extractEmail(tokenValue);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("{\"message\": \"Token không hợp lệ!\"}");
		}

		User user = usService.getUserByEmailToan(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("{\"message\": \"Email không tồn tại!\"}");
		}

		Test test = testService.timTestTheoIdTam(testId);
		UserTestResult userTestResult = userTestResultService.findByUserAndTest(user, test);
		boolean isSubmitted = userTestResult != null;

		if (isSubmitted) {
			return ResponseEntity.ok(userTestResult);
		} else {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
					.body("{\"message\": \"Người dùng chưa làm bài kiểm tra này!\"}");
		}

	}

	// Load danh sách câu hỏi
	@GetMapping("/take-the-test/{test-id}")
	public ResponseEntity<TestDTO> timKiemQuizTheoIdTam(@PathVariable("test-id") int testId) {
		Test baiKiemTraEntity = testService.timTestTheoIdTam(testId);
		TestDTO baiKiemTraDTO = new TestDTO();
		baiKiemTraDTO.setTestID(baiKiemTraEntity.getTestId());
		baiKiemTraDTO.setCountdownTimer(baiKiemTraEntity.getCountdownTimer());

		List<Question> listQuestionEntity = questionService.timKiemDanhSachCauHoiTheoBaiQuizTam(baiKiemTraEntity);
		List<QuestionDTO> listQuestionDTO = new ArrayList<>();

		for (Question questionEntity : listQuestionEntity) {
			QuestionDTO questionDTO = new QuestionDTO();
			questionDTO.setQuestionId(questionEntity.getQuestionId());
			questionDTO.setContents(questionEntity.getContents());

			List<Answer> listAnswerEntity = answerService.timListDapAnTheoCauHoiTam(questionEntity);
			List<AnswerDTO> listAnswerDTO = new ArrayList<>();

			for (Answer answerEntity : listAnswerEntity) {
				AnswerDTO answerDTO = new AnswerDTO();
				answerDTO.setAnswerId(answerEntity.getAnswerId());
				answerDTO.setText(answerEntity.getContent());
				answerDTO.setCorrect(answerEntity.isCorrect()); // Set giá trị isCorrect từ entity

				listAnswerDTO.add(answerDTO);
			}
			questionDTO.setListAnswerDTO(listAnswerDTO);
			listQuestionDTO.add(questionDTO);
		}

		baiKiemTraDTO.setListQuestion(listQuestionDTO);
		return ResponseEntity.ok(baiKiemTraDTO);
	}

	// Lấy danh sách đáp án đã chọn
	@GetMapping("/userTestResult/{token}")
	public ResponseEntity<List<UserTestResult>> GetuserTestResultByUserHao(@PathVariable("token") String token) {
		System.out.println("Token received: " + token);
		String email;

		email = jwtTokenUtils.extractEmail(token);
		System.out.println("Extracted email: " + jwtTokenUtils.extractEmail(token));

		// Copy khúc này
		User user = userService.getUserByEmailToan(email);
		List<UserTestResult> userTestResult = userTestResultService.findUserTestResultByUserHao(user.getUserId());
		return ResponseEntity.ok(userTestResult);
	}

	@PostMapping("/submit/{testId}")
	public ResponseEntity<?> submitTest(@RequestHeader("Authorization") Optional<String> token,
			@PathVariable("testId") int testId) {
		// Lấy token và loại bỏ "Bearer "
		String tokenValue = token.map(t -> t.replace("Bearer ", "")).orElse("");
		System.out.println("------------------------Sumit Test-----------------");

		// Tìm kiếm email từ token
		String email;
		try {
			email = jwtTokenUtils.extractEmail(tokenValue);
			System.out.println("Lịch sử làm bài - Email: " + email);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("{\"message\": \"Token không hợp lệ!\"}");
		}

		// Tìm kiếm người dùng theo email
		User user = usService.getUserByEmailToan(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("{\"message\": \"Email không tồn tại!\"}");
		}

		// Tìm kiếm bài kiểm tra theo id
		Test test = testService.timTestTheoIdTam(testId);
		List<UserAnswerHistory> userAnswerHistories = userAnswerHistoryService.getAnswersByUserAndTest(user, test);
		// Hiển thị độ dài của danh sách
		long numberOfUserAnswerHistory = userAnswerHistories.stream().count();
		System.out.println("Số đáp án đã chọn: " + numberOfUserAnswerHistory);

		// Kiểm tra null
//		if (userAnswerHistories == null || userAnswerHistories.isEmpty()) {
//			return ResponseEntity.badRequest()
//					.body("{\"message\": \"Người dùng chưa chọn đáp án nào trong bài kiểm tra!\"}");
//		}

		boolean status = false;
		Date createAt = new Date();
		int numberOfCorrectAnswer = 0;
		float score = 0; // Tổng điểm

		// Lấy danh sách câu hỏi từ test
		List<Question> questions = questionService.timKiemDanhSachCauHoiTheoBaiQuizTam(test);
		// Duyệt qua từng câu hỏi và so sánh đáp án đã chọn
		for (Question question : questions) {
			// Tìm đáp án đúng cho câu hỏi
			Optional<Answer> correctAnswer = answerService.timListDapAnTheoCauHoiTam(question).stream()
					.filter(Answer::isCorrect).findFirst();

			// Tìm đáp án đã chọn từ lịch sử trả lời
			Optional<UserAnswerHistory> userAnswerHistory = userAnswerHistories.stream()
					.filter(history -> history.getQuestion().getQuestionId() == question.getQuestionId()).findFirst();

			// Nếu có đáp án đã chọn và đáp án đúng, tăng điểm
			if (userAnswerHistory.isPresent() && correctAnswer.isPresent()
					&& userAnswerHistory.get().getAnswer().getAnswerId() == correctAnswer.get().getAnswerId()) {
				numberOfCorrectAnswer++;
			}
		}

		// Tính tỷ lệ phần trăm
		score = ((float) numberOfCorrectAnswer / questions.size()) * 10;
		System.out.println("Điểm: " + score);

		// Tìm kết quả bài kiểm tra của người dùng
		UserTestResult userTestResult = userTestResultService.findByUserAndTest(user, test);

		if (userTestResult != null) {

			// Nếu đã có kết quả, cập nhật lại
			userTestResult.setScore(score);
			userTestResult.setNumberOfCorrectAnswer(numberOfCorrectAnswer);
			userTestResult.setUpdateAt(new Date());

			// Nếu người dùng đã pass thì không cập nhật trạng thái
			if (userTestResult.isStatus() == true) {
				// Không làm gì hết
			} else {
				// Điểm người dùng nhỏ hơn 5 thì rớt
				if (score < 5) {
					userTestResult.setStatus(false);// Không đạt
				} else {
					userTestResult.setStatus(true);// Đạt thì tính lại tiến độ khóa học
					courseProgressService.updateCourseProgressForTakeTheTest(user, test.getSection().getCourse());
				}
			}

			// Nếu kết quả mới làm lớn hơn kết quả lớn nhất thì cập nhật lại kết quả lớn
			// nhất
			if (score > userTestResult.getMaxScore()) {
				userTestResult.setMaxScore(score);
			}
			// Giả sử trạng thái không thay đổi
			userTestResultService.save(userTestResult);
		} else {
			// Nếu chưa có kết quả, tạo mới
			userTestResult = new UserTestResult();
			userTestResult.setUser(user);
			userTestResult.setTest(test);
			userTestResult.setNumberOfCorrectAnswer(numberOfCorrectAnswer);
			userTestResult.setScore(score);
			userTestResult.setMaxScore(score);
			userTestResult.setCreateAt(new Date());
			userTestResult.setUpdateAt(new Date());
			// Điểm người dùng nhỏ hơn 5 thì rớt
			if (score < 5) {
				userTestResult.setStatus(false);// Không đạt
			} else {
				userTestResult.setStatus(true);// Đạt thì tính lại tiến độ khóa học
				courseProgressService.updateCourseProgressForTakeTheTest(user, test.getSection().getCourse());
			}
			userTestResultService.save(userTestResult);
		}

		// Trả về kết quả
		return ResponseEntity.ok(userTestResult);
	}

	@DeleteMapping("/delete-answers/{testId}")
	public ResponseEntity<String> deleteAnswers(@RequestHeader("Authorization") Optional<String> token,
			@PathVariable("testId") int testId) {
		System.out.println(token);
		System.out.println(testId);
		// Lấy token và loại bỏ "Bearer "
		String tokenValue = token.map(t -> t.replace("Bearer ", "")).orElse("");
		String email;

		try {
			email = jwtTokenUtils.extractEmail(tokenValue);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("{\"message\": \"Token không hợp lệ!\"}");
		}

		// Tìm kiếm người dùng theo email
		User user = userService.getUserByEmailToan(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("{\"message\": \"Email không tồn tại!\"}");
		}

		// Xóa lịch sử đáp án của người dùng cho bài kiểm tra cụ thể
		boolean isDeleted = userAnswerHistoryService.deleteAnswersByUserAndTestId(user, testId);
		if (isDeleted) {
			return ResponseEntity.ok("{\"message\": \"Làm lại thành công!\"}");
		} else {
			return ResponseEntity.status(404).body("{\"message\": \"Làm lại thành công!\"}");
		}
	}

	// DTO cho yêu cầu submit
	public static class SubmitTestRequest {
		private int testID;
		private List<SelectedAnswerDTO> selectedAnswers;

		// Getters và setters
		public int getTestID() {
			return testID;
		}

		public void setTestID(int testID) {
			this.testID = testID;
		}

		public List<SelectedAnswerDTO> getSelectedAnswers() {
			return selectedAnswers;
		}

		public void setSelectedAnswers(List<SelectedAnswerDTO> selectedAnswers) {
			this.selectedAnswers = selectedAnswers;
		}
	}

	// DTO cho đáp án đã chọn
	public static class SelectedAnswerDTO {
		private int questionId;
		private int answerId;

		// Getters và setters
		public int getQuestionId() {
			return questionId;
		}

		public void setQuestionId(int questionId) {
			this.questionId = questionId;
		}

		public int getAnswerId() {
			return answerId;
		}

		public void setAnswerId(int answerId) {
			this.answerId = answerId;
		}
	}

}
