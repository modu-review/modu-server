package com.modureview.controller;

import com.modureview.dto.response.CommentDetailResponse;
import com.modureview.dto.response.CommentListResponse;
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
  public ResponseEntity<CommentListResponse> getCommentList(
      @PathVariable Long reviewId,
      @RequestParam(name = "page", defaultValue = "1") int page  // 페이지 기본값을 1로 바꿔도 좋습니다
  ) {
    Page<Comment> commentPage = commentService.commentList(reviewId, page);
    List<CommentDetailResponse> listComment = commentPage.getContent().stream()
        .map(CommentDetailResponse::fromEntity)
        .toList();
    Integer rawCount = commentService.commentCount(reviewId);
    int commentsCount = rawCount != null ? rawCount : 0;
    int totalPages = commentPage.getTotalPages();
    int currentPage = (totalPages == 0 ? 0 : commentPage.getNumber() + 1);
    CommentListResponse response = CommentListResponse.builder()
        .commentsCount(commentsCount)
        .comments(listComment)
        .currentPage(currentPage)
        .totalPages(totalPages)
        .build();

    return ResponseEntity.ok(response);
  }

}