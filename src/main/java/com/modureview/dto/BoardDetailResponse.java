package com.modureview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.modureview.entity.Board;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDetailResponse {



  private String title;

  private String content;

  private String user;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
  private LocalDateTime createdAt;


  private String category;

  private Long viewCount;

  private Integer stars;



  public static BoardDetailResponse fromEntity(Board board,String email){
    return BoardDetailResponse.builder()
        .title(board.getTitle())
        .content(board.getContent())
        .user(email)
        .createdAt(board.getCreatedAt())
        .viewCount(board.getViewCount())
        .category(board.getCategory())
        .stars(board.getStars())
        .createdAt(board.getCreatedAt())
        .build();
  }

}
