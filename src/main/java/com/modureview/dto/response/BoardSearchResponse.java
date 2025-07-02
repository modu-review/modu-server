package com.modureview.dto.response;


import com.modureview.entity.Board;
import com.modureview.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BoardSearchResponse(
    Long board_id,
    String title,
    Category category,
    String author_id,
    String author_email,
    LocalDateTime created_at,
    String preview,
    Integer comments_count,
    Integer bookmarks
) {

  public static BoardSearchResponse fromEntity(Board board) {
    return BoardSearchResponse.builder()
        .board_id(board.getId())
        .title(board.getTitle())
        .category(board.getCategory())
        .author_id(board.getAuthorEmail().split("@")[0])
        .author_email(board.getAuthorEmail())
        .created_at(board.getCreatedAt())
        .preview(board.getPreview())
        .comments_count(board.getCommentsCount())
        .bookmarks(board.getBookmarksCount())
        .build();
  }

}
