package com.modureview.exception.mypage;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class InvalidFormatException extends CustomException {

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }

  public InvalidFormatException(ErrorCode errorCode) {
    super(errorCode);
  }
}
