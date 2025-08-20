package com.fpoly.service;

import java.util.List;
import java.util.Optional;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.dto.UpdateLessonRequest;
import com.fpoly.dto.learning.CourseProgressDTO;
import com.fpoly.dto.learning.LessonDTO;
import com.fpoly.entity.Course;
import com.fpoly.entity.CourseProgress;
import com.fpoly.entity.Lesson;
import com.fpoly.entity.User;
import com.fpoly.entity.Voucher;
import com.fpoly.repository.AnswerRepository;
import com.fpoly.repository.CourseProgressRepository;
import com.fpoly.repository.CourseRepository;
import com.fpoly.repository.LessonRepository;
import com.fpoly.repository.UserRepository;
import com.fpoly.security.JwtTokenUtils;
import com.fpoly.repository.CourseProgressRepository;

@Service
public class CourseProgressService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CourseRepository courseReponsitory;
	@Autowired
	private LessonRepository lessonRepository;
	@Autowired
	private CourseProgressRepository courseProgressRepository;

	@Autowired
	private JwtTokenUtils jwtTokenUtil;
	
//Learning Page
	public Lesson updateCurrentLesson(UpdateLessonRequest request) {
		
		Course course  = courseReponsitory.findByCourseId(request.getCourseId());
        CourseProgress progress = courseProgressRepository.findByCourse(course)
                .orElseGet(() -> createNewProgress(request.getCourseId()));
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        progress.setCurrentLesson(lesson);
        courseProgressRepository.save(progress);
        return lesson;
    }

	public LessonDTO getCurrentLessonDTO(int courseId, String token) {
        Course course = courseReponsitory.findByCourseId(courseId);
        CourseProgress progress = courseProgressRepository.findByCourse(course)
                .orElseGet(() -> createNewProgress(courseId));

        Lesson lesson = progress.getCurrentLesson();

        boolean isComplete = false;
        if (token != null && !token.isEmpty()) {
            String email = jwtTokenUtil.extractEmail(token.replace("Bearer ", ""));
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            isComplete = lesson.getListLessonComplete().stream()
                    .anyMatch(lc -> lc.getUser().equals(user));
        }

        // Gộp convertToDTO vào Service luôn
        if (lesson == null) return null;
        return new LessonDTO(
                lesson.getLessonId(),
                lesson.getName(),
                lesson.getDescription(),
                lesson.getContentDescription(),
                lesson.getLessionDuration(),
                lesson.getPathVideo(),
                isComplete
        );
    }


    private CourseProgress createNewProgress(int courseId) {
        Lesson firstLesson = lessonRepository.findFirstBySection_Course_CourseIdOrderByLessonIdAsc(courseId)
                .orElseThrow(() -> new RuntimeException("No lessons found for course"));
        CourseProgress progress = new CourseProgress();
        progress.setCourse(firstLesson.getSection().getCourse());
        progress.setCurrentLesson(firstLesson);
        return courseProgressRepository.save(progress);
    }
    
    public CourseProgressDTO getCourseProgress(int courseId, String token) {
        // Lấy userId từ token
        String jwt = token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
        if (jwt == null) {
            throw new RuntimeException("Token không hợp lệ hoặc thiếu");
        }
        String  email = jwtTokenUtil.extractEmail(jwt);
        User user =  userRepository.timKiemUserTheoEmailToan(email);
        // Tìm tiến độ trong DB
        CourseProgress progress = courseProgressRepository.findByCourse_CourseIdAndUser_UserId(courseId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tiến độ cho khóa học"));

        return new CourseProgressDTO(
                progress.getCourse().getCourseId(),
                progress.getTotalLession(),
                progress.getTotalQuiz(),
                progress.getTotalLessionComplete(),
                progress.getTotalTestComplete(),
                progress.getProgressPercentage(),
                progress.getProgressStatus()
        );
    }

//Khác
	public List<CourseProgress> FillCourseKhoa(int userId){
		return courseProgressRepository.FillCourseKhoa(userId);
	}

	public User updateStatusKhoa(User user) {
		return userRepository.save(user);
	}

	public CourseProgress findCourseProgressByIdKhoa(int userId) {
		return courseProgressRepository.findByCourseProgressId(userId);
	}

	// CODE HBao
	public List<CourseProgress> FillTotalCourseComplete() {
		return courseProgressRepository.FillTotalCourseCompleteCuaBao();
	}

	public CourseProgress findByUserAndCourse(User user, Course course) {
		return courseProgressRepository.findByUserAndCourse(user, course);
	}

	public CourseProgress save(CourseProgress courseProgress) {
		return courseProgressRepository.save(courseProgress);
	}

	// Cập nhật tiến độ khi kiểm tra
	public CourseProgress updateCourseProgressForTakeTheTest(User user, Course course) {
		CourseProgress courseProgressUpDate = new CourseProgress();
		// Tìm kiếm tiến độ của bài học
		courseProgressUpDate = courseProgressRepository.findByUserAndCourse(user, course);
		// Nếu người dùng đã hoàn thành bài kiểm tra thì cộng lên một
		courseProgressUpDate.setTotalTestComplete(courseProgressUpDate.getTotalTestComplete() + 1);
		// Tính lại phần trăm hoàn thành
		courseProgressUpDate.setProgressPercentage(calculateProgressPercentage(courseProgressUpDate));
		return courseProgressRepository.save(courseProgressUpDate);
	}

	public float calculateProgressPercentage(CourseProgress courseProgress) {
		// Giả sử bạn có tổng số bài học và bài kiểm tra
		int totalLessons = courseProgress.getTotalLession();
		int totalQuizzes = courseProgress.getTotalQuiz();
		int totalCompleted = courseProgress.getTotalLessionComplete() + courseProgress.getTotalTestComplete();

		if (totalLessons + totalQuizzes == 0) {
			return 0;
		}

		return (float) totalCompleted / (totalLessons + totalQuizzes) * 100;
	}

    public CourseProgress getCourseProgress(int courseId, int userId) {
        return courseProgressRepository.findByCourse_CourseIdAndUser_UserId(courseId, userId)
                                          .orElse(null);  // Trả về null nếu không tìm thấy tiến độ
    }

    // Cập nhật tiến độ khóa học và tính toán lại progressPercentage
    public CourseProgress updateProgressPercentage(CourseProgress courseProgress) {
        // Tính lại tiến độ khóa học dựa trên số lượng bài học và bài kiểm tra hoàn thành
        float progress = 0;
        // Tránh chia cho 0 bằng cách kiểm tra nếu tổng bài học và quiz lớn hơn 0
        if ((courseProgress.getTotalLession() + courseProgress.getTotalQuiz()) > 0) {
            progress = (float)(courseProgress.getTotalLessionComplete() + courseProgress.getTotalTestComplete()) 
                       / (courseProgress.getTotalLession() + courseProgress.getTotalQuiz()) * 100;
        }

        // Làm tròn tiến độ về số nguyên (1 con số phía trước dấu phẩy)
//        progress = Math.round(progress);

        courseProgress.setProgressPercentage(progress);

        // Lưu lại tiến độ khóa học đã cập nhật vào cơ sở dữ liệu
        return courseProgressRepository.save(courseProgress);
    }

    // Tạo hoặc cập nhật tiến độ khóa học (cho trường hợp không tồn tại tiến độ)
    public CourseProgress createOrUpdateCourseProgress(CourseProgress courseProgress) {
        // Kiểm tra nếu đã có tiến độ của khóa học này cho người dùng
        CourseProgress existingProgress = getCourseProgress(courseProgress.getCourse().getCourseId(), courseProgress.getUser().getUserId());

        if (existingProgress != null) {
            // Cập nhật tiến độ hiện tại
            return updateProgressPercentage(courseProgress);
        } else {
            // Nếu không có tiến độ, lưu mới
            // Tính toán lại tiến độ trước khi lưu
            return courseProgressRepository.save(courseProgress);
        }
    }
  

   


    
}
