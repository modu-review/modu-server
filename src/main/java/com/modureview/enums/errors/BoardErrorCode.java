package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BoardErrorCode implements ErrorCode {
  BOARD_ID_NOTFOUND("BOARD_ID_NOT_FOUND", HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
  NOT_ALLOWED_HTML("WRONG_HTML_TYPE", HttpStatus.INTERNAL_SERVER_ERROR, "잘못된 html입니다."),
  BOARD_SAVE_ERROR("CAN_NOT_SAVE_DATA", HttpStatus.INTERNAL_SERVER_ERROR, "데이터를 저장할 수 없습니다."),
  IMG_SRC_EXTRACT_ERROR("EXTRACT_IMAGE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR,
      "html에서 이미지 추출중 에러가 발생했습니다."),
  BOARD_SEARCH_KEYWORD_NOTFOUND("KEYWORD_NOT_FOUND", HttpStatus.NOT_FOUND, "키워드를 찾을 수 없습니다."),
  BOARD_NOT_EXIST("BOARD_NOT_EXIST", HttpStatus.INTERNAL_SERVER_ERROR, "게시글이 존재하지 않습니다."),
  BOARD_USER_NOT_EQUALS("BOARD_USER_NOT_EQUALS", HttpStatus.FORBIDDEN, "개사글 작성한 유저와 동일하지 않습니다."),
  INVALID_BOARD_ID_FORMAT("INVALID_BOARD_ID_FORMAT", HttpStatus.BAD_REQUEST, "잘못된 파라미터 형식입니다.");

  private final String title;
  private final HttpStatus httpStatus;
  private final String detail;

  BoardErrorCode(String title, HttpStatus httpStatus, String detail
  ) {
    this.httpStatus = httpStatus;
    this.title = title;
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
