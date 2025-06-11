package com.modureview.exception.bestReviewException;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class JsonParsingFromRedisException extends CustomException {

  public JsonParsingFromRedisException(ErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public ErrorCode getErrorCode() {
    return super.getErrorCode();
  }

}
