package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
  USER_NOT_FOUND("USER_NOT_FOUND",HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");

  private final String title;
  private final HttpStatus httpStatus;
  private final String detail;


  UserErrorCode(String title, HttpStatus httpStatus, String detail) {
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
