package com.modureview.exception.jwtError;

import com.modureview.enums.JwtErrorCode;
import com.modureview.exception.CustomException;

public class TokenExpiredError extends CustomException {

  public TokenExpiredError(JwtErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }
}
