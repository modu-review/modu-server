package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BoardErrorCode implements ErrorCode {
  BOARD_ID_NOTFOUND(HttpStatus.NOT_FOUND, "해당 ID의 리뷰를 찾을 수 없어요."),
  NOT_ALLOWED_HTML_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 html입니다."),
  BOARD_SAVE_ERROR(HttpStatus.CONFLICT, "데이터를 저장할 수 없습니다."),
  IMG_SRC_EXTRACT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "html에서 이미지 추출중 에러가 발생했습니다."),
  BOARD_SEARCH_KEYWORD_NOTFOUND(HttpStatus.NOT_FOUND, "키워드를 찾을 수 없습니다."),
  INVALID_BOARD_ID_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 파라미터 형식입니다.");

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
