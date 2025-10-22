package com.fpoly.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fpoly.cloudinary.VideoService;
import com.fpoly.dto.AnswerDTO;
import com.fpoly.dto.CourseDetailManagerDTO;
import com.fpoly.dto.QuestionDTO;
import com.fpoly.dto.TestDTO;
import com.fpoly.entity.Answer;
import com.fpoly.entity.Course;
import com.fpoly.entity.Lesson;
import com.fpoly.entity.Question;
import com.fpoly.entity.Section;
import com.fpoly.entity.Test;
import com.fpoly.entity.User;
import com.fpoly.service.AnswerService;
import com.fpoly.service.CourseService;
import com.fpoly.service.LessonService;
import com.fpoly.service.QuestionService;
import com.fpoly.service.SectionService;
import com.fpoly.service.TestService;

import io.jsonwebtoken.io.IOException;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin("*") // cho phép bên ngoài truy xuất vào thoải mái k ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/section-manager")
public class SectionManagerController {
	@Autowired
	private CourseService courseService;
	@Autowired
	private SectionService sectionService;
	@Autowired
	private LessonService lessonService;
	@Autowired
	private TestService testService;
	@Autowired
	private QuestionService questionService;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private VideoService videoService;

	/////////////////////////////////// API liên quan đến Phần
	@GetMapping("/course/{courseId}")
	public ResponseEntity<?> getListSectionOfCourse(@PathVariable("courseId") int courseId) {
		System.out.println(courseId);
		//
		Course course = new Course();
		course = courseService.timKhoaHocTheoMaKhoaHocHuy(courseId).orElse(null);
		// Không tim thấy khóa học
		if (course == null) {
			return ResponseEntity.notFound().build();
		}
		//
		List<Section> listSection = new ArrayList<>();
		listSection = sectionService.findListSectionOfCourse_Huy(course);
		return ResponseEntity.ok(listSection);
	}

	@PostMapping("/course/{courseId}")
	public ResponseEntity<?> addSection(@PathVariable("courseId") int courseId) {
		Course course = new Course();
		course = courseService.timKhoaHocTheoMaKhoaHocHuy(courseId).orElse(null);
		// Tạo khóa học mới
		Section section = new Section();
		section.setCourse(course);
		section.setCreateAt(new Date());
		section.setUpdateAt(new Date());
		sectionService.createSection_Huy(section);
		// Khóa học mới tạo
		Section sectionMoiTao = sectionService.getLatestSection();
		return ResponseEntity.ok(sectionMoiTao.getSectionId());
	}

	@GetMapping("/{sectionId}")
	public ResponseEntity<?> getSectionDetail(@PathVariable("sectionId") int sectionId) {
		Section section = new Section();
		section = sectionService.getSectionById_Huy(sectionId).orElse(null);
		return ResponseEntity.ok(section);
	}

	// Lưu thông tin phần
	@PutMapping("/{sectionId}")
	public ResponseEntity<?> updateSection(@PathVariable("sectionId") int sectionId, @RequestBody Section section) {
		// Kiểm tra tồn tại
		Section sectionTonTai = sectionService.getSectionById_Huy(sectionId).orElse(null);
		if (sectionTonTai == null) {
			return ResponseEntity.notFound().build();
		}
		System.out.println(section.getName());

		// Cập nhật thông tin
		sectionTonTai.setName(section.getName());
		sectionTonTai.setDescription(section.getDescription());
		sectionTonTai.setUpdateAt(new Date()); // Cập nhật thời gian sửa đổi

		// Lưu lại thông tin đã cập nhật
		sectionService.updateSection_Huy(sectionTonTai);

		return ResponseEntity.ok(sectionTonTai);
	}

	//////////////////////////// Kết thúc API Phần

	/////////////////////////// API bài học
	@GetMapping("/{sectionId}/show-list-section")
	public ResponseEntity<?> showListLessonOfSection(@PathVariable("sectionId") int sectionId) {
		//
		Section section = new Section();
		section = sectionService.getSectionById_Huy(sectionId).orElse(null);
		// Không tim thấy khóa học
		if (section == null) {
			return ResponseEntity.notFound().build();
		}
		//
		List<Lesson> listLesson = new ArrayList<>();
		listLesson = lessonService.getLessonByLesson(section);
		return ResponseEntity.ok(listLesson);
	}

	// Thêm mới bài học
	@PostMapping(value = "/{sectionId}/add-lesson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addLesson(@PathVariable("sectionId") int sectionId,
			@RequestParam("name") Optional<String> name, @RequestParam("videoDuration") Optional<Float> videoDuration,
			@RequestParam("description") Optional<String> description,
			@RequestParam(value = "file", required = false) MultipartFile video)
			throws IOException, java.io.IOException {
		System.out.println("Vào phương thức thêm bài học");

		Section section = sectionService.getSectionById_Huy(sectionId).orElse(null);
		if (section == null) {
			return ResponseEntity.notFound().build();
		}

		Lesson lesson = new Lesson();
		lesson.setSection(section);
		lesson.setName(name.orElse(null));
		lesson.setLessionDuration(videoDuration.orElse((float) 0));
		lesson.setDescription(description.orElse(null));
		lesson.setCreateAt(new Date());
		lesson.setUpdateAt(new Date());

		if (video != null && !video.isEmpty()) {
			long maxSizeOnCloudinary = 10 * 1024 * 1024; // 10MB
			long maxSizeOnServer = 200 * 1024 * 1024; // 200MB

			if (video.getSize() > maxSizeOnServer) {
				return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File quá lớn. Giới hạn là 200MB.");
			}

			// Nhỏ hơn 10MB
			if (video.getSize() <= maxSizeOnCloudinary) {
				System.out.println("Lưu video vào Cloudinary");
				try {
					Map<?, ?> data = this.videoService.uploadVideoOnCloudinary(video);
					lesson.setPathVideo((String) data.get("url").toString());
				} catch (RuntimeException e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body("Upload video không thành công: " + e.getMessage());
				}
			}
			// Hơn 10MB
			if (video.getSize() > maxSizeOnCloudinary) {
				System.out.println("Lưu video vào Server");
				// Xử lý lưu video lên server nếu lớn hơn 10MB
				// Ví dụ: video.transferTo(new File("path/to/save/" +
				// video.getOriginalFilename()));
				try {
					String videoPath = videoService.uploadVideo(video);
					// Tạo đường dẫn trả về
					String fileName = Paths.get(videoPath).getFileName().toString();
					String downloadLink = "http://localhost:8080/api/v1/upload-file/download-video-on-server?path="
							+ URLEncoder.encode(videoPath, "UTF-8");
					lesson.setPathVideo(downloadLink);
				} catch (IOException e) {
					return ResponseEntity.status(500).body("Error uploading video");
				}
			}
		}

		try {
//			Lesson lessonCreate = lessonService.addLesson(lesson);
//			lessonService.updateLessonDuration(lessonCreate.getLessonId(), lessonCreate.getLessionDuration());
//			return ResponseEntity.ok(lessonCreate);
			Lesson lessonCreate = lessonService.addLesson(lesson);
			// lessonService.updateLessonDuration(lessonCreate.getLessonId(),
			// lessonCreate.getLessionDuration()); // Có vẻ thừa, vì lessonCreate đã có thời
			// lượng

			// 💡 BƯỚC 1: Cập nhật thời lượng Phần (Section)
			Section updatedSection = sectionService.calculateAndSetSectionDuration(sectionId);

			if (updatedSection != null) {
				// 💡 BƯỚC 2: Cập nhật thời lượng Khóa học (Course)
				int courseId = updatedSection.getCourse().getCourseId();
				courseService.calculateAndSetCourseDuration(courseId);
			}

			return ResponseEntity.ok(lessonCreate);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Có lỗi xảy ra khi thêm bài học: " + e.getMessage());
		}
	}

	@PutMapping(value = "/{sectionId}/lesson/{lessonId}/update-lesson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateLesson(@PathVariable("sectionId") int sectionId,
			@PathVariable("lessonId") int lessonId, @RequestParam("videoDuration") Optional<Float> videoDuration,
			@RequestParam("name") Optional<String> name, @RequestParam("description") Optional<String> description,
			@RequestParam(value = "file", required = false) MultipartFile video)
			throws IOException, InterruptedException, java.io.IOException {
		System.out.println("Section  controller - Thời lượng video float: " + videoDuration);// 8.746667

		Lesson lessonUpdate = lessonService.getLessonById_Huy(lessonId).orElse(null);

		// Bắt lỗi không tìm thấy lesson cần cập nhật
		if (lessonUpdate == null) {
			return ResponseEntity.notFound().build();
		}

		lessonUpdate.setLessionDuration(videoDuration.orElse((float) 0.0));
		lessonUpdate.setUpdateAt(new Date());
		lessonUpdate.setDescription(description.orElse(null));
		lessonUpdate.setName(name.orElse(null));

		if (video != null && !video.isEmpty()) {
			long maxSizeOnCloudinary = 10 * 1024 * 1024; // 10MB
			long maxSizeOnServer = 200 * 1024 * 1024; // 200MB

			if (video.getSize() > maxSizeOnServer) {
				return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File quá lớn. Giới hạn là 200MB.");
			}

			// Nhỏ hơn 10MB
			if (video.getSize() <= maxSizeOnCloudinary) {
				System.out.println("Lưu video vào Cloudinary");
				try {
					Map<?, ?> data = this.videoService.uploadVideoOnCloudinary(video);
					lessonUpdate.setPathVideo((String) data.get("url").toString());
				} catch (RuntimeException e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body("Upload video không thành công: " + e.getMessage());
				}
			}
			// Hơn 10MB
			if (video.getSize() > maxSizeOnCloudinary) {
				System.out.println("Lưu video vào Server");
				// Xử lý lưu video lên server nếu lớn hơn 10MB
				// Ví dụ: video.transferTo(new File("path/to/save/" +
				// video.getOriginalFilename()));
				try {
					String videoPath = videoService.uploadVideo(video);
					// Tạo đường dẫn trả về
					String fileName = Paths.get(videoPath).getFileName().toString();
					String downloadLink = "http://localhost:8080/api/v1/upload-file/download-video-on-server?path="
							+ URLEncoder.encode(videoPath, "UTF-8");
					lessonUpdate.setPathVideo(downloadLink);
				} catch (IOException e) {
					return ResponseEntity.status(500).body("Error uploading video");
				}
			}

		}

		try {
//			Lesson lesssonUpdated = lessonService.updateLesson(lessonUpdate);
//			lessonService.updateLessonDuration(lesssonUpdated.getLessonId(), lesssonUpdated.getLessionDuration());
//			return ResponseEntity.ok(lesssonUpdated);
			
			Lesson lesssonUpdated = lessonService.updateLesson(lessonUpdate);
	        // lessonService.updateLessonDuration(lesssonUpdated.getLessonId(), lesssonUpdated.getLessionDuration()); // Có vẻ thừa

	        // 💡 BƯỚC 1: Cập nhật thời lượng Phần (Section)
	        Section updatedSection = sectionService.calculateAndSetSectionDuration(sectionId);
	        
	        if (updatedSection != null) {
	            // 💡 BƯỚC 2: Cập nhật thời lượng Khóa học (Course)
	            int courseId = updatedSection.getCourse().getCourseId();
	            courseService.calculateAndSetCourseDuration(courseId);
	        }
	        
	        return ResponseEntity.ok(lesssonUpdated);
	        
	        
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Có lỗi xảy ra khi thêm bài học: " + e.getMessage());
		}
	}

	@GetMapping("/{sectionId}/lesson/{lessonId}")
	public ResponseEntity<?> showListLessonOfSection(@PathVariable("sectionId") int sectionId,
			@PathVariable("lessonId") int lessonId) {
		System.out.println("Phần: " + sectionId);
		System.out.println("Bài: " + lessonId);
		//
		Lesson lessonDetail = new Lesson();
		lessonDetail = lessonService.getLessonById_Huy(lessonId).orElse(null);
		// Không tim thấy bài học
		if (lessonDetail == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(lessonDetail);
	}

	@DeleteMapping("/{sectionId}/lesson/{lessonId}/remove-lesson")
	public ResponseEntity<?> removeLesson(@PathVariable("lessonId") int lessonId) {
		System.out.println(lessonId);
		try {
			Lesson baiHocCanXoa = lessonService.getLessonById_Huy(lessonId).orElse(null);
			lessonService.removeLesson(baiHocCanXoa);// Xóa đáp án
			return ResponseEntity.ok(baiHocCanXoa);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Bài học đã đăng không thể xóa bài học" + e.getMessage());
		}

	}

	/////////////////////////////////////// API liên quan đến quiz
//	@GetMapping("/{sectionId}/test-manager") // Hiển thị một bài kiểm tra duy nhất
//	public ResponseEntity<?> showTest(@PathVariable("sectionId") int sectionId) {
//		System.out.println("Bài kiểm tra của phần: " + sectionId);
//		//
//		Section sectionTonTai = sectionService.getSectionById_Huy(sectionId).orElse(null);
//		if (sectionTonTai == null) {
//			return ResponseEntity.notFound().build();
//		}
//		Test baiKiemTraEntity = testService.timKiemBaiQuizNhoNhatCuaSection(sectionTonTai);
//		if (baiKiemTraEntity == null) {
//			return ResponseEntity.notFound().build();
//		}
//		TestDTO baiKiemTraDTO = new TestDTO();
//		baiKiemTraDTO.setTestID(baiKiemTraEntity.getTestId());
//		baiKiemTraDTO.setCountdownTimer(baiKiemTraEntity.getCountdownTimer());
//
//		List<Question> listQuestionEntity = questionService.timKiemDanhSachCauHoiTheoBaiQuizTam(baiKiemTraEntity);
//		List<QuestionDTO> listQuestionDTO = new ArrayList<>();
//
//		for (Question questionEntity : listQuestionEntity) {
//			QuestionDTO questionDTO = new QuestionDTO();
//			questionDTO.setQuestionId(questionEntity.getQuestionId());
//			questionDTO.setContents(questionEntity.getContents());
//
//			List<Answer> listAnswerEntity = answerService.timListDapAnTheoCauHoiTam(questionEntity);
//			List<AnswerDTO> listAnswerDTO = new ArrayList<>();
//
//			for (Answer answerEntity : listAnswerEntity) {
//				AnswerDTO answerDTO = new AnswerDTO();
//				answerDTO.setAnswerId(answerEntity.getAnswerId());
//				answerDTO.setText(answerEntity.getContent());
//				answerDTO.setCorrect(answerEntity.isCorrect()); // Set giá trị isCorrect từ entity
//
//				listAnswerDTO.add(answerDTO);
//			}
//			questionDTO.setListAnswerDTO(listAnswerDTO);
//			listQuestionDTO.add(questionDTO);
//		}
//
//		baiKiemTraDTO.setListQuestion(listQuestionDTO);
//		return ResponseEntity.ok(baiKiemTraDTO);
//	}

	@GetMapping("/{sectionId}/test-manager") // Hiển thị một bài kiểm tra duy nhất
	public ResponseEntity<?> showTest(@PathVariable("sectionId") int sectionId) {
		System.out.println("Bài kiểm tra của phần: " + sectionId);

		// 1. Kiểm tra Section có tồn tại không
		Section sectionTonTai = sectionService.getSectionById_Huy(sectionId).orElse(null);
		if (sectionTonTai == null) {
			// Nếu Section không tồn tại, trả về 404 là đúng
			return ResponseEntity.notFound().build();
		}

		// 2. Tìm bài kiểm tra
		Test baiKiemTraEntity = testService.timKiemBaiQuizNhoNhatCuaSection(sectionTonTai);

		// 3. Xử lý khi KHÔNG tìm thấy bài kiểm tra
		if (baiKiemTraEntity == null) {
			// Trả về 200 OK, nhưng với một đối tượng TestDTO (hoặc Map) có testID = 0 (hoặc
			// null)
			// để Frontend biết rằng Section tồn tại nhưng chưa có Test.
			TestDTO emptyTestDTO = new TestDTO();
			emptyTestDTO.setTestID(0); // Rất quan trọng: set TestID = 0
			emptyTestDTO.setCountdownTimer(0);
			emptyTestDTO.setListQuestion(Collections.emptyList());

			return ResponseEntity.ok(emptyTestDTO);
		}

		// 4. Trường hợp tìm thấy bài kiểm tra (Logic cũ của bạn)
		TestDTO baiKiemTraDTO = new TestDTO();
		// ... (logic mapping từ entity sang DTO của bạn)
		baiKiemTraDTO.setTestID(baiKiemTraEntity.getTestId());
		baiKiemTraDTO.setCountdownTimer(baiKiemTraEntity.getCountdownTimer());

		// ... (Phần còn lại của logic mapping câu hỏi và đáp án)
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
				answerDTO.setCorrect(answerEntity.isCorrect());
				listAnswerDTO.add(answerDTO);
			}
			questionDTO.setListAnswerDTO(listAnswerDTO);
			listQuestionDTO.add(questionDTO);
		}

		baiKiemTraDTO.setListQuestion(listQuestionDTO);
		return ResponseEntity.ok(baiKiemTraDTO);
	}

	// Import test
//	@PostMapping("/{sectionId}/test/{testId}/import-quiz")
//	public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file,
//	                                          @PathVariable("sectionId") int sectionId,
//	                                          @PathVariable("testId") int testId) throws IOException, EncryptedDocumentException, java.io.IOException {
//	    if (file.isEmpty()) {
//	        return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
//	    }
//
//	    Section section = sectionService.getSectionById_Huy(sectionId).orElse(null);
//	    if (section == null) {
//	        return ResponseEntity.notFound().build();
//	    }
//
//	    Test test = testService.timKiemBaiQuizNhoNhatCuaSection(section);
//	    if (test == null) {
//	        return ResponseEntity.notFound().build();
//	    }
//
//	    try {
//	        Workbook workbook = WorkbookFactory.create(file.getInputStream());
//	        Sheet sheet = workbook.getSheetAt(0);
//
//	        Map<String, Question> questionMap = new HashMap<>(); // Map để lưu các câu hỏi theo nội dung
//	        List<Answer> answers = new ArrayList<>(); // Danh sách để lưu các đáp án
//
//	        for (Row row : sheet) {
//	            if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề
//
//	            // Lấy dữ liệu từ các cột
//	            Cell questionCell = row.getCell(1); // QUESTION
//	            Cell answerCell = row.getCell(2); // ANSWER
//	            Cell correctCell = row.getCell(3); // CORRECT
//
//	            if (questionCell == null || answerCell == null || correctCell == null) {
//	                continue; // Bỏ qua dòng không hợp lệ
//	            }
//
//	            String questionContent = questionCell.getStringCellValue().trim();
//
//	            // Kiểm tra xem câu hỏi đã tồn tại trong Map hay chưa
//	            Question question = questionMap.computeIfAbsent(questionContent, key -> {
//	                Question newQuestion = new Question();
//	                newQuestion.setTest(test);
//	                newQuestion.setContents(key);
//	                newQuestion.setListAnswer(new ArrayList<>());
//	                return newQuestion;
//	            });
//
//	            // Chuyển đổi nội dung của đáp án thành String
//	            String answerContent;
//	            if (answerCell.getCellType() == CellType.STRING) {
//	                answerContent = answerCell.getStringCellValue().trim();
//	            } else if (answerCell.getCellType() == CellType.NUMERIC) {
//	                answerContent = String.valueOf((int) answerCell.getNumericCellValue());
//	            } else {
//	                continue; // Bỏ qua đáp án không hợp lệ
//	            }
//
//	            // Tạo đáp án và liên kết với câu hỏi
//	            Answer answer = new Answer();
//	            answer.setQuestion(question); // Gắn câu hỏi vào đáp án
//	            answer.setContent(answerContent);
//	            answer.setCorrect(correctCell.getBooleanCellValue());
//	            question.getListAnswer().add(answer); // Thêm đáp án vào danh sách của câu hỏi
//
//	            // Thêm đáp án vào danh sách lưu trữ
//	            answers.add(answer);
//	        }
//
//	        // Lưu các câu hỏi vào cơ sở dữ liệu
//	        questionService.saveAll(new ArrayList<>(questionMap.values()));
//
//	        // Lưu các đáp án vào cơ sở dữ liệu
//	        answerService.saveAll(answers);
//
//	        return new ResponseEntity<>("Import successful", HttpStatus.OK);
//	    } catch (IOException e) {
//	        return new ResponseEntity<>("Error reading file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//	    }
//	}

	@PostMapping("/{sectionId}/test/{testId}/import-quiz")
	public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file,
			@PathVariable("sectionId") int sectionId, @PathVariable("testId") int testId)
			throws IOException, EncryptedDocumentException, java.io.IOException {
		Section section = sectionService.getSectionById_Huy(sectionId).orElse(null);
		if (section == null) {
			return ResponseEntity.notFound().build();
		}

		Test test = testService.timKiemBaiQuizNhoNhatCuaSection(section);
		if (test == null) {
			return ResponseEntity.notFound().build();
		}

		if (file.isEmpty()) {
			return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
		}

		try {
			Workbook workbook = WorkbookFactory.create(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);

			Map<String, Question> questionMap = new LinkedHashMap<>(); // To store unique questions
			List<Answer> answers = new ArrayList<>();

			String currentQuestionContent = null; // Track the most recent question content

			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue; // Skip the header row

				// Extract cells
				Cell noCell = row.getCell(0); // NO
				Cell questionCell = row.getCell(1); // QUESTION
				Cell answerCell = row.getCell(2); // ANSWER
				Cell correctCell = row.getCell(3); // CORRECT

				if (noCell == null || answerCell == null || correctCell == null) {
					continue; // Skip invalid rows
				}

				// Check if the question cell is not empty or merged
				if (questionCell != null && questionCell.getCellType() == CellType.STRING) {
					currentQuestionContent = questionCell.getStringCellValue().trim();
				}

				if (currentQuestionContent == null) {
					continue; // Skip rows where we can't determine the question
				}

				// Check if the question already exists
				Question question = questionMap.computeIfAbsent(currentQuestionContent, key -> {
					Question newQuestion = new Question();
					newQuestion.setTest(test);
					newQuestion.setContents(key);
					newQuestion.setListAnswer(new ArrayList<>());
					return newQuestion;
				});

				String answerContent;
				if (answerCell.getCellType() == CellType.STRING) {
					answerContent = answerCell.getStringCellValue().trim();
				} else if (answerCell.getCellType() == CellType.NUMERIC) {
					answerContent = String.valueOf((int) answerCell.getNumericCellValue());
				} else {
					continue; // Bỏ qua đáp án không hợp lệ
				}

				// Tạo đáp án và liên kết với câu hỏi
				Answer answer = new Answer();
				answer.setQuestion(question); // Gắn câu hỏi vào đáp án
				answer.setContent(answerContent);
				answer.setCorrect(correctCell.getBooleanCellValue());
				question.getListAnswer().add(answer); // Thêm đáp án vào danh sách của câu hỏi

				// Thêm đáp án vào danh sách lưu trữ
				answers.add(answer);
			}

			// Lưu các câu hỏi vào cơ sở dữ liệu
			questionService.saveAll(new ArrayList<>(questionMap.values()));

			// Lưu các đáp án vào cơ sở dữ liệu
			answerService.saveAll(answers);

			return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\": \"Import bài kiểm tra thành công!\"}");

		} catch (Exception e) {
			return new ResponseEntity<>("Error reading file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{sectionId}/question-detail/{questionId}")
	public ResponseEntity<?> showQuestionDetail(@PathVariable("sectionId") int sectionId,
			@PathVariable("questionId") int questionId) {
		Question questionEntity = questionService.timKiemCauHoiTheoIdCauHoi_Huy(questionId).orElse(null);
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
		return ResponseEntity.ok(questionDTO);
	}

	@PostMapping("/{sectionId}/question-detail/{questionId}/add-answer")
	public ResponseEntity<?> addAnswerForQuestion(@PathVariable("sectionId") int sectionId,
			@PathVariable("questionId") int questionId) {
		Question questionEntity = questionService.timKiemCauHoiTheoIdCauHoi_Huy(questionId).orElse(null);
		Answer answerEntity = new Answer();
		answerEntity.setQuestion(questionEntity);
		return ResponseEntity.ok(answerService.themDapAn(answerEntity));
	}

	@DeleteMapping("/{sectionId}/question-detail/{questionId}/remove-answer/{answerId}")
	public ResponseEntity<?> removeAnswerForQuestion(@PathVariable("answerId") int answerId) {
		System.out.println(answerId);
		Answer answerEntity = answerService.timKiemDapAnTheoId(answerId).orElse(null);
		answerService.xoaDapAn(answerEntity);
		return ResponseEntity.ok(answerEntity);
	}

	@PutMapping("/{sectionId}/question-detail/{questionId}")
	public ResponseEntity<Question> updateContentQuestion(@PathVariable("sectionId") String sectionId,
			@PathVariable("questionId") String questionId, @RequestBody Question question) {
		int questionIdInt = Integer.parseInt(questionId);
		// Giả sử bạn chỉ cần cập nhật nội dung
		question.setQuestionId(questionIdInt); // Đảm bảo ID câu hỏi được thiết lập
		Question updatedQuestion = questionService.updateContentsQuestion(questionIdInt, question);
		return ResponseEntity.ok(updatedQuestion);
	}

	@PutMapping("/{sectionId}/question-detail/{questionId}/update-answer/{answerId}") // Cập nhật nội dung đáp án
	public ResponseEntity<Answer> updateContentsAnswer(@PathVariable("sectionId") String sectionId,
			@PathVariable("questionId") String questionId, @PathVariable("answerId") String answerId,
			@RequestBody Answer answer) { // Nhận đối tượng Answer thay vì String
		int answerIdInt = Integer.parseInt(answerId);
		// Chỉ cập nhật nội dung đáp án
		Answer updatedAnswer = answerService.updateAnswer(answerIdInt, answer);
		return ResponseEntity.ok(updatedAnswer);
	}

	@PutMapping("/{sectionId}/question-detail/{questionId}/update-anwer-correct")
	public ResponseEntity<?> updateQuestion(@PathVariable("questionId") int questionId,
			@RequestBody List<AnswerDTO> listAnswerDTO) {

		System.out.println("Mã cần cập nhật: " + questionId);
		System.out.println("Mã cần cập nhật: " + listAnswerDTO);

		// Tìm câu hỏi trong cơ sở dữ liệu
		Question existingQuestion = questionService.timKiemCauHoiTheoIdCauHoi_Huy(questionId).orElse(null);
		if (existingQuestion == null) {
			return ResponseEntity.notFound().build();
		}

		// Kiểm tra và khởi tạo danh sách đáp án nếu cần
		if (existingQuestion.getListAnswer() == null) {
			existingQuestion.setListAnswer(new ArrayList<>()); // Khởi tạo danh sách nếu nó null
		}

		// Cập nhật danh sách đáp án
		for (AnswerDTO answer : listAnswerDTO) {
			Answer existingAnswer = answerService.timKiemDapAnTheoId(answer.getAnswerId()).orElse(null);
			if (existingAnswer != null) {
				existingAnswer.setContent(answer.getText());
				existingAnswer.setCorrect(answer.isCorrect());
				answerService.save(existingAnswer);
			}
		}
		return ResponseEntity.ok(existingQuestion);
	}

	@DeleteMapping("/{sectionId}/question-detail/{questionId}/remove-question") // Xóa câu hỏi
	public ResponseEntity<?> removeQuestion(@PathVariable("questionId") int questionId) {
		System.out.println(questionId);

		// Tìm câu hỏi theo ID
		Question questionCanDuocXoa = questionService.timKiemCauHoiTheoIdCauHoi_Huy(questionId).orElse(null);

		// Nếu câu hỏi không tồn tại, trả về lỗi
		if (questionCanDuocXoa == null) {
			return ResponseEntity.notFound().build();
		}

		// Xóa tất cả các đáp án liên quan
		List<Answer> answers = questionCanDuocXoa.getListAnswer();
		for (Answer answer : answers) {
			answerService.xoaCauTraLoi(answer.getAnswerId());
		}

		// Xóa câu hỏi
		questionService.xoaCauHoi(questionCanDuocXoa);

		return ResponseEntity.ok(questionCanDuocXoa);
	}

	@PostMapping("/{sectionId}/test-manager/{testId}/add-question") // Thêm câu hỏi mới
	public ResponseEntity<?> addQuestion(@PathVariable("testId") int testId, @RequestBody QuestionDTO questionDTO) {
		// Tạo bài kiểm tra
		Question questionEntity = new Question();
		questionEntity.setContents(questionDTO.getContents());
		// Tìm bài kiểm tra dựa trên ID
		Test test = testService.timTestTheoIdTam(testId);
		if (test == null) {
			return ResponseEntity.notFound().build(); // Trả về lỗi nếu không tìm thấy bài kiểm tra
		}
		questionEntity.setTest(test); // Gán bài kiểm tra cho câu hỏi
		Question createdQuestion = questionService.addQuestion(questionEntity);
		// Duyệt list => add đáp án
		for (AnswerDTO answerDTO : questionDTO.getListAnswerDTO()) {
			// Add đáp án
			Answer answer = new Answer();
			answer.setContent(answerDTO.getText());
			System.out.println("Trạng thái đáp án: " + answerDTO.isCorrect() + " - " + answerDTO.isCorrect());
			answer.setCorrect(answerDTO.isCorrect());
			answer.setQuestion(createdQuestion);
			answerService.save(answer);

		}
		return ResponseEntity.ok(createdQuestion);
	}

	// Tạo bài kiểm tra mới
	@PostMapping("/{sectionId}/add-test")
	public ResponseEntity<?> addTest(@PathVariable("sectionId") int sectionId) {
		// Tìm kiếm section
		Section sectionTonTai = sectionService.getSectionById_Huy(sectionId).orElse(null);
		if (sectionTonTai == null) {
			return ResponseEntity.notFound().build();
		}
		Test test = new Test();
		test.setCreateAt(new Date());
		test.setSection(sectionTonTai);
		test.setCountdownTimer(0);// Mới tạo sẽ không có thời gian điểm ngược
		Test createdTest = testService.addTest(test);
		return ResponseEntity.ok(createdTest);
	}

	// Tạo bài kiểm tra mới
	@PostMapping("/{sectionId}/update-countdown-timer/{testId}/{setCountdownTimer}")
	public ResponseEntity<?> updateCountdownTimer(@PathVariable("sectionId") int sectionId,
			@PathVariable("testId") int testId, @PathVariable("setCountdownTimer") int setCountdownTimer) {

		// Đồng hồ điểm ngược
		int setCountdownTimerInt = setCountdownTimer;
		// Thời gian điểm ngược không thể nhỏ hơn 0
		if (setCountdownTimerInt < 0) {
			return ResponseEntity.notFound().build();
		}
		// Tìm kiếm section
		Section sectionTonTai = sectionService.getSectionById_Huy(sectionId).orElse(null);
		if (sectionTonTai == null) {
			return ResponseEntity.notFound().build();
		}
		Test baiKiemTraEntity = testService.timKiemBaiQuizNhoNhatCuaSection(sectionTonTai);
		if (baiKiemTraEntity == null) {
			return ResponseEntity.notFound().build();
		}
		baiKiemTraEntity.setCountdownTimer(setCountdownTimerInt);
		Test testUpdated = testService.saveTest(baiKiemTraEntity);
		return ResponseEntity.ok(testUpdated);
	}

	//////////////////////////////////////////// Kết thúc API quiz
}
