package com.fpoly.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoogleLoginRequest {
    // Trường này chứa ID token (credential) gửi từ frontend
    private String credential;
}