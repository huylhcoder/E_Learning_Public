package com.fpoly.dto;

import java.util.List;

import com.fpoly.entity.Course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredCourseWithPaymentDTO {
	private List<Course> courses;
	private boolean paymentStatus;
}
