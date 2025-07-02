package com.modureview.dto.request;

public record CommentDeleteRequest(String userEmail, Long boardId, Long commentId) {

}
