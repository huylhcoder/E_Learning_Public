package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunFactDTO {
	private long totalUsers;
	private long totalCourses;
	private long totalCategories;
	private double averageRating;
}