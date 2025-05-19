package com.modureview.enums;

import org.springframework.http.HttpStatus;

public enum BoardErrorCode {

  BOARD_ID_NOTFOUND(HttpStatus.NOT_FOUND,"게시글을 찾을 수 없습니다.");


  private final HttpStatus httpStatus;
  private final String message;

  BoardErrorCode(HttpStatus httpStatus, String message) {
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

