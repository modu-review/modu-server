package com.modureview.exception.bestReviewException;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class JsonParsingException extends CustomException {

  public JsonParsingException(ErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public ErrorCode getErrorCode() {
    return super.getErrorCode();
  }

}
