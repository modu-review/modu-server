package com.modureview.controller;

import com.modureview.dto.response.CommentDetailResponse;
import com.modureview.dto.response.CustomPageResponse;
import com.modureview.entity.Comment;
import com.modureview.service.CommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/reviews/{reviewId}/comments")
  public ResponseEntity<CustomPageResponse<CommentDetailResponse>> getCommentList(
      @PathVariable Long reviewId,
      @RequestParam(name = "page", defaultValue = "0") int page
  ) {
    Page<Comment> commentPage = commentService.commentList(reviewId, page);
    List<CommentDetailResponse> listComment = commentPage.getContent().stream()
        .map(CommentDetailResponse::fromEntity)
        .toList();
    CustomPageResponse<CommentDetailResponse> commentPageResponse = new CustomPageResponse<>(
        listComment,
        commentPage.getNumber() + 1,
        commentPage.getTotalPages()
    );
    return ResponseEntity.ok(commentPageResponse);
  }

}
