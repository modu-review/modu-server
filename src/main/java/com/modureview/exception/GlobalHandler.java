package com.modureview.exception;

import com.modureview.dto.response.ErrorResponse;
import com.modureview.enums.ErrorCode;
import com.modureview.enums.errors.BoardErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    ErrorCode errorCode = e.getErrorCode();

    ErrorResponse response = new ErrorResponse(
        errorCode.getTitle(),
        errorCode.getHttpStatus().value(),
        errorCode.getDetail()
    );

    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(response);
  }


  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    ErrorCode errorCode = BoardErrorCode.INVALID_BOARD_ID_FORMAT;

    ErrorResponse response = new ErrorResponse(
        errorCode.getTitle(),
        errorCode.getHttpStatus().value(),
        errorCode.getDetail()
    );

    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(response);
  }
}
