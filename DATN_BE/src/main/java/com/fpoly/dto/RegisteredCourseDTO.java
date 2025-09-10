package com.fpoly.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredCourseDTO {
    private int registeredCourseId;

    // Thông tin cơ bản của khóa học
    private int courseId;
    private String courseName;
    private String avatar;
    private float price;
    private float averageRating;
    private int numberOfLesson;
    private float courseDuration;

    // Thông tin tiến độ
    private int totalLession;
    private int totalQuiz;
    private int totalLessionComplete;
    private int totalTestComplete;
    private float progressPercentage;
    private int progressStatus;

    // Ngày đăng ký
    private Date createAt;
}
