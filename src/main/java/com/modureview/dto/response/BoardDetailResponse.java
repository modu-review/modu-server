package com.modureview.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.modureview.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BoardDetailResponse(
    Long board_id,
    String title,
    Category category,
    String author_email,
    String author_id,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime created_at,
    String content
) {


}
