package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum JwtErrorCode implements ErrorCode {

  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "유효하지 않은 사용자입니다.");

  private final HttpStatus httpStatus;
  private final String message;

  JwtErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }

  @Override // 인터페이스 메서드 구현 명시
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  @Override // 인터페이스 메서드 구현 명시
  public String getMessage() {
    return message;
  }
}