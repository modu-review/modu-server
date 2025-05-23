package com.modureview.exception.imageSaveError;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class CreateUuidError extends CustomException {

  public CreateUuidError(ErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }
}
