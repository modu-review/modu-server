package com.modureview.Dto.Board.Request;


import com.modureview.Entity.Board;
import com.modureview.Entity.Status.BoardStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteBoardRequestDto {
    public Board toEntity(Board existingBoard){
        return existingBoard.builder()
                .Id(existingBoard.getId())
                .title(existingBoard.getTitle())
                .content(existingBoard.getContent())
                .view_count(existingBoard.getView_count())
                .category(existingBoard.getCategory())
                .user(existingBoard.getUser())
                .build();
    }

}
