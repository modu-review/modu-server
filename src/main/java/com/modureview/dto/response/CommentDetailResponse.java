package com.modureview.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.modureview.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentDetailResponse(
    Long id,
    String author_id,
    String author_email,
    String content,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime created_at
) {

  public static CommentDetailResponse fromEntity(Comment comment) {
    return CommentDetailResponse.builder()
        .id(comment.getId())
        .author_id(comment.getUserEmail().split("@")[0])
        .author_email(comment.getUserEmail())
        .content(comment.getContent())
        .created_at(comment.getCreatedAt())
        .build();
  }

}