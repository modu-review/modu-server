package com.modureview.enums;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
  String getTitle();
  HttpStatus getHttpStatus();
  String getDetail();
}
