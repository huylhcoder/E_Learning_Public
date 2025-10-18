package com.fpoly.config;

import com.fpoly.integration.openai.AiQuotaExceededException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode().isError();
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // Đọc nội dung lỗi từ body để xem nguyên nhân
                String responseBody = new BufferedReader(
                        new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                HttpStatus status = (HttpStatus) response.getStatusCode();

                if (status == HttpStatus.TOO_MANY_REQUESTS) {
                    throw new AiQuotaExceededException("⚠️ Quota OpenAI/OpenRouter đã hết. Chi tiết: " + responseBody);
                } else if (status.is5xxServerError()) {
                    // Ghi rõ chi tiết lỗi từ OpenRouter
                    throw new RuntimeException("🔥 Lỗi server OpenRouter (" + status + "): " + responseBody);
                } else if (status.is4xxClientError()) {
                    throw new HttpClientErrorException(status, "❌ Lỗi phía client: " + responseBody);
                } else {
                    throw new RuntimeException("❗Lỗi không xác định: " + status + " - " + responseBody);
                }
            }
        });
        return restTemplate;
    }
}
