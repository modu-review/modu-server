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
        errorCode.getHttpStatus().value(),
        errorCode.getMessage()
    );

    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(response);
  }


  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    // 이전에 BoardErrorCode에 추가했던 에러 코드를 사용합니다.
    ErrorCode errorCode = BoardErrorCode.INVALID_BOARD_ID_FORMAT;

    // 기존 ErrorResponse 형식에 맞게 응답을 생성합니다.
    ErrorResponse response = new ErrorResponse(
        errorCode.getHttpStatus().value(),
        errorCode.getMessage()
    );

    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(response);
  }
}
