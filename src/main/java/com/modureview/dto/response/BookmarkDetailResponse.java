package com.modureview.dto.response;

import lombok.Builder;

@Builder
public record BookmarkDetailResponse(
    Integer bookmarks,
    boolean hasBookmarked
) {

  public static BookmarkDetailResponse fromEntity(boolean hasBookMark, Integer bookmark_count) {
    return BookmarkDetailResponse.builder()
        .bookmarks(bookmark_count)
        .hasBookmarked(hasBookMark)
        .build();
  }

}
