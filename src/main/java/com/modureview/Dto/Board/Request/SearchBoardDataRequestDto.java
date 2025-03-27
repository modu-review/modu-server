package com.modureview.Dto.Board.Request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBoardDataRequestDto {
    private String title;
    private String content;
    private String writerName;

    public static SearchBoardDataRequestDto createSearchData(String title,String content, String writerName){
        return SearchBoardDataRequestDto.builder()
                .title(title)
                .content(content)
                .writerName(writerName)
                .build();
    }
}
