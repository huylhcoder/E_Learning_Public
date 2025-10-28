package com.fpoly.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {

	private int answerId;
	private String text;
	
	@JsonProperty("correct") // ✅ ánh xạ JSON key "correct" ↔ field "isCorrect"
	private boolean isCorrect; // Thêm thuộc tính này

}
