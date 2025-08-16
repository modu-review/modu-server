package com.modureview.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.modureview.entity.Category;

public record CommentSaveRequest(@JsonProperty("userNickname") String nickname, Category category,
                                 String content) {
}
