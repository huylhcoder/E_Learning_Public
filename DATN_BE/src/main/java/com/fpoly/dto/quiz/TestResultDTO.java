package com.fpoly.dto.quiz;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestResultDTO {
	private Integer testId;
	private String testName;
	private List<QuestionResultDTO> questions;
}