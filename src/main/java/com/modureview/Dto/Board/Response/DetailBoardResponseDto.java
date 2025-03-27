/*
package com.modureview.Dto.Response;


import com.modureview.Entity.Board;
import com.modureview.Entity.Status.BoardStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailBoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private String writerName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedDate;
    //----테스트용----//
    private BoardStatus status;
    //----테스트용----//
    //comments

    //files추가

    //TODO : FIlE밀기
    private List<DetailsFileResponseDto> files;


    public static DetailBoardResponseDto fromEntity(Board board){
        return DetailBoardResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getView_count())
                .writerName(board.getUser().getEmail())
                .createdAt(board.getCreatedAt())
                .modifiedDate(board.getModifiedAt())
                .files(galleryBoard.getFileEntities().stream()
                        .map(DetailsFileResponseDto:: fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
*/
