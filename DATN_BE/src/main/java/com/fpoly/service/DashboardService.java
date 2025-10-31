//package com.fpoly.service;
//
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.fpoly.dto.ChartPoint;
//import com.fpoly.dto.CourseRevenueDTO;
//import com.fpoly.dto.DashboardSummaryDTO;
//import com.fpoly.dto.MetricData;
//import com.fpoly.repository.CategoryRepository;
//import com.fpoly.repository.CommentRepository;
//import com.fpoly.repository.CourseProgressRepository;
//import com.fpoly.repository.CourseRepository;
//import com.fpoly.repository.RegisteredCourseRepository;
//import com.fpoly.repository.UserRepository;
//import com.fpoly.utils.TimeUtils;
//
//@Service
//public class DashboardService {
//	// DashboardService.java
//
//	@Autowired
//	private CourseProgressRepository courseProgressRepo;
//	@Autowired
//	private RegisteredCourseRepository registeredCourseRepo;
//	@Autowired
//	private CourseRepository courseRepo;
//	@Autowired
//	private CategoryRepository categoryRepo;
//	@Autowired
//	private CommentRepository commentRepo;
//	@Autowired
//	private UserRepository userRepo;
//
//	// (Giả sử bạn đã tạo các Repository và các DTO cần thiết)
//
//	public DashboardSummaryDTO getSummary(String filter) {
//		// 1. Lấy Khoảng Thời Gian
//		TimeUtils.DateRange range = TimeUtils.calculateDateRange(filter);
//
//		// 2. Tính toán các chỉ số (Metric Data)
//		DashboardSummaryDTO summary = new DashboardSummaryDTO();
//
//		// --- Số Khóa Học Hoàn Thành (Completed Courses) ---
//		summary.setCompletedCourses(calculateCompletedCoursesMetric(range));
//
//		// --- Tổng Doanh Thu (Total Revenue) ---
//		summary.setTotalRevenue(calculateRevenueMetric(range));
//
//		// --- Số Lượng User Mới ---
//		summary.setNewUsers(calculateNewUsersMetric(range));
//
//		// --- Số Lượng Khóa Học Mới ---
//		summary.setNewCourses(calculateNewCoursesMetric(range));
//
//		// --- Dữ liệu Biểu đồ (Ví dụ: Doanh thu theo thời gian) ---
//		summary.setRevenueChartData(getRevenueChartData(range.currentStart, range.currentEnd, filter));
//
//		// ... (Tiếp tục tính toán các chỉ số khác: Danh mục, Bình luận...)
//
//		return summary;
//	}
//
//	/**
//	 * Hàm tiện ích tính toán MetricData cho từng chỉ số
//	 */
//	private MetricData createMetricData(String title, String icon, long current, long previous) {
//		MetricData metric = new MetricData();
//		metric.setTitle(title);
//		metric.setIcon(icon);
//		metric.setCurrentValue(current);
//		metric.setPreviousValue(previous);
//
//		if (previous == 0) {
//			// Nếu kỳ trước bằng 0, coi là 100% tăng trưởng (hoặc một giá trị mặc định)
//			metric.setPercentageChange(current > 0 ? 100.0 : 0.0);
//		} else {
//			double change = ((double) (current - previous) / previous) * 100.0;
//			metric.setPercentageChange(change);
//		}
//		return metric;
//	}
//
//	// --- Các hàm tính toán chi tiết ---
//
//	private MetricData calculateCompletedCoursesMetric(TimeUtils.DateRange range) {
//		// Giả định CourseProgressRepository có hàm đếm:
//		// countByProgressPercentageGreaterThanEqualAndCourse_CreateAtBetween(100,
//		// startDate, endDate)
//
//		// Kỳ hiện tại: Đếm CourseProgress có progressPercentage >= 100
//		long currentCompleted = courseProgressRepo.countCompletedCourses(range.currentStart, range.currentEnd);
//
//		// Kỳ trước:
//		long previousCompleted = 0;
//		if (range.previousStart != null) {
//			previousCompleted = courseProgressRepo.countCompletedCourses(range.previousStart, range.previousEnd);
//		}
//
//		return createMetricData("Số Khóa Học Hoàn Thành", "🎓", currentCompleted, previousCompleted);
//	}
//
//	private MetricData calculateRevenueMetric(TimeUtils.DateRange range) {
//		// Giả định RegisteredCourseRepository có hàm tính tổng:
//		// sumRevenueByDateRange(startDate, endDate) trả về Float/Double
//
//		// Kỳ hiện tại: Tổng doanh thu (price) từ RegisteredCourse có statusPayment =
//		// true
//		Float currentRevenue = registeredCourseRepo.sumRevenueByDateRange(range.currentStart, range.currentEnd);
//		long current = (currentRevenue != null) ? currentRevenue.longValue() : 0;
//
//		// Kỳ trước:
//		Float previousRevenue = 0f;
//		if (range.previousStart != null) {
//			previousRevenue = registeredCourseRepo.sumRevenueByDateRange(range.previousStart, range.previousEnd);
//		}
//		long previous = (previousRevenue != null) ? previousRevenue.longValue() : 0;
//
//		return createMetricData("Tổng Doanh Thu", "💰", current, previous);
//	}
//
//	private MetricData calculateNewUsersMetric(TimeUtils.DateRange range) {
//		// Giả định UserRepository có hàm đếm:
//		// countByCreateAtBetween(startDate, endDate)
//
//		long currentCount = userRepo.countByCreateAtBetween(range.currentStart, range.currentEnd);
//
//		long previousCount = 0;
//		if (range.previousStart != null) {
//			previousCount = userRepo.countByCreateAtBetween(range.previousStart, range.previousEnd);
//		}
//
//		return createMetricData("Số Lượng User Mới", "👥", currentCount, previousCount);
//	}
//
//	private MetricData calculateNewCoursesMetric(TimeUtils.DateRange range) {
//		// Giả định CourseRepository có hàm đếm:
//		// countByCreateAtBetween(startDate, endDate)
//
//		long currentCount = courseRepo.countByCreateAtBetween(range.currentStart, range.currentEnd);
//
//		long previousCount = 0;
//		if (range.previousStart != null) {
//			previousCount = courseRepo.countByCreateAtBetween(range.previousStart, range.previousEnd);
//		}
//
//		return createMetricData("Số Lượng Khóa Học Mới", "🆕", currentCount, previousCount);
//	}
//
//	// --- Hàm lấy dữ liệu Biểu đồ ---
//	private List<ChartPoint> getRevenueChartData(Date startDate, Date endDate, String filter) {
//		// Tùy thuộc vào filter, bạn sẽ lấy dữ liệu theo ngày, tuần, hoặc tháng.
//		String format;
//		if ("LAST_YEAR".equals(filter)) {
//			format = "%Y-%m"; // Theo tháng
//		} else if ("LAST_WEEK".equals(filter) || "YESTERDAY".equals(filter)) {
//			format = "%Y-%m-%d"; // Theo ngày
//		} else {
//			format = "%Y-%m-%d"; // Mặc định theo ngày
//		}
//
//		// Giả định registeredCourseRepo có hàm truy vấn dữ liệu theo nhóm:
//		// List<Object[]> findRevenueGroupedBy(startDate, endDate, format)
//		// trả về (label, revenue)
//		List<Object[]> results = registeredCourseRepo.findRevenueGroupedBy(startDate, endDate, format);
//
//		// Chuyển đổi Object[] sang List<ChartPoint>
//		return results.stream().map(row -> {
//			ChartPoint point = new ChartPoint();
//			point.setLabel((String) row[0]);
//			point.setValue(((Number) row[1]).longValue());
//			return point;
//		}).collect(Collectors.toList());
//	}
//
//	public List<CourseRevenueDTO> getCourseStatistics(String sortBy) {
//		List<CourseRevenueDTO> data = courseRepo.getRawCourseRevenueStatistics();
//
//		Comparator<CourseRevenueDTO> comparator;
//
//		switch (sortBy.toUpperCase()) {
//		case "REVENUE_DESC":
//			// Sắp xếp theo doanh thu cao nhất (Sử dụng boxing primitive Long an toàn hơn)
//			comparator = Comparator.comparing(CourseRevenueDTO::getTotalRevenue).reversed();
//			break;
//		case "CERTIFICATE_DESC":
//			// Sắp xếp theo số chứng chỉ nhiều nhất (Sử dụng boxing primitive Long an toàn
//			// hơn)
//			comparator = Comparator.comparing(CourseRevenueDTO::getCertificateCount).reversed();
//			break;
//		case "NEWEST":
//		default:
//			// Sửa lỗi NullPointerException ở đây:
//			// Bọc Comparator bằng nullsLast() để xử lý Date == null
//			comparator = Comparator.comparing(CourseRevenueDTO::getCreateAt, Comparator.nullsLast(Date::compareTo))
//					.reversed(); // Khóa học mới nhất (Ngày lớn nhất) sẽ lên đầu
//			break;
//		}
//
//		return data.stream().sorted(comparator).collect(Collectors.toList());
//	}
//}

package com.fpoly.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.dto.ChartPoint;
import com.fpoly.dto.CourseRevenueDTO;
import com.fpoly.dto.DashboardSummaryDTO;
import com.fpoly.dto.MetricData;
import com.fpoly.repository.CategoryRepository;
import com.fpoly.repository.CommentRepository;
import com.fpoly.repository.CourseProgressRepository;
import com.fpoly.repository.CourseRepository;
import com.fpoly.repository.RegisteredCourseRepository;
import com.fpoly.repository.UserRepository;
import com.fpoly.utils.TimeUtils;

@Service
public class DashboardService {
	// DashboardService.java

	@Autowired
	private CourseProgressRepository courseProgressRepo;
	@Autowired
	private RegisteredCourseRepository registeredCourseRepo;
	@Autowired
	private CourseRepository courseRepo;
	@Autowired
	private CategoryRepository categoryRepo;
	@Autowired
	private CommentRepository commentRepo;
	@Autowired
	private UserRepository userRepo;

	public DashboardSummaryDTO getSummary(String filter) {
		// 1. Lấy Khoảng Thời Gian
		TimeUtils.DateRange range = TimeUtils.calculateDateRange(filter);

		// 2. Tính toán các chỉ số (Metric Data)
		DashboardSummaryDTO summary = new DashboardSummaryDTO();

		// --- Số Khóa Học Hoàn Thành (Completed Courses) ---
		summary.setCompletedCourses(calculateCompletedCoursesMetric(range));

		// --- Tổng Doanh Thu (Total Revenue) ---
		summary.setTotalRevenue(calculateRevenueMetric(range));

		// --- Số Lượng User Mới ---
		summary.setNewUsers(calculateNewUsersMetric(range));

		// --- Số Lượng Khóa Học Mới ---
		summary.setNewCourses(calculateNewCoursesMetric(range));

		// --- Dữ liệu Biểu đồ (Ví dụ: Doanh thu theo thời gian) ---
		summary.setRevenueChartData(getRevenueChartData(range.currentStart, range.currentEnd, filter));

		return summary;
	}

	/**
	 * Hàm tiện ích tính toán MetricData cho từng chỉ số
	 */
	private MetricData createMetricData(String title, String icon, long current, long previous) {
		MetricData metric = new MetricData();
		metric.setTitle(title);
		metric.setIcon(icon);
		metric.setCurrentValue(current);
		metric.setPreviousValue(previous);

		if (previous == 0) {
			metric.setPercentageChange(current > 0 ? 100.0 : 0.0);
		} else {
			double change = ((double) (current - previous) / previous) * 100.0;
			metric.setPercentageChange(change);
		}
		return metric;
	}

	// --- Các hàm tính toán chi tiết (Giữ nguyên) ---

	private MetricData calculateCompletedCoursesMetric(TimeUtils.DateRange range) {
		long currentCompleted = courseProgressRepo.countCompletedCourses(range.currentStart, range.currentEnd);
		long previousCompleted = 0;
		if (range.previousStart != null) {
			previousCompleted = courseProgressRepo.countCompletedCourses(range.previousStart, range.previousEnd);
		}
		return createMetricData("Số Khóa Học Hoàn Thành", "🎓", currentCompleted, previousCompleted);
	}

	private MetricData calculateRevenueMetric(TimeUtils.DateRange range) {
		Float currentRevenue = registeredCourseRepo.sumRevenueByDateRange(range.currentStart, range.currentEnd);
		long current = (currentRevenue != null) ? currentRevenue.longValue() : 0;

		Float previousRevenue = 0f;
		if (range.previousStart != null) {
			previousRevenue = registeredCourseRepo.sumRevenueByDateRange(range.previousStart, range.previousEnd);
		}
		long previous = (previousRevenue != null) ? previousRevenue.longValue() : 0;

		return createMetricData("Tổng Doanh Thu", "💰", current, previous);
	}

	private MetricData calculateNewUsersMetric(TimeUtils.DateRange range) {
		long currentCount = userRepo.countByCreateAtBetween(range.currentStart, range.currentEnd);
		long previousCount = 0;
		if (range.previousStart != null) {
			previousCount = userRepo.countByCreateAtBetween(range.previousStart, range.previousEnd);
		}
		return createMetricData("Số Lượng User Mới", "👥", currentCount, previousCount);
	}

	private MetricData calculateNewCoursesMetric(TimeUtils.DateRange range) {
		long currentCount = courseRepo.countByCreateAtBetween(range.currentStart, range.currentEnd);
		long previousCount = 0;
		if (range.previousStart != null) {
			previousCount = courseRepo.countByCreateAtBetween(range.previousStart, range.previousEnd);
		}
		return createMetricData("Số Lượng Khóa Học Mới", "🆕", currentCount, previousCount);
	}

	// ----------------------------------------------------------------------
	// --- HÀM MỚI: Định dạng nhãn biểu đồ (Khắc phục lỗi "Y-0-XX") ---
	// ----------------------------------------------------------------------

	/**
	 * Hàm tiện ích định dạng nhãn (label) từ kết quả SQL thô sang chuỗi thân thiện
	 * với người dùng. Ví dụ: "2025-10" -> "Tháng 10/2025"
	 */
	private String formatChartLabel(String rawLabel, String timeUnit) {
		if (rawLabel == null)
			return "N/A";

		try {
			if ("MONTH".equals(timeUnit) && rawLabel.matches("\\d{4}-\\d{2}")) {
				// Định dạng từ "YYYY-MM" sang "Tháng MM/YYYY"
				String year = rawLabel.substring(0, 4);
				String month = rawLabel.substring(5, 7);

				// Xóa số 0 đầu nếu có (cho thẩm mỹ)
				int monthInt = Integer.parseInt(month);
				return "Tháng " + monthInt + "/" + year;

			} else if ("DAY".equals(timeUnit) && rawLabel.matches("\\d{4}-\\d{2}-\\d{2}")) {
				// Định dạng từ "YYYY-MM-DD" sang "Ngày DD/MM"
				String day = rawLabel.substring(8, 10);
				String month = rawLabel.substring(5, 7);

				// Xóa số 0 đầu
				int dayInt = Integer.parseInt(day);
				int monthInt = Integer.parseInt(month);

				return "Ngày " + dayInt + "/" + monthInt;
			}
		} catch (Exception e) {
			// Bỏ qua lỗi định dạng và giữ nguyên nhãn thô
			return rawLabel;
		}
		return rawLabel;
	}

	// ----------------------------------------------------------------------
	// --- Hàm lấy dữ liệu Biểu đồ (Cập nhật logic filter) ---
	// ----------------------------------------------------------------------
	private List<ChartPoint> getRevenueChartData(Date startDate, Date endDate, String filter) {

		String format; // Format SQL (ví dụ: MySQL DATE_FORMAT)
		String timeUnit; // Đơn vị thời gian (để định dạng nhãn)

		switch (filter.toUpperCase()) {
		case "LAST_YEAR":
		case "ALL":
			// Lấy dữ liệu theo tháng. Nhãn: "Tháng MM/YYYY"
			format = "%Y-%m"; // MySQL format cho Năm-Tháng
			timeUnit = "MONTH";
			break;
		case "LAST_WEEK":
		case "YESTERDAY":
		case "LAST_MONTH":
		default:
			// Lấy dữ liệu theo ngày. Nhãn: "Ngày DD/MM"
			format = "%Y-%m-%d"; // MySQL format cho Năm-Tháng-Ngày
			timeUnit = "DAY";
			break;
		}

		// 1. Lấy dữ liệu thô từ Repository
		List<Object[]> rawResults = registeredCourseRepo.findRevenueGroupedBy(startDate, endDate, format);

		// 2. Chuyển đổi và định dạng nhãn
		return rawResults.stream().map(row -> {
			ChartPoint point = new ChartPoint();
			String rawLabel = (String) row[0];

			// Gán giá trị 0 nếu kết quả null (đảm bảo an toàn)
			long value = (row[1] != null) ? ((Number) row[1]).longValue() : 0L;

			// *** Dùng hàm tiện ích đã tạo để định dạng nhãn ***
			String formattedLabel = formatChartLabel(rawLabel, timeUnit);

			point.setLabel(formattedLabel);
			point.setValue(value);
			return point;
		}).collect(Collectors.toList());
	}

	// ----------------------------------------------------------------------
	// --- Hàm thống kê khóa học (Giữ nguyên) ---
	// ----------------------------------------------------------------------

	public List<CourseRevenueDTO> getCourseStatistics(String sortBy) {
		List<CourseRevenueDTO> data = courseRepo.getRawCourseRevenueStatistics();

		Comparator<CourseRevenueDTO> comparator;

		switch (sortBy.toUpperCase()) {
		case "REVENUE_DESC":
			comparator = Comparator.comparing(CourseRevenueDTO::getTotalRevenue).reversed();
			break;
		case "CERTIFICATE_DESC":
			comparator = Comparator.comparing(CourseRevenueDTO::getCertificateCount).reversed();
			break;
		case "NEWEST":
		default:
			comparator = Comparator.comparing(CourseRevenueDTO::getCreateAt, Comparator.nullsLast(Date::compareTo))
					.reversed();
			break;
		}

		return data.stream().sorted(comparator).collect(Collectors.toList());
	}
}