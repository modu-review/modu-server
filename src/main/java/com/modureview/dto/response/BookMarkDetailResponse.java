package com.modureview.dto.response;

import lombok.Builder;

@Builder
public record BookMarkDetailResponse(
    Integer bookmarks,
    boolean hasBookmarked
) {

  public static BookMarkDetailResponse fromEntity(boolean hasBookMark, Integer bookmark_count) {
    return BookMarkDetailResponse.builder()
        .bookmarks(bookmark_count)
        .hasBookmarked(hasBookMark)
        .build();
  }

}
