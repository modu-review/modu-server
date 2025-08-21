package com.modureview.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.modureview.entity.Category;

public record CommentSaveRequest(Category category,
                                 String content) {
}
