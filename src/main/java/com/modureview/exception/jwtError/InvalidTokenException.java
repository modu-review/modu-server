package com.modureview.exception.jwtError;

import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.CustomException;

public class InvalidTokenException extends CustomException {

  public InvalidTokenException(JwtErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }
}
