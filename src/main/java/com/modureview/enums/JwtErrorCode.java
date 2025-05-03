package com.modureview.enums;

import org.springframework.http.HttpStatus;

public enum JwtErrorCode {

  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "유효하지 않은 사용자입니다.");

  private final HttpStatus httpStatus;
  private final String message;

  JwtErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public String getMessage() {
    return message;
  }
}
