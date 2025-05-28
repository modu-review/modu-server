package com.modureview.exception.BoardError;

import com.modureview.enums.ErrorCode;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.CustomException;

public class BoardSaveError extends CustomException {

  public BoardSaveError(BoardErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }
}
