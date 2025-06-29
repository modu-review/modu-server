package com.modureview.exception.bookmark;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class BoardNotExistException extends CustomException {

  public BoardNotExistException(ErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }
}
