package com.fpoly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpoly.dto.CourseRevenueDTO;
import com.fpoly.dto.DashboardSummaryDTO;
import com.fpoly.service.DashboardService;
import com.fpoly.service.ExcelService;

@CrossOrigin("*") // Cho phép bên ngoài truy xuất vào thoải mái, không ngăn cản gì cả
@RestController
@RequestMapping("${api.prefix}/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private ExcelService excelService;

	@GetMapping("/summary")
	public ResponseEntity<DashboardSummaryDTO> getDashboardSummary(
			@RequestParam(name = "filter", defaultValue = "LAST_MONTH") String filter) {
		// 'filter' có thể là: YESTERDAY, LAST_WEEK, LAST_MONTH, LAST_YEAR, ALL
		DashboardSummaryDTO summary = dashboardService.getSummary(filter);
		return ResponseEntity.ok(summary);
	}

	@GetMapping("/course-statistics")
	public ResponseEntity<List<CourseRevenueDTO>> getCourseStatistics(
			@RequestParam(name = "sortBy", defaultValue = "NEWEST") String sortBy) {
		List<CourseRevenueDTO> statistics = dashboardService.getCourseStatistics(sortBy);
		return ResponseEntity.ok(statistics);
	}

	// 4.2. Thêm Endpoint Xuất Excel
//	@GetMapping("/course-statistics/export-excel")
//	public ResponseEntity<Resource> exportCourseStatistics(
//			@RequestParam(name = "sortBy", defaultValue = "NEWEST") String sortBy) {
//		// Tạo file Excel từ dữ liệu thống kê
//		ByteArrayResource resource = excelService.exportCourseStatistics(sortBy);
//
//		return ResponseEntity.ok()
//				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"thong_ke_doanh_thu_khoa_hoc.xlsx\"")
//				.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
//	}
	@GetMapping("/course-statistics/export-excel")
	public ResponseEntity<Resource> exportCourseStatistics(
			@RequestParam(name = "sortBy", defaultValue = "NEWEST") String sortBy) {
		// 1. Lấy dữ liệu
		List<CourseRevenueDTO> statistics = dashboardService.getCourseStatistics(sortBy);

		// 2. Tạo file Excel (được đóng gói dưới dạng ByteArrayResource)
		ByteArrayResource resource = excelService.exportCourseStatistics(statistics);

		// 3. Trả về ResponseEntity
		return ResponseEntity.ok()
				// Header Content-Disposition buộc trình duyệt tải file xuống
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"thong_ke_doanh_thu_khoa_hoc.xlsx\"")
				// Loại nội dung (MIME Type) cho file Excel (.xlsx)
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				// Đặt kích thước file
				.contentLength(resource.contentLength()).body(resource); // Gửi nội dung file
	}

}
