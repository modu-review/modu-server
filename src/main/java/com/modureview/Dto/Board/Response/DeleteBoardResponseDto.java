package com.modureview.Dto.Board.Response;



import com.modureview.Entity.Board;
import com.modureview.Entity.Status.BoardStatus;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteBoardResponseDto {
    //==== TEST를 위해서 ====//
    private Long Id;
    //==== TEST를 위해서 ====//

    private String title;
    private String writerName;
    private BoardStatus status;
    private LocalDateTime deleteAt;

    public static DeleteBoardResponseDto fromEntity(Board board){
        return DeleteBoardResponseDto.builder()
                .Id(board.getId())
                .title(board.getTitle())
                .writerName(board.getUser().getEmail())
                .deleteAt(board.getDeleteAt())
                .build();
    }

}
