package com.modureview.dto.response;

import com.modureview.entity.Board;
import com.modureview.entity.BoardImage;
import java.util.List;
import lombok.Builder;

public record BestReviewResponse(

    Long board_id,
    String title,
    String author,
    String category,
    String preview,
    int comments_count,
    int bookmarks,
    String thumbnail
) {

  @Builder
  public BestReviewResponse {}
  public static BestReviewResponse from(Board board) {

    List<String> imageUrls = board.getImages().stream()
        .map(BoardImage::getFullImageUrl)
        .toList();


    return new BestReviewResponse(
        board.getId(),
        board.getTitle(),
        board.getUser().getEmail(),
        board.getCategory().name(),
        board.getPreview(),
        board.getCommentsCount(),
        board.getBookmarksCount(),
        board.getThumbnail()
    );
  }
}
