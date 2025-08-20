package com.fpoly.dto.quiz;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerDTO {
    private Integer answerId;
    private String text;
}
