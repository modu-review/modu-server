package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BestReviewErrorCode implements ErrorCode {
  JSON_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON파싱중 에러가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String message;

  BestReviewErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
  @Override
  public String getMessage() {
    return message;
  }
}
