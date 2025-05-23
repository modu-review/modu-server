package com.modureview.exception.imageSaveError;

import com.modureview.enums.ErrorCode;
import com.modureview.enums.errors.ImageSaveErrorCode;
import com.modureview.exception.CustomException;

public class CreatPresignedUrlError extends CustomException {

  public CreatPresignedUrlError(ImageSaveErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }
}
