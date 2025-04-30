package com.modureview.exception.user;

import com.modureview.enums.ErrorCode;
import com.modureview.exception.CustomException;

public class UserEmailNotFound extends CustomException {
  public UserEmailNotFound(String email) {
    super(ErrorCode.USER_EMAIL_NOT_FOUND,email);
  }
}
