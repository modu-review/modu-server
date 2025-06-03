package com.modureview.exception.BoardError;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class NotAllowedHtmlError extends CustomException {

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }

  public NotAllowedHtmlError(ErrorCode errorCode) {
    super(errorCode);
  }
}
