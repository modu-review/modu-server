package com.modureview.Dto.Board.Response;

import com.modureview.Entity.Board;
import com.modureview.Entity.Status.BoardStatus;
import com.modureview.Entity.Status.Category;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListBoardResponseDto {
    private Long Id;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedDate;
    private String writerName;
    private Category category;
    //TODO : FileResponse 밀기
    //private DetailsFileResponseDto thumnail;

    public static ListBoardResponseDto fromEntity(Board board){
        /*DetailsFileResponseDto thumnail = null;
        for(FileEntity file : galleryBoard.getFileEntities()){
            if(file.getFileType().startsWith("image/")){
                thumnail = DetailsFileResponseDto.fromEntity(file);
                break;
            }
        }
*/
        return ListBoardResponseDto.builder()
                .Id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getView_count())
                .createdAt(board.getCreatedAt())
                .modifiedDate(board.getModifiedAt())
                .writerName(board.getUser().getEmail())
                .category(board.getCategory())
                .build();

    }
}

