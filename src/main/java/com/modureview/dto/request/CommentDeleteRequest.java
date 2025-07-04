package com.modureview.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentDeleteRequest(@JsonProperty("user_email") String userEmail, @JsonProperty("board_id") Long boardId,
                                  @JsonProperty("comment_id") Long commentId) {

}
