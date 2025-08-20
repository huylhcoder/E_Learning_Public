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
public class QuestionResultDTO {
    private Integer id;
    private String content;
    private List<AnswerResultDTO> answers;
}
