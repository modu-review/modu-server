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
    String author,
    LocalDateTime create_At,
    String content,
    Integer comments_count,
    Integer bookmarks
) {

  public static BoardSearchResponse fromEntity(Board board) {
    return BoardSearchResponse.builder()
        .board_id(board.getId())
        .title(board.getTitle())
        .category(board.getCategory())
        .author(board.getAuthorEmail())
        .create_At(board.getCreatedAt())
        .content(board.getContent())
        .comments_count(board.getCommentsCount())
        .bookmarks(board.getBookmarksCount())
        .build();
  }

}
