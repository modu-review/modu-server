package com.modureview.Dto.Board.Request;

import com.modureview.Entity.Status.Category;
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
    private Category category;

    public static SearchBoardDataRequestDto createSearchData(String title,String content, String writerName,
        Category category) {
        return SearchBoardDataRequestDto.builder()
                .title(title)
                .content(content)
                .writerName(writerName)
                .category(category)
                .build();
    }
}
