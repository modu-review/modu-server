package com.modureview.enums;

public enum ErrorCode {
  USER_NOT_FOUND(401, "로그인한 사용자만 이용할 수 있습니다."),
  BOARD_NOT_FOUND(404, "게시글을 찾을 수 없습니다.");

  private final int status;
  private final String message;

  ErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
