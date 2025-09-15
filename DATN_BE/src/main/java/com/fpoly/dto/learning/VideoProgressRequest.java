package com.fpoly.dto.learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoProgressRequest {
    private int courseId;      // 👈 đổi từ registeredCourseId thành courseId
    private int lessonId;
    private String pathVideo;
    private int videoProgress;
}