package com.modureview.exception;

import com.modureview.enums.ErrorCode;

public class CustomException extends RuntimeException {
  private final ErrorCode errorCode;


  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return this.errorCode;
  }


  public String getErrorMessage() {
    return errorCode.getMessage();
  }
}