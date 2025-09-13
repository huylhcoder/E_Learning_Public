package com.fpoly.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
	@JsonProperty("fullname")
	private String fullname;

	@JsonProperty("email")
	private String email;

	@NotBlank(message = "Password cannot be blank")
	private String password;

	@JsonProperty("retype_password")
	private String retypePassword;

	@JsonProperty("facebook_account_id")
	private int facebookAccountId;

	@JsonProperty("google_account_id")
	private int googleAccountId;
	
    @JsonProperty("otp")
    private String otp;   // 👈 thêm trường OTP để BE nhận từ FE
}
