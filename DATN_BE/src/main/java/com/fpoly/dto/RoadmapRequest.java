package com.fpoly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapRequest {
	private String goal; // ví dụ: "Tôi muốn học Backend với Java"
	private String level; // optional: "Beginner","Intermediate","Advanced"
	private String preferredCategories; // optional: comma separated categories
}