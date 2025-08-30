package com.modureview.enums.errors;

import org.springframework.http.HttpStatus;

import com.modureview.enums.ErrorCode;

public enum TokenErrorCode implements ErrorCode {
	NICKNAME_NOT_FOUND("NICKNAME_NOT_FOUND", HttpStatus.NOT_FOUND, "닉네임을 찾을 수 없습니다. 발생 시각  : %s");

	private final String title;
	private final HttpStatus httpStatus;
	private final String detail;

	TokenErrorCode(String title , HttpStatus httpStatus, String detail) {
		this.title = title;
		this.httpStatus = httpStatus;
		this.detail = detail;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getDetail() {
		return detail;
	}
}
