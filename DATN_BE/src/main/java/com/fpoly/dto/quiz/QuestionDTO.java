package com.fpoly.dto.quiz;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionDTO {
	private Integer questionId;
    private String contents;
    private List<AnswerDTO> listAnswerDTO;
    private Integer selectedAnswerId;  
}