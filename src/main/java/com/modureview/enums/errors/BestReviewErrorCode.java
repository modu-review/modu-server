package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BestReviewErrorCode implements ErrorCode {
  JSON_PROCESSING_ERROR("JSON_PARSING_ERROR",HttpStatus.INTERNAL_SERVER_ERROR, "JSON파싱중 에러가 발생했습니다.");

  private final String title;
  private final HttpStatus httpStatus;
  private final String detail;

  BestReviewErrorCode(String title ,HttpStatus httpStatus, String detail) {
    this.title = title;
    this.httpStatus = httpStatus;
    this.detail = detail;
  }

  @Override
  public String getTitle() {
    return "";
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  @Override
  public String getDetail() {
    return "";
  }

}
