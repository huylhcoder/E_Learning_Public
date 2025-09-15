package com.fpoly.dto.learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDTO {
    private int testId;
    private String title;          // Tên bài kiểm tra
    private String description;    // Mô tả bài test
    private String numberOfQuestion; // Số câu hỏi
    private int countdownTimer;    // Thời gian làm bài (giây)
    private boolean isCompleted; // ✅ thêm
}