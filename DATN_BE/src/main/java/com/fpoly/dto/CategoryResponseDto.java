package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
	private Integer categoryId;
	private String name;
	private String slug;
	private String parentName;
	private int courseCount;
}
