package com.fpoly.service;

import com.fpoly.dto.CourseDTO;
import com.fpoly.dto.RoadmapRequest;
import com.fpoly.dto.RoadmapResponse;
import com.fpoly.integration.openai.AiQuotaExceededException;
import com.fpoly.integration.openai.OpenAiClient;
import com.fpoly.integration.openai.OpenAiClient.AiResult;
import com.fpoly.repository.CourseRepository;
import com.fpoly.entity.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoadmapService {

    private static final Logger log = LoggerFactory.getLogger(RoadmapService.class);

    private final CourseRepository courseRepository;
    private final OpenAiClient openAiClient;

    public RoadmapService(CourseRepository courseRepository, OpenAiClient openAiClient) {
        this.courseRepository = courseRepository;
        this.openAiClient = openAiClient;
    }

    public RoadmapResponse suggestRoadmap(RoadmapRequest req) {
        AiResult aiResult = null;

        try {
            // ✅ Gọi OpenRouter để phân tích mục tiêu học
            aiResult = openAiClient.analyzeGoalToJson(
                    req.getGoal(),
                    req.getLevel(),
                    req.getPreferredCategories()
            );
            log.info("✅ Nhận phản hồi từ OpenRouter: {}", aiResult);
        } catch (AiQuotaExceededException ex) {
            log.warn("⚠️ Hết hạn mức sử dụng API OpenRouter: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("❌ Lỗi khi gọi OpenRouter API: {}", ex.getMessage(), ex);
        }

        // ✅ Nếu AI không trả về hoặc bị lỗi, dùng fallback hợp lý
        String category = (aiResult != null && aiResult.getCategory() != null && !aiResult.getCategory().isBlank())
                ? aiResult.getCategory()
                : (req.getPreferredCategories() != null && !req.getPreferredCategories().isBlank()
                    ? req.getPreferredCategories()
                    : "General");

        String level = req.getLevel();

        // ✅ Lấy danh sách khóa học phù hợp từ DB
        List<Course> courses = courseRepository.searchCourses(level, category);
        log.info("📘 Tìm thấy {} khóa học cho category='{}', level='{}'", courses.size(), category, level);

        // ✅ Map sang DTO
        List<CourseDTO> dtos = courses.stream().map(c -> {
            CourseDTO dto = new CourseDTO();
            dto.setCourseId(c.getCourseId());
            dto.setName(c.getName());
            dto.setDescription(c.getDescription());
            dto.setTopic(c.getTopic());
            dto.setLevel(c.getCourseLevel() != null ? c.getCourseLevel().getName() : null);
            dto.setAverageRating(c.getAverageRating());
            dto.setPrice(c.getPrice());
            return dto;
        }).collect(Collectors.toList());

        // ✅ Trả về response
        RoadmapResponse resp = new RoadmapResponse();
        resp.setRoadmapName(aiResult != null && aiResult.getRoadmapName() != null
                ? aiResult.getRoadmapName()
                : "Lộ trình học gợi ý");
        resp.setExplanation(aiResult != null && aiResult.getExplanation() != null
                ? aiResult.getExplanation()
                : "AI (OpenRouter) đang bận hoặc không phản hồi. Hệ thống tạm thời gợi ý các khóa học phù hợp nhất theo yêu cầu của bạn.");
        resp.setRecommendedCourses(dtos);

        log.info("🎯 Trả về roadmap: {}", resp.getRoadmapName());
        return resp;
    }
}
