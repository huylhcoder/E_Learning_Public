//package com.fpoly.integration.openai;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//@Component
//public class OpenAiClient {
//
//    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);
//
//    @Value("${ai.api.key}")
//    private String apiKey;
//
//    @Value("${ai.api.base-url:https://openrouter.ai/api/v1}")
//    private String baseUrl;
//
//    private final RestTemplate restTemplate;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    public OpenAiClient(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public AiResult analyzeGoalToJson(String goal, String level, String categories) {
//        try {
//            // 🧠 Prompt chi tiết, yêu cầu JSON hợp lệ
//            String prompt = String.format("""
//                    Bạn là chuyên gia hướng nghiệp trong lĩnh vực CNTT, có 10 năm kinh nghiệm giúp sinh viên lập kế hoạch học tập.
//
//                    Hãy tạo **JSON hợp lệ (chỉ JSON, không thêm văn bản ngoài)** theo cấu trúc sau:
//                    {
//                      "roadmapName": "Tên lộ trình học tập",
//                      "category": "Tên lĩnh vực chính",
//                      "explanation": "Mô tả tổng quan (tối thiểu 150 từ, rõ ràng, có động lực học tập)",
//                      "steps": [
//                        {
//                          "title": "Tên giai đoạn 1",
//                          "description": "Chi tiết nội dung, kỹ năng cần học",
//                          "skills": ["Kỹ năng 1", "Kỹ năng 2"]
//                        },
//                        {
//                          "title": "Tên giai đoạn 2",
//                          "description": "Ví dụ: học Spring Boot, API, Database",
//                          "skills": ["Spring", "REST API", "MySQL"]
//                        }
//                      ],
//                      "suggestedKeywords": ["Java cơ bản", "OOP", "Spring Boot", "MySQL"]
//                    }
//
//                    Thông tin đầu vào:
//                    - Mục tiêu: %s
//                    - Trình độ: %s
//                    - Lĩnh vực: %s
//
//                    Chỉ trả về JSON hợp lệ duy nhất, không thêm lời giải thích.
//                    """, goal, level, categories);
//
//            // 🧩 Endpoint OpenRouter
//            String url = baseUrl + "/chat/completions";
//
//            // 🧱 Header bắt buộc
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(apiKey);
//            headers.add("HTTP-Referer", "http://localhost:8080"); // Đổi sang domain thật nếu deploy
//            headers.add("X-Title", "FPoly AI Roadmap Generator");
//
//            // 🧩 Dùng ObjectMapper để build JSON body an toàn
//            ObjectNode bodyNode = mapper.createObjectNode();
//            bodyNode.put("model", "openai/gpt-3.5-turbo"); // Free tier model
//            bodyNode.put("temperature", 0.8);
//
//            ArrayNode messages = mapper.createArrayNode();
//            messages.addObject()
//                    .put("role", "system")
//                    .put("content", "Bạn là chuyên gia gợi ý lộ trình học tập IT.");
//            messages.addObject()
//                    .put("role", "user")
//                    .put("content", prompt);
//            bodyNode.set("messages", messages);
//
//            String body = mapper.writeValueAsString(bodyNode);
//
//            // 🧩 Gửi request
//            HttpEntity<String> entity = new HttpEntity<>(body, headers);
//            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
//
//            // 🧩 Kiểm tra phản hồi
//            JsonNode root = mapper.readTree(response.getBody());
//            JsonNode choices = root.path("choices");
//
//            if (choices.isMissingNode() || !choices.isArray() || choices.isEmpty()) {
//                throw new RuntimeException("Phản hồi từ OpenRouter không hợp lệ: " + response.getBody());
//            }
//
//            String content = choices.get(0).path("message").path("content").asText();
//            if (content == null || content.isBlank()) {
//                throw new RuntimeException("OpenRouter không trả về nội dung nào");
//            }
//
//            // 🧹 Làm sạch nếu có ```json
//            String jsonPart = content.trim();
//            if (jsonPart.startsWith("```")) {
//                jsonPart = jsonPart.replaceAll("```json", "").replaceAll("```", "").trim();
//            }
//
//            // 🧩 Parse JSON thật sự
//            JsonNode aiJson;
//            try {
//                aiJson = mapper.readTree(jsonPart);
//            } catch (Exception parseEx) {
//                log.error("❌ Không thể parse JSON từ OpenRouter:\n{}", jsonPart);
//                throw new RuntimeException("OpenRouter trả về dữ liệu không phải JSON hợp lệ");
//            }
//
//            // 🧩 Tạo đối tượng kết quả
//            AiResult result = new AiResult();
//            result.setRoadmapName(aiJson.path("roadmapName").asText("Lộ trình học gợi ý"));
//            result.setCategory(aiJson.path("category").asText("General"));
//            result.setExplanation(aiJson.path("explanation").asText("Không có mô tả chi tiết."));
//
//            log.info("✅ Nhận phản hồi thành công từ OpenRouter: {}", result);
//            return result;
//
//        } catch (Exception e) {
//            log.error("❌ Lỗi khi gọi OpenRouter API: {}", e.getMessage(), e);
//            throw new RuntimeException("Lỗi khi gọi OpenRouter API: " + e.getMessage(), e);
//        }
//    }
//
//    // 🧾 DTO kết quả trả về từ AI
//    public static class AiResult {
//        private String roadmapName;
//        private String category;
//        private String explanation;
//
//        public String getRoadmapName() {
//            return roadmapName;
//        }
//
//        public void setRoadmapName(String roadmapName) {
//            this.roadmapName = roadmapName;
//        }
//
//        public String getCategory() {
//            return category;
//        }
//
//        public void setCategory(String category) {
//            this.category = category;
//        }
//
//        public String getExplanation() {
//            return explanation;
//        }
//
//        public void setExplanation(String explanation) {
//            this.explanation = explanation;
//        }
//
//        @Override
//        public String toString() {
//            return "AiResult{" +
//                    "roadmapName='" + roadmapName + '\'' +
//                    ", category='" + category + '\'' +
//                    ", explanation='" + (explanation != null ? explanation.substring(0, Math.min(60, explanation.length())) + "..." : null) + '\'' +
//                    '}';
//        }
//    }
//}

package com.fpoly.integration.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OpenAiClient {

	private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);

	@Value("${ai.api.key}")
	private String apiKey;

	@Value("${ai.api.base-url}")
	private String baseUrl;

	private final RestTemplate restTemplate;
	private final ObjectMapper mapper = new ObjectMapper();

	public OpenAiClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public AiResult analyzeGoalToJson(String goal, String level, String categories) {
		AiResult result = new AiResult();
		try {
			result.setRoadmapName("Lộ trình " + categories);
			result.setCategory(categories);

			// 🧠 Request 1: Sinh mô tả tổng quan
			String explanationPrompt = String.format("""
					Viết phần mô tả tổng quan (150 từ trở lên) cho lộ trình học "%s" ở trình độ "%s".
					Không cần trả JSON, chỉ cần đoạn văn mô tả ngắn gọn, truyền cảm hứng.
					""", goal, level);
			String explanation = callOpenRouter(explanationPrompt);
			result.setExplanation(explanation);

			// 🧠 Request 2: Sinh các bước học (steps)
			String stepsPrompt = String.format("""
					Tạo danh sách 4-6 giai đoạn học cho mục tiêu "%s" với lĩnh vực "%s".
					Mỗi giai đoạn gồm:
					- title: tên giai đoạn
					- description: mô tả chi tiết 2-3 câu
					- skills: danh sách kỹ năng cần học
					Trả về đúng JSON array như ví dụ:
					[
					  {"title": "...", "description": "...", "skills": ["...", "..."]}
					]
					""", goal, categories);
			String stepsJson = callOpenRouter(stepsPrompt);
			result.setStepsJson(stepsJson);

			// 🧠 Request 3: Sinh từ khóa gợi ý
			String keywordsPrompt = String.format("""
					Cho 5-8 từ khóa phù hợp với mục tiêu "%s" và lĩnh vực "%s".
					Chỉ trả về mảng JSON ví dụ: ["Java", "Spring Boot", "REST API"]
					""", goal, categories);
			String keywords = callOpenRouter(keywordsPrompt);
			result.setSuggestedKeywordsJson(keywords);

			log.info("✅ Hoàn thành tạo lộ trình với 3 request thành công.");
			return result;

		} catch (Exception e) {
			log.error("❌ Lỗi khi gọi OpenRouter API: {}", e.getMessage(), e);
			throw new RuntimeException("Lỗi khi tạo lộ trình: " + e.getMessage(), e);
		}
	}

	private String callOpenRouter(String prompt) {
		try {
			String url = baseUrl + "/chat/completions";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(apiKey);
			headers.add("HTTP-Referer", "http://localhost:8080");
			headers.add("X-Title", "FPoly AI Roadmap Generator");

			String body = mapper.writeValueAsString(
					mapper.createObjectNode().put("model", "openai/gpt-3.5-turbo").put("temperature", 0.8)
							.set("messages", mapper.createArrayNode()
									.add(mapper.createObjectNode().put("role", "system").put("content",
											"Bạn là chuyên gia gợi ý học tập."))
									.add(mapper.createObjectNode().put("role", "user").put("content", prompt))));

			HttpEntity<String> entity = new HttpEntity<>(body, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

			JsonNode root = mapper.readTree(response.getBody());
			String content = root.path("choices").get(0).path("message").path("content").asText();

			// Làm sạch nếu có markdown
			if (content.startsWith("```")) {
				content = content.replaceAll("```json", "").replaceAll("```", "").trim();
			}

			return content.trim();

		} catch (Exception e) {
			log.error("🔥 Lỗi server OpenRouter: {}", e.getMessage());
			throw new RuntimeException("Không thể kết nối hoặc parse OpenRouter: " + e.getMessage(), e);
		}
	}

	// 🧾 DTO kết quả
	public static class AiResult {
		private String roadmapName;
		private String category;
		private String explanation;
		private String stepsJson;
		private String suggestedKeywordsJson;

		// Getters + setters
		public String getRoadmapName() {
			return roadmapName;
		}

		public void setRoadmapName(String roadmapName) {
			this.roadmapName = roadmapName;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getExplanation() {
			return explanation;
		}

		public void setExplanation(String explanation) {
			this.explanation = explanation;
		}

		public String getStepsJson() {
			return stepsJson;
		}

		public void setStepsJson(String stepsJson) {
			this.stepsJson = stepsJson;
		}

		public String getSuggestedKeywordsJson() {
			return suggestedKeywordsJson;
		}

		public void setSuggestedKeywordsJson(String suggestedKeywordsJson) {
			this.suggestedKeywordsJson = suggestedKeywordsJson;
		}

		@Override
		public String toString() {
			return "AiResult{" + "roadmapName='" + roadmapName + '\'' + ", category='" + category + '\''
					+ ", explanation='"
					+ (explanation != null ? explanation.substring(0, Math.min(50, explanation.length())) + "..."
							: null)
					+ '\'' + '}';
		}
	}
}
