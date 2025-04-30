package com.modureview.dto.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.modureview.entity.Board;
import java.time.LocalDateTime;

public record BoardDetailResponse(
    String title,
    String content,
    String user,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    LocalDateTime createdAt,
    String category,
    Long viewCount,
    Integer stars
) {
  public static BoardDetailResponse fromEntity(Board board, String email) {
    return new BoardDetailResponse(
        board.getTitle(),
        board.getContent(),
        email,
        board.getCreatedAt(),
        board.getCategory(),
        board.getViewCount(),
        board.getStars()
    );
  }
}
