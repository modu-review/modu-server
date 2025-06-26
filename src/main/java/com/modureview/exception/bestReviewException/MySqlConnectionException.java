package com.modureview.exception.bestReviewException;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class MySqlConnectionException extends CustomException {

  public MySqlConnectionException(ErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public ErrorCode getErrorCode() {
    return super.getErrorCode();
  }
}
