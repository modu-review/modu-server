package com.modureview.exception.mypage;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class FileUploadFailed extends CustomException {

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }

  public FileUploadFailed(ErrorCode errorCode) {
    super(errorCode);
  }
}
