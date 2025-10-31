package com.fpoly.service;

// ExcelService.java

import com.fpoly.dto.CourseRevenueDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

	public ByteArrayResource exportCourseStatistics(List<CourseRevenueDTO> data) {

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("ThongKeKhoaHoc");

			// --- 1. Tạo Header Row ---
			Row headerRow = sheet.createRow(0);
			String[] headers = { "ID", "Tên Khóa Học", "Ngày Tạo", "Doanh Thu", "Lượt Đăng Ký", "Số Chứng Chỉ" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				// Tùy chọn: Thêm style cho header
				// CellStyle headerStyle = workbook.createCellStyle();
				// Font font = workbook.createFont();
				// font.setBold(true);
				// headerStyle.setFont(font);
				// cell.setCellStyle(headerStyle);
			}

			// --- 2. Ghi Dữ liệu ---
			int rowNum = 1;
			for (CourseRevenueDTO dto : data) {
				Row row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(dto.getCourseId());
				row.createCell(1).setCellValue(dto.getCourseName());

				// *** KHẮC PHỤC LỖI NULL POINTER Ở ĐÂY ***
				// Kiểm tra null và thay thế bằng chuỗi "N/A" nếu createAt là null
				String createAtString = (dto.getCreateAt() != null) ? dto.getCreateAt().toString() : "N/A";

				row.createCell(2).setCellValue(createAtString);
				// ***************************************

				row.createCell(3).setCellValue(dto.getTotalRevenue());
				row.createCell(4).setCellValue(dto.getRegistrationCount());
				row.createCell(5).setCellValue(dto.getCertificateCount());
			}

			// Tự động điều chỉnh kích thước cột
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			// --- 3. Ghi Workbook vào ByteArrayOutputStream và trả về Resource ---
			workbook.write(out);

			return new ByteArrayResource(out.toByteArray());

		} catch (IOException e) {
			// Log lỗi chi tiết nếu cần
			e.printStackTrace();
			throw new RuntimeException("Lỗi khi tạo hoặc ghi file Excel: " + e.getMessage());
		}
	}
}