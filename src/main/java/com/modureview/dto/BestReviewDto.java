package com.modureview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.modureview.entity.Board;
import com.modureview.entity.BoardImage;
import java.util.List;
import lombok.Builder;

public record BestReviewDto(
    @JsonProperty("boardId")
    Long board_id,

    @JsonProperty("title")
    String title,

    @JsonProperty("author")
    String author,

    @JsonProperty("category")
    String category,

    @JsonProperty("thumbnail")
    String thumbnail,

    @JsonProperty("contents")
    String contents,

    @JsonProperty("commentsCount")
    int comments_count,

    @JsonProperty("bookmarks")
    int bookmarks,

    @JsonProperty("image_url")
    List<String> image_url,

    @JsonProperty("view_count")
    int viewCount
) {

  @Builder
  public BestReviewDto {}
  public static BestReviewDto from(Board board) {

    List<String> imageUrls = board.getImages().stream()
        .map(BoardImage::getFullImageUrl)
        .toList();

    return new BestReviewDto(
        board.getId(),
        board.getTitle(),
        board.getUser().getEmail(),
        board.getCategory().name(),
        board.getThumbnail(),
        board.getContent(),
        board.getCommentsCount(),
        board.getBookmarksCount(),
        imageUrls,
        board.getViewCount()
    );
  }

  public int getHotScore() {
    return (this.bookmarks * 4) + this.viewCount + (this.comments_count * 2);
  }
}
