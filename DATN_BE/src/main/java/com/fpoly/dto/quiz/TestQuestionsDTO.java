package com.fpoly.dto.quiz;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestQuestionsDTO {
    private Integer testId;
    private String title;
    private String description;
    private List<QuestionDTO> questions;
    private Date startTime;   // Thời gian bắt đầu bài kiểm  tra
    private Date endTime;     // Thời gian nhấn nút submit
}
