package com.fpoly.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseManagerDTO {
	private int courseId;
	private String avatar;
	private String name;
	private int status;// 0 => không công khai, 1 = công khai, 2 = không công khai
	private Date createAt;
	private int numberOfComment;// Số lượng comment
	private double revenue;// Doanh thu
	private float courseDuration;// Thời lượng

	private List<CategoryDto> categories;
}
