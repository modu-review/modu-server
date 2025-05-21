package com.modureview.dto.response;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
    int code,
    String message) {

}
