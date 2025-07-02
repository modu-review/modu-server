package com.modureview.dto.response;

import com.modureview.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentDetailResponse(
    Long id,
    String author,
    String content,
    LocalDateTime createdAt
) {

  public static CommentDetailResponse fromEntity(Comment comment) {
    return CommentDetailResponse.builder()
        .id(comment.getId())
        .author(comment.getAuthor())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .build();
  }

}
