package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MypageErrorCode implements ErrorCode {
  UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "저장할 수 없는 이미지 형식입니다.");


  private final String title;
  private final HttpStatus httpStatus;
  private final String detail;

  MypageErrorCode(String title, HttpStatus httpStatus, String detail) {
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
