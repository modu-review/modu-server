package com.modureview.dto.response;

import com.modureview.entity.Board;
import com.modureview.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;

@Builder
public record BoardSearchResponse(
    Long board_id,
    String title,
    String author_id,
    String author_email,
    Category category,
    String preview,
    Integer comments_count,
    LocalDateTime created_at,
    Integer bookmarks,
    String image_url
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
        .image_url(board.getImages().isEmpty()?  "https://d1izijuzr22yly.cloudfront.net/no-thumbnail.png": board.getImages().get(0).getFullImageUrl())
        .build();
  }

}
