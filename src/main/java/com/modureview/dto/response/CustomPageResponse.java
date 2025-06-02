package com.modureview.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public record CustomPageResponse<T>(
    List<T> results,
    @JsonProperty("current_page")
    int currentPage,
    @JsonProperty("total_pages")
    int totalPages
) {


}