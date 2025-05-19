package com.modureview.dto;

import com.modureview.entity.Category;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BoardDetailResponse(
    Long board_id,
    String title,
    Category category,
    String author,
    LocalDateTime create_At,
    String content,
    Integer comment_count,
    Integer bookmarks
) {


}
