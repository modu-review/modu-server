package com.modureview.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;

@Builder
public record SliceBoardResponse(
    Long board_id,
    String title,
    String author_nickname,
    Category category,
    String preview,
    Integer comments_count,
    @JsonFormat(pattern = "yyyy-MM-dd HH시 mm분", shape = JsonFormat.Shape.STRING, timezone = "Asia/Seoul")
    LocalDateTime created_at,
    Integer bookmarks,
    String image_url
) {



  public static SliceBoardResponse fromEntity(Board board) {


    return SliceBoardResponse.builder()
        .board_id(board.getId())
        .title(board.getTitle())
        .category(board.getCategory())
        .author_nickname(board.getNickname())
        .created_at(board.getCreatedAt())
        .preview(board.getPreview())
        .comments_count(board.getCommentsCount())
        .bookmarks(board.getBookmarksCount())
        .image_url(board.getImages().isEmpty()?  "https://cdn.modu-review.com/no-thumbnail.png": board.getImages().get(0).getFullImageUrl())
        .build();
  }

}
