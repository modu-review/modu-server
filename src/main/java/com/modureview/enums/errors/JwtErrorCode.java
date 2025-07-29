package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum JwtErrorCode implements ErrorCode {

  UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
  FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN, "유효하지 않은 사용자입니다.");

  private final String title;
  private final HttpStatus httpStatus;
  private final String detail;

  JwtErrorCode(String title, HttpStatus httpStatus, String detail) {
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