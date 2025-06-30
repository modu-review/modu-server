package com.modureview.dto.request;

import com.modureview.entity.Category;

public record CommentSaveRequest(String userEmail, Category category, String content) {
}
