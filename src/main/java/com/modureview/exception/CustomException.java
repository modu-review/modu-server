package com.modureview.exception;

import com.modureview.enums.ErrorCode;

public class CustomException extends RuntimeException {
  private final ErrorCode errorCode;


  public CustomException(ErrorCode errorCode) {
    super(errorCode.getDetail());
    this.errorCode = errorCode;
  }

  public String title() {
    return errorCode.getTitle();
  }
  public ErrorCode getErrorCode() {
    return this.errorCode;
  }


  public String getErrorMessage() {
    return errorCode.getDetail();
  }
}