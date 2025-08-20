package com.fpoly.cloudinary;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.EagerTransformation;
import com.cloudinary.utils.ObjectUtils;
import io.jsonwebtoken.io.IOException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
public class VideoService {

	@Autowired
	private Cloudinary cloudinary;

	// Nếu video <= 10MB thì up lên Cloudinary
	public Map<?, ?> uploadVideoOnCloudinary(MultipartFile file) throws IOException {
		try {
			// Kiểm tra định dạng file
			String contentType = file.getContentType();
			if (contentType == null || !contentType.startsWith("video/")) {
				throw new IllegalArgumentException("File không phải là video hợp lệ.");
			}

			// Đọc nội dung file vào byte array
			byte[] bytes = file.getBytes();

			System.out.println(file.getName());

			String dateString = "";
			Date date = new Date();
			dateString = date.toString();

			Map<String, Object> options = ObjectUtils.asMap("resource_type", "video", "public_id", dateString, "eager",
					Arrays.asList(new EagerTransformation().width(300).height(300).crop("pad").audioCodec("none"),
							new EagerTransformation().width(160).height(100).crop("crop").gravity("south")
									.audioCodec("none")),
					"eager_async", true, "eager_notification_url", "https://mysite.example.com/notify_endpoint");

			// Upload video sử dụng byte array
			Map<?, ?> data = cloudinary.uploader().upload(bytes, options);
			return data;

		} catch (IOException io) {
			throw new RuntimeException("Up video thất bại: " + io.getMessage());
		} catch (Exception e) {
			e.printStackTrace(); // In ra stack trace để dễ dàng gỡ lỗi
			throw new RuntimeException("Up video thất bại: " + e.getMessage());
		}
	}

	// private final String UPLOAD_DIR = "src/main/webapp/uploads/";

//	public String uploadVideo(MultipartFile file) throws IOException, java.io.IOException {
//		if (!file.isEmpty()) {
//			Date date = new Date();
//			System.out.println("Video service - Date upload video: " + date);
//			String fileName = file.getOriginalFilename() + date;
//			Path path = Paths.get(UPLOAD_DIR + fileName);
//			Files.createDirectories(path.getParent());
//			Files.write(path, file.getBytes());
//			// Trả về đường dẫn video
//			return path.toString();
//		}
//		return null;
//	}
	private final String UPLOAD_DIR = "src/main/webapp/uploads/";

	public String uploadVideo(MultipartFile file) throws IOException, java.io.IOException {
		if (!file.isEmpty()) {
			Date date = new Date();
			System.out.println("Video service - Date upload video: " + date);

			// Lấy tên tệp gốc và mã hóa nó
			String originalFileName = file.getOriginalFilename();
			String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8") + "_" + date.getTime(); // Thêm
																											// timestamp
																											// để tránh
																											// trùng lặp

			Path path = Paths.get(UPLOAD_DIR + encodedFileName);
			Files.createDirectories(path.getParent());
			Files.write(path, file.getBytes());
			// Trả về đường dẫn video
			return path.toString();
		}
		return null;
	}

	public byte[] downloadVideo(String videoUrl) throws IOException, java.io.IOException {

		URL url = new URL(videoUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect(); // Kết nối đến URL

		System.out.println("Show video");
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException("HTTP error code: " + connection.getResponseCode());
		}

		try (InputStream inputStream = connection.getInputStream()) {
			return inputStream.readAllBytes();
		}

	}
}
