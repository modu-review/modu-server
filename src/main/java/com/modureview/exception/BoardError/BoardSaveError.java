package com.modureview.exception.BoardError;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class BoardSaveError extends CustomException {

  public BoardSaveError(ErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }
}
