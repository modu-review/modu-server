package com.modureview.controller;

import com.modureview.dto.request.CommentSaveRequest;
import com.modureview.service.BoardService;
import com.modureview.service.CommentService;
import com.modureview.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CommentController {

  private final UserService userService;
  private final CommentService commentService;

  @PostMapping("/reviews/{reviewId}/comments")
  public ResponseEntity<?> addComment(@PathVariable Long reviewId, CommentSaveRequest commentSaveRequest) {
    Long userId = userService.findUserId(commentSaveRequest.userEmail());
    commentService.saveComment(reviewId, userId, commentSaveRequest);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

}
