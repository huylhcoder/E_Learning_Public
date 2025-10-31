package com.fpoly.dto;

//MetricData.java
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricData {
	private String title; // Ví dụ: "Số Khóa Học Hoàn Thành"
	private String icon; // Ví dụ: "🎓" hoặc tên icon FE
	private long currentValue;
	private long previousValue; // Giá trị kỳ trước (để so sánh)
	private double percentageChange; // Tỷ lệ thay đổi. > 0 là tăng (xanh), < 0 là giảm (đỏ)
	private String currencyUnit; // Đơn vị tiền tệ (ví dụ: "VNĐ") - chỉ dùng cho doanh thu

	public MetricData(String title, String icon, long currentValue, long previousValue, double percentageChange) {
		this.title = title;
		this.icon = icon;
		this.currentValue = currentValue;
		this.previousValue = previousValue;
		this.percentageChange = percentageChange;
	}
}