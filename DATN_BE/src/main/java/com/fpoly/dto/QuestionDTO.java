package com.fpoly.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
	private int questionId;
	private String contents;
	List<AnswerDTO> listAnswerDTO;
}
