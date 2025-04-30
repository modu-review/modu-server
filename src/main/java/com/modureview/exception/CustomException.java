package com.modureview.exception;

import com.modureview.enums.ErrorCode;

public abstract class CustomException extends RuntimeException {
  private final ErrorCode errorCode;


  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public CustomException(ErrorCode code, Object... args) {
    super(String.format(code.getMessage(), args));
    this.errorCode = code;
  }


  public ErrorCode getErrorCode() {
    return errorCode;
  }
}