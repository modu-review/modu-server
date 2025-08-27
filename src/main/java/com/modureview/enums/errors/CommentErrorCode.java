package com.modureview.enums.errors;

import org.springframework.http.HttpStatus;

import com.modureview.enums.ErrorCode;

public enum CommentErrorCode implements ErrorCode {
	COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND,"일치하지 않는 댓글 아이디입니다."),
	COMMENT_USER_NOT_EQUAL("COMMENT_USER_NOT_EQUAL", HttpStatus.FORBIDDEN,"댓글 작성자가 아닙니다.");

	private final String title;
	private final HttpStatus httpStatus;
	private final String detail;

	CommentErrorCode(String title, HttpStatus httpStatus, String detail) {
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
