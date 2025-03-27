package com.modureview.Dto.Board.Response;

import com.modureview.Entity.Board;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WriteBoardResponseDto {

    private Long Id;

    private String title;

    private String content;

    private String writerName;

    private LocalDateTime createdDate;

    public static WriteBoardResponseDto fromEntity(Board board){
        return WriteBoardResponseDto.builder()
                .Id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writerName(board.getUser().getEmail())
                .createdDate(board.getCreatedAt())
                .build();

    }
}
