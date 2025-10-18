package com.fpoly.integration.openai;

public class AiQuotaExceededException extends RuntimeException {
	public AiQuotaExceededException(String msg) {
		super(msg);
	}
}
