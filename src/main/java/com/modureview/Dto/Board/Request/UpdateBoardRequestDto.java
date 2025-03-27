package com.modureview.Dto.Board.Request;


import com.modureview.Entity.Board;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBoardRequestDto {
    private String title;
    //-> 아무것도 안넣어도 되는이유 : null이 들어가도 되기 때문에 왜?
    // :title(this.title != null ? this.title: existingGalleryBoard.getTitle()) 이렇게 삼항연산자를 사용했기 때문에
    // title에 null이 들어가면 존재하는 GalleryBoardEntity의 title을 그대로 가져온다.
    private String content;

    /*
        this.title과 this.content가 null이 아닌 경우에는 Dto에서 전달된 값을 사용합니다.
        null인 경우에는 기존 엔티티(existingUser)에서 가져온 값을 그대로 유지합니다.
    */
    public Board toEntity(Board existingBoard){
        return Board.builder()
                .Id(existingBoard.getId())
                .title(this.title != null ? this.title : existingBoard.getTitle())
                .content(this.content != null ? this.content : existingBoard.getContent())
                .view_count(existingBoard.getView_count())
                .user(existingBoard.getUser())
                .status(existingBoard.getStatus())
                .build();

    }

}
