package com.modureview.exception;

import com.modureview.dto.response.ErrorResponse;
import com.modureview.enums.ErrorCode;
import com.modureview.enums.JwtErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    ErrorCode errorCode = e.getErrorCode();

    ErrorResponse response = new ErrorResponse(
        errorCode.getHttpStatus().value(),
        errorCode.getMessage()
    );

    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(response);
  }
}
