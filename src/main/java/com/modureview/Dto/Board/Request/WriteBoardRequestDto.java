package com.modureview.Dto.Board.Request;


import com.modureview.Entity.Board;
import com.modureview.Entity.Status.BoardStatus;
import com.modureview.Entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WriteBoardRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private User user;
    public static Board toEntity(WriteBoardRequestDto dto,User user){
        return Board.builder()
                .title(dto.title)
                .content(dto.content)
                .user(user)
                .build();
    }
}
