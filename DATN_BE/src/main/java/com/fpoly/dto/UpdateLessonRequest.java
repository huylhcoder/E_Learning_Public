package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLessonRequest {
    private int courseId;   // ID của khóa học
    private int lessonId;   // ID của bài học hiện tại
}