package com.modureview.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentListResponse {

  @JsonProperty("comments_count")
  private final int commentsCount;

  @JsonProperty("comments")
  private final List<CommentDetailResponse> comments;

  @JsonProperty("current_page")
  private final int currentPage;

  @JsonProperty("total_pages")
  private final int totalPages;

  @Builder
  public CommentListResponse(int commentsCount,
      List<CommentDetailResponse> comments,
      int currentPage,
      int totalPages) {
    this.commentsCount = commentsCount;
    this.comments = comments;
    this.currentPage = currentPage;
    this.totalPages = totalPages;
  }
}