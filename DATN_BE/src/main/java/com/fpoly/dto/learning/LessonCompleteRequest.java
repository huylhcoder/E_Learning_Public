package com.fpoly.dto.learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonCompleteRequest {
    private int lessonId;
    private int courseId;
}

