package com.modureview.exception.bookmark;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class BookmarkNotExistException extends CustomException {

  public BookmarkNotExistException(ErrorCode errorCode) {
    super(errorCode);
  }

  @Override
  public String getErrorMessage() {
    return super.getErrorMessage();
  }
}
