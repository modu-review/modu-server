package com.modureview.enums.errors;

import com.modureview.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ImageSaveErrorCode implements ErrorCode {
  CAN_NOT_CREATE_PRESIGNED_URL("CAN_NOT_CREATE_PRESIGNED_URL",HttpStatus.INTERNAL_SERVER_ERROR,
      "S3 PresignedURL을 생성할 수 없습니다."),

  CAN_NOT_CREATE_UUID("CAT_NOT_CREATE_UUID",HttpStatus.INTERNAL_SERVER_ERROR,
      "이미지 uuid를 생성할 수 없습니다.");

  private final String title;
  private final HttpStatus httpStatus;
  private final String detail;

  ImageSaveErrorCode(String title ,HttpStatus httpStatus, String detail) {
    this.title = title;
    this.httpStatus = httpStatus;
    this.detail = detail;
  }

  @Override
  public String getTitle() {
    return "";
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  @Override
  public String getDetail() {
    return "";
  }


}
