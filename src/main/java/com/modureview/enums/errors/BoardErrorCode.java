package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BoardErrorCode implements ErrorCode {

  BOARD_ID_NOTFOUND(HttpStatus.NOT_FOUND,"게시글을 찾을 수 없습니다."),
  BOARD_SEARCH_KEYWORD_NOTFOUND( HttpStatus.NOT_FOUND, "키워드를 찾을 수 없습니다." );

  private final HttpStatus httpStatus;
  private final String message;

  BoardErrorCode(HttpStatus httpStatus, String message) {
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
