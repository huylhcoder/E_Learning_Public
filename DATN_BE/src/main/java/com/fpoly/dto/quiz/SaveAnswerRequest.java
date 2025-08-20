package com.fpoly.dto.quiz;

import lombok.Data;

@Data
public class SaveAnswerRequest {
    private Integer questionId;
    private Integer answerId;
}
