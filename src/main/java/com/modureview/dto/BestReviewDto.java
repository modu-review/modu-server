package com.modureview.dto;

import com.modureview.entity.Board;
import lombok.Builder;

public record BestReviewDto(
    Long boardId,
    String title,
    String author,
    String category,
    String contents,
    int commentsCount,
    int bookmarks,
    String imageUrl
) {

  @Builder
  public BestReviewDto {}

  public static BestReviewDto from(Board board) {
    return BestReviewDto.builder()
        .boardId(board.getId())
        .title(board.getTitle())
        .author(board.getUser().getEmail())
        .category(board.getCategory().name())
        .contents(board.getContent())
        .commentsCount(board.getCommentsCount())
        .bookmarks(board.getBookmarksCount())
        .imageUrl(board.getThumbnail())
        .build();
  }
}
