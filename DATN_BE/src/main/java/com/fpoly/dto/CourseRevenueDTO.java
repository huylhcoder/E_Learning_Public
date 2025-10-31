package com.fpoly.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRevenueDTO {
	private int courseId;
	private String courseName;
	private Date createAt; // Thời gian tạo khóa học (cho filter "mới nhất")
	private long totalRevenue; // Tổng doanh thu
	private long registrationCount; // Số lượng lượt đăng ký thành công
	private long certificateCount; // Số lượng chứng chỉ đã cấp (tức là số lần hoàn thành khóa học)
}