package com.modureview.exception.jwtError;

import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.CustomException;

public class TokenExpiredException extends CustomException {

  public TokenExpiredException(JwtErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }
}
