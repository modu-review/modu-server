package com.modureview.exception.BoardError;

import com.modureview.enums.ErrorCode;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.CustomException;

public class ImageSrcExtractError extends CustomException {

  public ImageSrcExtractError(BoardErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }


}
