package com.modureview.enums;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
  HttpStatus getHttpStatus();
  String getMessage();


}
