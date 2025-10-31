package com.fpoly.dto;

//DashboardSummaryDTO.java
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {

	// --- Chỉ Số Tổng Quan (Metric Cards) ---

	// Số khóa học đã hoàn thành
	private MetricData completedCourses;

	// Tổng doanh thu theo khóa học
	private MetricData totalRevenue;

	// Số lượng khóa học mới
	private MetricData newCourses;

	// Số lượng danh mục mới
	private MetricData newCategories;

	// Số lượng bình luận mới
	private MetricData newComments;

	// Số lượng User mới
	private MetricData newUsers;

	// --- Dữ Liệu Biểu Đồ ---

	// Biểu đồ Tăng trưởng Doanh thu (Line/Bar Chart)
	private List<ChartPoint> revenueChartData;

	// Biểu đồ Tăng trưởng Người dùng mới (Line/Bar Chart)
	private List<ChartPoint> userGrowthChartData;

	// Biểu đồ cho số lượng khóa học mới đăng ký (Optional)
	private List<ChartPoint> registeredCourseChartData;

	// Dữ liệu cho biểu đồ phân bố danh mục (Pie/Donut Chart).
	// Có thể tái sử dụng ChartPoint: label=CategoryName, value=Count
	private List<ChartPoint> courseCategoryDistribution;
}
