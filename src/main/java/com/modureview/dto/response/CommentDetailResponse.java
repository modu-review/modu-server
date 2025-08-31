package com.modureview.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.modureview.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentDetailResponse(
    Long id,
    String author_nickname,
    String content,
    String profile_image,
    @JsonFormat(pattern = "yyyy-MM-dd HH시 mm분", shape = JsonFormat.Shape.STRING, timezone = "Asia/Seoul")
    LocalDateTime created_at
) {

  public static CommentDetailResponse fromEntity(Comment comment) {
      return CommentDetailResponse.builder()
          .id(comment.getId())
          .author_nickname(comment.getNickname())
          .content(comment.getContent())
          .created_at(comment.getCreatedAt())
          .build();
  }
  public static CommentDetailResponse of(Comment comment, String profileImage) {
    return new CommentDetailResponse(
        comment.getId(),
        comment.getNickname(),
        comment.getContent(),
        profileImage,
        comment.getCreatedAt()
    );
  }
}
