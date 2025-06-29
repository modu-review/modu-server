package com.modureview.dto.response;

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
    LocalDateTime create_At,
    String content,
    Integer comment_count,
    Integer bookmarks
) {


}
