package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BookmarkErrorCode implements ErrorCode {
  BOOKMARK_NOT_FOUND("BOOKMARK_NOT_FOUND",HttpStatus.NOT_FOUND, "북마크를 찾을 수 없습니다."),
  ;

  private final String title;
  private final HttpStatus httpStatus;
  private final String detail;

  BookmarkErrorCode(String title, HttpStatus httpStatus, String detail) {
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
    return null;
  }

  @Override
  public String getDetail() {
    return "";
  }

}
