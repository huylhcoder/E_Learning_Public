package com.fpoly.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpoly.dto.quiz.AnswerDTO;
import com.fpoly.dto.quiz.AnswerResultDTO;
import com.fpoly.dto.quiz.QuestionDTO;
import com.fpoly.dto.quiz.QuestionResultDTO;
import com.fpoly.dto.quiz.SaveAnswerRequest;
import com.fpoly.dto.quiz.TestQuestionsDTO;
import com.fpoly.dto.quiz.TestResultDTO;
import com.fpoly.entity.Answer;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.Lesson;
import com.fpoly.entity.Question;
import com.fpoly.entity.Section;
import com.fpoly.entity.Test;
import com.fpoly.entity.User;
import com.fpoly.entity.UserAnswerHistory;
import com.fpoly.entity.UserTestResult;
import com.fpoly.repository.AnswerRepository;
import com.fpoly.repository.CourseProgressRepository;
import com.fpoly.repository.QuestionRepository;
import com.fpoly.repository.TestRepository;
import com.fpoly.repository.UserAnswerHistoryRepository;
import com.fpoly.repository.UserRepository;
import com.fpoly.repository.UserTestResultRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {
	@Autowired
	private TestRepository testRepo;

	private final TestRepository testRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;
	private final UserRepository userRepository;
	private final UserTestResultRepository userTestResultRepository;
	private final UserAnswerHistoryRepository userAnswerHistoryRepository;
	private final CourseProgressRepository courseProgressRepository;

//Quiz Page
	public UserTestResult getOrCreateTestResult(Integer testId, String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		// Tìm kết quả đã có
		UserTestResult result = userTestResultRepository.findByTest_TestIdAndUser_UserId(testId, user.getUserId())
				.orElse(null);
		System.out.println("Trạng thái bài kiểm tra tại getOrCreateTestResult: " + result.isStatus());

		// Nếu đã có thì trả về kết quả cho người dùng liền
		if (result != null) {
			System.out.println("Đã có result");
			return result;
		}

		// Nếu chưa có thì tạo mới
		Test test = testRepository.findById(testId).orElseThrow(() -> new RuntimeException("Test not found"));

		UserTestResult newResult = new UserTestResult();
		newResult.setUser(user);
		newResult.setTest(test);
		newResult.setScore(0);// Điểm mặc định sẽ là 0 = Chưa làm kiểm tra || Làm bài lại
		newResult.setCompletionTime(0);
		newResult.setNumberOfCorrectAnswer(0);
		newResult.setStatus(false);
		newResult.setCreateAt(new Date());
		newResult.setUpdateAt(new Date());
		newResult.setMaxScore(0);
		newResult.setStartTime(new Date()); // Đánh dấu thời điểm bắt đầu test
		newResult.setEndTime(null);

		// Lưu DB
		return userTestResultRepository.save(newResult);
	}
	
	@Transactional
	public TestQuestionsDTO getTestQuestions(Integer testId, String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		Test test = testRepository.findById(testId).orElseThrow(() -> new RuntimeException("Test not found"));

		// lấy lịch sử câu trả lời
		List<UserAnswerHistory> userAnswers = userAnswerHistoryRepository.findByTest_TestIdAndUser_UserId(testId,
				user.getUserId());

		// tìm kết quả thi (UserTestResult)
		UserTestResult userTestResult = userTestResultRepository.findByTest_TestIdAndUser_UserId(testId, user.getUserId())
				.orElse(null);
		UserTestResult updateResult;
		if (userTestResult == null) {
			// lần đầu mở đề → tạo mới
			updateResult = new UserTestResult();
			updateResult.setUser(user);
			updateResult.setTest(test);
			updateResult.setStartTime(new Date()); // lần đầu => set start
			updateResult.setStatus(false);
			updateResult.setCreateAt(new Date());
			updateResult = userTestResultRepository.save(updateResult);
		} else {
			updateResult = userTestResult;
			// Nếu đã nộp rồi (có endTime) => cho phép làm lại thì reset
			if (updateResult.getEndTime() != null) {
				updateResult.setStartTime(new Date()); // reset thời gian bắt đầu
				updateResult.setEndTime(null); // reset thời gian kết thúc
				updateResult.setScore(0);
				updateResult.setNumberOfCorrectAnswer(0);
				updateResult.setCompletionTime(0);
//				updateResult.setStatus(false);
				updateResult.setUpdateAt(new Date());
				updateResult = userTestResultRepository.save(updateResult);
			}
			// Nếu chưa nộp thì giữ nguyên startTime để không mất thời gian làm dở
		}

		return TestQuestionsDTO.builder().testId(test.getTestId()).title(test.getTitle())
				.description(test.getDescription())
				.questions(mapQuestionsWithUserAnswers(test.getListQuestion(), userAnswers))
				.startTime(updateResult.getStartTime()).endTime(updateResult.getEndTime()).build();
	}

	// Hiển thị đáp án cho người tham khảo
	public TestResultDTO getTestResultWithAnswers(Integer testId, String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		Test test = testRepository.findById(testId).orElseThrow(() -> new RuntimeException("Test not found"));

		// Lấy danh sách câu hỏi trong test
		List<Question> questions = questionRepository.findByTest_TestId(testId);

		// Lấy lịch sử trả lời của user
		List<UserAnswerHistory> histories = userAnswerHistoryRepository.findByUserAndTest(user, test);

		Map<Integer, Integer> userAnswers = histories.stream().collect(Collectors.groupingBy(
				uah -> uah.getQuestion().getQuestionId(), // key: questionId
				Collectors.collectingAndThen(
						Collectors.maxBy(
								Comparator.comparing(u -> Optional.ofNullable(u.getCreateAt()).orElse(new Date(0)))),
						opt -> opt.map(u -> u.getAnswer().getAnswerId()).orElse(null))));

		// Build DTO
		List<QuestionResultDTO> questionDTOs = questions.stream().map(q -> {
			List<AnswerResultDTO> answerDTOs = q.getListAnswer().stream().map(a -> {
				boolean isSelected = Objects.equals(userAnswers.getOrDefault(q.getQuestionId(), -1), a.getAnswerId());
				return new AnswerResultDTO(a.getAnswerId(), a.getContent(), a.isCorrect(), isSelected);
			}).collect(Collectors.toList());

			QuestionResultDTO qDTO = new QuestionResultDTO();
			qDTO.setId(q.getQuestionId());
			qDTO.setContent(q.getContents());
			qDTO.setAnswers(answerDTOs);
			return qDTO;
		}).collect(Collectors.toList());

		TestResultDTO dto = new TestResultDTO();
		dto.setTestId(test.getTestId());
		dto.setTestName(test.getTitle()); // kiểm tra entity Test có field name hay không
		dto.setQuestions(questionDTOs);

		return dto;
	}

	
	@Transactional
	public void saveUserAnswer(Integer testId, SaveAnswerRequest request, String email) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    Answer answer = answerRepository.getReferenceById(request.getAnswerId());

	    // Kiểm tra đã có lịch sử chưa
	    Optional<UserAnswerHistory> optionalHistory =
	            userAnswerHistoryRepository.findByUserAndTestAndQuestion(
	                    user,
	                    testRepository.getReferenceById(testId),
	                    questionRepository.getReferenceById(request.getQuestionId())
	            );

	    UserAnswerHistory userAnswerHistory;

	    if (optionalHistory.isPresent()) {
	        // Nếu đã tồn tại thì update
	        userAnswerHistory = optionalHistory.get();
	        userAnswerHistory.setAnswer(answer);
	        userAnswerHistory.setCorrect(answer.isCorrect());
	    } else {
	        // Nếu chưa có thì tạo mới
	        userAnswerHistory = new UserAnswerHistory();
	        userAnswerHistory.setUser(user);
	        userAnswerHistory.setTest(testRepository.getReferenceById(testId));
	        userAnswerHistory.setQuestion(questionRepository.getReferenceById(request.getQuestionId()));
	        userAnswerHistory.setAnswer(answer);
	        userAnswerHistory.setCorrect(answer.isCorrect());
	        userAnswerHistory.setCreateAt(new Date());
	    }

	    userAnswerHistoryRepository.save(userAnswerHistory);
	}


	@Transactional
	public UserTestResult submitTest(Integer testId, String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		Test test = testRepository.findById(testId).orElseThrow(() -> new RuntimeException("Test not found"));

		// Lấy danh sách câu trả lời
		List<UserAnswerHistory> userAnswers = userAnswerHistoryRepository.findByTest_TestIdAndUser_UserId(testId,
				user.getUserId());

		// Tính điểm
		int correctAnswers = (int) userAnswers.stream().filter(ua -> ua.getAnswer().isCorrect()).count();

		int totalQuestions = test.getListQuestion().size();
		float maxScoreValue = 10; // hoặc test.getMaxScore()
		float score = ((float) correctAnswers / totalQuestions) * maxScoreValue;

		// Lúc submit thì chắc chắn đã có UserTestResult (do getResult đã tạo nếu chưa
		// có)
		UserTestResult result = userTestResultRepository.findByUser_UserIdAndTest_TestId(user.getUserId(), testId)
				.orElseThrow(() -> new RuntimeException("Test result not found"));

		// Cập nhật kết quả
		result.setScore(score);
		result.setNumberOfCorrectAnswer(correctAnswers);
		result.setEndTime(new Date());

		// cập nhật maxScore nếu cao hơn
		if (score > result.getMaxScore()) {
			result.setMaxScore(score);
		}
		System.out.println("Trạng thái kiểm tra: " + result.isStatus());
		// Nếu đã pass rồi thì giữ nguyên status
		if (result.isStatus()) {
			// giữ nguyên, không làm gì cả
			System.out.println("Trạng thái true: " + result.isStatus());
			result.setStatus(true);
		} else {
			// Nếu chưa pass thì check điểm lần này
			if (score >= 9) {
				result.setStatus(true);
				updateCourseProgress(user, test);
			}
		}

		// Tính completionTime
		if (result.getStartTime() != null && result.getEndTime() != null) {
			long durationMillis = result.getEndTime().getTime() - result.getStartTime().getTime();
			float durationSeconds = durationMillis / 1000f;
			result.setCompletionTime(durationSeconds);
		}

		result.setUpdateAt(new Date());

		System.out.println("Tổng số câu hỏi: " + totalQuestions);
		System.out.println("Số câu đúng: " + correctAnswers);
		System.out.println("Trạng  thái khi submit: " + result.isStatus());

		return userTestResultRepository.save(result);
	}

	private void updateCourseProgress(User user, Test test) {
		Course course = test.getSection().getCourse(); // giả sử Section có Course
		CourseProgress progress = courseProgressRepository
				.findByUser_UserIdAndCourse_CourseId(user.getUserId(), course.getCourseId())
				.orElseThrow(() -> new RuntimeException("CourseProgress not found"));

		progress.setTotalTestComplete(progress.getTotalTestComplete() + 1);

		// Tính lại % hoàn thành
		int total = progress.getTotalLession() + progress.getTotalQuiz();
		int completed = progress.getTotalLessionComplete() + progress.getTotalTestComplete();
		float percentage = (total == 0) ? 0 : (completed * 100f / total);
		progress.setProgressPercentage(percentage);

		courseProgressRepository.save(progress);
	}

	@Transactional
	public void resetTest(Integer testId, String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		// Xóa toàn bộ câu trả lời
		userAnswerHistoryRepository.deleteByTest_TestIdAndUser_UserId(testId, user.getUserId());

		// Tìm kết quả làm bài
		UserTestResult result = userTestResultRepository.findByTest_TestIdAndUser_UserId(testId,
				user.getUserId()).get();

		System.out.println("Trạng thái khi reset: " + result.isStatus());

		if (result != null) {
			UserTestResult updateResult = result;
			updateResult.setStatus(result.isStatus());// Giữ nguyên trạng thái
			updateResult.setScore(0);
			updateResult.setCompletionTime(0);
			updateResult.setNumberOfCorrectAnswer(0);
			updateResult.setUpdateAt(new Date());
			updateResult.setStartTime(new Date());
			userTestResultRepository.save(updateResult);
		}
	}

	private List<QuestionDTO> mapQuestionsWithUserAnswers(List<Question> questions,
			List<UserAnswerHistory> userAnswers) {
		// Tạo map chứa câu trả lời của user: questionId -> answerId
		Map<Integer, Integer> userAnswerMap = userAnswers.stream()
				.collect(Collectors.toMap(ua -> ua.getQuestion().getQuestionId(), ua -> ua.getAnswer().getAnswerId(),
						// Nếu có duplicate key, giữ lại giá trị mới nhất
						(existing, replacement) -> replacement));

		// Map questions sang QuestionDTO
		return questions.stream().map(question -> {
			// Map answers sang AnswerDTO
			List<AnswerDTO> answerDTOs = question.getListAnswer().stream()
					.map(answer -> AnswerDTO.builder().answerId(answer.getAnswerId()).text(answer.getContent()).build())
					.collect(Collectors.toList());

			// Build QuestionDTO với đầy đủ thông tin
			return QuestionDTO.builder().questionId(question.getQuestionId()).contents(question.getContents())
					.listAnswerDTO(answerDTOs).selectedAnswerId(userAnswerMap.get(question.getQuestionId())).build();
		}).collect(Collectors.toList());
	}

	private float calculateCompletionTime(List<UserAnswerHistory> userAnswers) {
		if (userAnswers.isEmpty()) {
			return 0f;
		}

		Date firstAnswer = userAnswers.stream().map(UserAnswerHistory::getCreateAt).min(Date::compareTo).orElse(null);

		Date lastAnswer = userAnswers.stream().map(UserAnswerHistory::getCreateAt).max(Date::compareTo).orElse(null);

		if (firstAnswer == null || lastAnswer == null) {
			return 0f;
		}

		// Tính thời gian làm bài (đơn vị phút)
		long diffInMillis = lastAnswer.getTime() - firstAnswer.getTime();
		return (float) diffInMillis / (1000 * 60); // Chuyển đổi từ milliseconds sang phút
	}

//Khác
	public Test timTestTheoIdTam(int testId) {
		Test baiKiemTraCuaSection = testRepo.findByTestId(testId);
		return baiKiemTraCuaSection;
	}

	public List<Test> hienThiTestTheoSection(Section sectionEntity) {
		List<Test> listTest = testRepo.findBySection(sectionEntity);
		return listTest;
	}

	// Tìm bài quiz có mã nhỏ nhất của section
	public Test timKiemBaiQuizNhoNhatCuaSection(Section section) {
		return testRepo.findFirstBySectionOrderByTestIdAsc(section);
	}

	// Thêm câu hỏi mới
	public Test addTest(Test test) {
		return testRepo.save(test);
	}

	public Test saveTest(Test test) {
		return testRepo.save(test);
	}

	public void saveAll(List<Test> tests) {
		testRepository.saveAll(tests);
	}

}
