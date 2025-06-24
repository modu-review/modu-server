package com.modureview.exception.bestReviewException;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class RedisConnectionException extends CustomException {

  @Override
  public ErrorCode getErrorCode() {
    return super.getErrorCode();
  }

  public RedisConnectionException(ErrorCode errorCode) {
    super(errorCode);
  }
}
