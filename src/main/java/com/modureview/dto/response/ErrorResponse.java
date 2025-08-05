package com.modureview.dto.response;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
    String title,
    int status,
    String detail) {

}
