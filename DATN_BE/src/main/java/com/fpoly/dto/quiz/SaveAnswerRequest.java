package com.fpoly.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveAnswerRequest {
    private Integer questionId;
    private Integer answerId;
}
