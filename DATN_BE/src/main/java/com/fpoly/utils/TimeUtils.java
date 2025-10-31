package com.fpoly.utils;

//TimeUtils.java
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeUtils {

	// Helper class để giữ 4 giá trị ngày tháng cần thiết
	public static class DateRange {
		public Date currentStart;
		public Date currentEnd;
		public Date previousStart;
		public Date previousEnd;
	}

	// Hàm chính để tính toán 4 giá trị ngày tháng dựa trên bộ lọc
	public static DateRange calculateDateRange(String filter) {
		DateRange range = new DateRange();
		LocalDate now = LocalDate.now();

		switch (filter.toUpperCase()) {
		case "YESTERDAY":
			LocalDate yesterday = now.minusDays(1);
			LocalDate dayBeforeYesterday = now.minusDays(2);

			// Kỳ hiện tại: Ngày hôm qua
			range.currentStart = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
			range.currentEnd = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()); // Đến đầu ngày hôm nay

			// Kỳ trước: Ngày hôm kia
			range.previousStart = Date.from(dayBeforeYesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
			range.previousEnd = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
			break;

		case "LAST_WEEK":
			// Kỳ hiện tại: 7 ngày trước (từ hôm nay lùi về 7 ngày)
			// Hoặc bạn có thể dùng tuần trước:
			// now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1)

			// Tính theo 7 ngày gần nhất so với 7 ngày trước đó
			range.currentEnd = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
			range.currentStart = Date.from(now.minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());

			range.previousEnd = range.currentStart;
			range.previousStart = Date.from(now.minusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant());
			break;

		case "LAST_MONTH":
			// Kỳ hiện tại: 30 ngày gần nhất
			range.currentEnd = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
			range.currentStart = Date.from(now.minusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant());

			// Kỳ trước: 30 ngày trước đó
			range.previousEnd = range.currentStart;
			range.previousStart = Date.from(now.minusDays(60).atStartOfDay(ZoneId.systemDefault()).toInstant());
			break;

		case "LAST_YEAR":
			// Kỳ hiện tại: 365 ngày gần nhất
			range.currentEnd = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
			range.currentStart = Date.from(now.minusDays(365).atStartOfDay(ZoneId.systemDefault()).toInstant());

			// Kỳ trước: 365 ngày trước đó
			range.previousEnd = range.currentStart;
			range.previousStart = Date.from(now.minusDays(730).atStartOfDay(ZoneId.systemDefault()).toInstant());
			break;

		case "ALL":
		default:
			// Đối với "ALL", chỉ tính toán kỳ hiện tại, kỳ trước có thể bằng 0 hoặc bỏ qua
			range.currentStart = null; // Bắt đầu từ đầu
			range.currentEnd = Date.from(now.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()); // Đến hiện
																											// tại

			range.previousStart = null;
			range.previousEnd = null;
		}

		// Đặt mặc định ngày kết thúc hiện tại là thời điểm hiện tại
		if (range.currentEnd == null) {
			range.currentEnd = new Date();
		}

		return range;
	}
}