package com.modureview.exception;

import com.modureview.enums.JwtErrorCode;

public class CustomException extends RuntimeException {
  private final JwtErrorCode errorCode;

  public CustomException(JwtErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public JwtErrorCode getErrorCode() {
    return this.errorCode;
  }
  public String getErrorMessage() {
    return errorCode.getMessage();
  }
}
