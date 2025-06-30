package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BookmarkErrorCode implements ErrorCode {
  BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "북마크를 찾을 수 없습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;

  BookmarkErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return null;
  }

  @Override
  public String getMessage() {
    return "";
  }
}
