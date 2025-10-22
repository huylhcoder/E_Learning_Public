package com.fpoly.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpoly.entity.Course;
import com.fpoly.entity.Lesson;
import com.fpoly.entity.Section;
import com.fpoly.entity.Test;
import com.fpoly.repository.LessonRepository;
import com.fpoly.repository.SectionRepository;
import com.fpoly.repository.TestRepository;

@Service
public class SectionService {

	@Autowired
	SectionRepository sectionRepository;
	@Autowired
	LessonRepository lessonRepository;
	@Autowired
	TestRepository testRepository;

	public List<Section> hienThiSectionTheoKhoaHocHao(Course course) {
		List<Section> listSection = sectionRepository.findByCourse(course);
		return listSection;
	}

	public List<Section> findListSectionOfCourse_Huy(Course course) {
		List<Section> listSection = sectionRepository.findByCourse(course);
		return listSection;
	}

	public Section createSection_Huy(Section section) {
		return sectionRepository.save(section);
	}

	// Cập nhật thời lượng section
	public Section calculateAndSetSectionDuration(int sectionId) {
		Section section = sectionRepository.findById(sectionId).orElse(null);
		if (section == null) {
			return null;
		}

		// 1. Tính tổng thời lượng của tất cả Lesson trong Section
		// (Giả sử bạn có phương thức trong LessonRepository để làm việc này)
		Float totalDuration = lessonRepository.sumLessionDurationBySectionId(sectionId);

		// Đảm bảo không bị null và cập nhật
		float newDuration = (totalDuration != null) ? totalDuration : 0.0f;

		if (section.getSectionDuration() != newDuration) {
			section.setSectionDuration(newDuration);
			return sectionRepository.save(section);
		}
		return section;
	}

	// Tìm kiếm phần mới được tạo
	public Section getLatestSection() {
		return sectionRepository.findSectionWithMaxId();
	}

	// Tìm kiếm phần theo id
	public Optional<Section> getSectionById_Huy(int sectionId) {
		return sectionRepository.findById(sectionId);
	}

	public Section updateSection_Huy(Section section) {
		// Kiểm tra xem section có tồn tại trong cơ sở dữ liệu không
		if (sectionRepository.existsById(section.getSectionId())) {
			return sectionRepository.save(section); // Lưu lại thông tin đã cập nhật
		}
		return null; // Trả về null nếu không tìm thấy section
	}

	// Xóa khóa trong trường hợp khóa học này chưa công khai
	// Nó sẽ không có kết quả làm quiz, tiến độ bài học,...
	@Transactional
	public void removeSection(int courseId) {
		// Tìm khóa học
		Section section = sectionRepository.findById(courseId)
				.orElseThrow(() -> new RuntimeException("Phần không tồn tại"));

		// Xóa tất cả các bài học trong phần
		for (Lesson lesson : section.getListLesson()) {
			lessonRepository.delete(lesson);
		}
		// Xóa tất cả các bài kiểm tra trong phần
		for (Test test : section.getListTest()) {
			testRepository.delete(test);
		}
		// Cuối cùng, xóa phần
		sectionRepository.delete(section);
	}

}