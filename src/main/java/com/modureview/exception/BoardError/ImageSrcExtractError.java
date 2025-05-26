package com.modureview.exception.BoardError;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class ImageSrcExtractError extends CustomException {

  public ImageSrcExtractError(ErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }


}
