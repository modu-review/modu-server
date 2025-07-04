package com.modureview.controller;

import com.modureview.dto.request.CommentDeleteRequest;
import com.modureview.dto.request.CommentSaveRequest;
import com.modureview.dto.response.CommentDetailResponse;
import com.modureview.dto.response.CommentListResponse;
import com.modureview.entity.Comment;
import com.modureview.service.CommentService;
import com.modureview.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class CommentController {

  private final UserService userService;
  private final CommentService commentService;

  @PostMapping("/reviews/{reviewId}/comments")
  public ResponseEntity<?> addComment(@PathVariable Long reviewId,
      @RequestBody CommentSaveRequest commentSaveRequest) {
    Long userId = userService.findUserId(commentSaveRequest.userEmail());
    commentService.saveComment(reviewId, userId, commentSaveRequest);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("/reviews/{reviewId}/comments")
  public ResponseEntity<?> deleteComment(@PathVariable Long reviewId,
     @RequestBody CommentDeleteRequest commentDeleteRequest) {
    log.info("댓글삭제");
    log.info("deleteComment == {}", commentDeleteRequest);
    log.info("reviewId == {}", reviewId);

    commentService.deleteComment(commentDeleteRequest.commentId());
    return new ResponseEntity<>(HttpStatus.OK);
  }

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
