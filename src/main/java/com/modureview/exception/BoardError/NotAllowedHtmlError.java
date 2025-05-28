package com.modureview.exception.BoardError;

import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.CustomException;

public class NotAllowedHtmlError extends CustomException {

  public NotAllowedHtmlError(BoardErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }

}
