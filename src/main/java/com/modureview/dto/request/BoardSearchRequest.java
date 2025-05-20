package com.modureview.dto.request;

import lombok.Builder;

@Builder
public record BoardSearchRequest(
    String keyword,
    int page,
    String sort
) {

}
