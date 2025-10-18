package com.fpoly.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapResponse {
    private String roadmapName; // vd: "Backend Developer Roadmap"
    private String explanation; // AI giải thích vì sao
    private List<CourseDTO> recommendedCourses;
}