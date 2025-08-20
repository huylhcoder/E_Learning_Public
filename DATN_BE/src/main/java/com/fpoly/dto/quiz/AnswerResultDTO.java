package com.fpoly.dto.quiz;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResultDTO {
 private Integer id;
 private String content;
 private boolean correct;   // có phải đáp án đúng?
 private boolean selected;  // user có chọn không?
}