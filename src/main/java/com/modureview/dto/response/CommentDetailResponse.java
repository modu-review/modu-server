package com.modureview.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.modureview.entity.Comment;
import com.modureview.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentDetailResponse(
    Long id,
    String profile_image,
    String author_nickname,
    String content,
    @JsonFormat(pattern = "yyyy-MM-dd HH시 mm분", shape = JsonFormat.Shape.STRING, timezone = "Asia/Seoul")
    LocalDateTime created_at
) {

  public static CommentDetailResponse fromEntity(Comment comment, User user) {
      return CommentDetailResponse.builder()
          .id(comment.getId())
          .author_nickname(user.getNickname())
          .content(comment.getContent())
          .created_at(comment.getCreatedAt())
          .profile_image(user.getProfile())
          .build();
  }
}
