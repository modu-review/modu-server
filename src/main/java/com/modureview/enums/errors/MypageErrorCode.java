package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MypageErrorCode implements ErrorCode {
  UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE,
      "저장할 수 없는 이미지 형식입니다."),
  FILE_SIZE_EXCEEDED("FILE_SIZE_EXCEEDE", HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기는 5MB를 초과할 수 없습니다."),
  FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");

  private final String title;
  private final HttpStatus httpStatus;
  private final String detail;

  MypageErrorCode(String title, HttpStatus httpStatus, String detail) {
    this.title = title;
    this.httpStatus = httpStatus;
    this.detail = detail;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  @Override
  public String getDetail() {
    return detail;
  }
}
