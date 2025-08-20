package com.fpoly.dto.learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonDTO {
    private int lessonId;
    private String name;           // Tiêu đề bài học
    private String description;    // Mô tả ngắn
    private String contentDescription; // Mô tả HTML từ Quill Editor
    private float lessonDuration;  // Thời lượng bài học
    private String pathVideo;      // Đường dẫn video
    private boolean isComplete;
}
