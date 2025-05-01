package com.modureview.dto.Response;


import com.modureview.entity.Board;

public record SearchResponseDto(
    String title ,
    String content ,
    String email,
    Long viewCount ,
    Integer stars) {

  public static SearchResponseDto fromEntity(Board board){
    return new SearchResponseDto(
        board.getTitle(),
        board.getContent(),
        board.getEmail(),
        board.getViewCount(),
        board.getStars()
    );
  }

}
