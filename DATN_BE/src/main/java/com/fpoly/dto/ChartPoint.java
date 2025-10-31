package com.fpoly.dto;

//ChartPoint.java
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartPoint {
 private String label; // Nhãn trên trục X (ví dụ: "2024-05-15" hoặc "Tháng 10")
 private long value;   // Giá trị trên trục Y (ví dụ: Doanh thu, số lượng User)
}