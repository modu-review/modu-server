package com.modureview.exception.user;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class BoardNotFoundException extends CustomException {
  public BoardNotFoundException() {
    super(ErrorCode.BOARD_NOT_FOUND);
  }
}
