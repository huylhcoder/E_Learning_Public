package com.fpoly.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    private final String PREFIX = "REGISTER_OTP:"; // key trong redis

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Lưu OTP vào redis với TTL 5 phút
    public void saveOTP(String email, String code) {
        redisTemplate.opsForValue().set(PREFIX + email, code, 5, TimeUnit.MINUTES);
    }

    // Kiểm tra OTP
    public boolean verifyOTP(String email, String code) {
        String redisCode = redisTemplate.opsForValue().get(PREFIX + email);
        return redisCode != null && redisCode.equals(code);
    }

    // Xóa OTP sau khi dùng
    public void clearOTP(String email) {
        redisTemplate.delete(PREFIX + email);
    }
}

