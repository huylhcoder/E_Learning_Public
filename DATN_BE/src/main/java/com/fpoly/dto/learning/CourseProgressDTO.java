package com.fpoly.dto.learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressDTO {
    private int courseId;
    private int totalLesson;
    private int totalQuiz;
    private int totalLessonComplete;
    private int totalTestComplete;
    private float progressPercentage;
    private int progressStatus; // 0 = chưa hoàn thành, 1 = hoàn thành
}
