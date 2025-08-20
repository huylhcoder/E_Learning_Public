package com.fpoly.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//CourseDetailPage
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailSearchDTO {
	private int courseId;
	private String avatar;
	private String name;
	private float averageRating;
	private String description;
	private String contentDescription;
	private float price;
	private String levelName;
	private int totalComments;
	private int totalRegistered;
	private boolean paymentStatus; // "Đã thanh toán" hoặc "Chưa thanh toán"
	private boolean inCart; // "Đã thêm vào giỏ hàng hoặc Chưa thêm vào giỏ hàng
	private List<CourseDetailSearchCategoryDTO> categories;
	private List<CourseDetailSearchSectionDTO> sections;
}
