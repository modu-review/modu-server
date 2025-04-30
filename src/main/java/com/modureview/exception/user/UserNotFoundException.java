package com.modureview.exception.user;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class UserNotFoundException extends CustomException {

  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }
}