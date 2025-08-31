package com.modureview.service;

import com.modureview.dto.request.CommentSaveRequest;
import com.modureview.entity.Comment;
import com.modureview.entity.NotificationType;
import com.modureview.dto.response.NotificationPushResponse;
import com.modureview.enums.errors.CommentErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.CommentRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {

  private final CommentRepository commentRepository;
  private final BoardRepository boardRepository;
  private final NotificationService notificationService;
  private final NotificationSseService notificationSseService;

  public void saveComment(Long boardId,String nickname, Long userId, CommentSaveRequest commentSaveRequest) {
    log.info("Comment save start: boardId={}, userId={}, nickname={}", boardId, userId, nickname);
    Comment comment = Comment.builder()
        .boardId(boardId)
        .nickname(nickname)
        .userId(userId)
        .content(commentSaveRequest.content())
        .build();

    commentRepository.save(comment);
    log.info("Comment saved: id={}, boardId={}", comment.getId(), boardId);

    boardRepository.findById(boardId).ifPresent(board -> {
      log.debug("Updating board commentsCount: boardId={}, before={}, after={}",
          boardId, board.getCommentsCount(), board.getCommentsCount() + 1);
      board.setCommentsCount(board.getCommentsCount() + 1);
      boardRepository.save(board);

      Long receiverUserId = board.getUser() != null ? board.getUser().getId() : null;
      if (receiverUserId != null && !receiverUserId.equals(userId)) {
        log.debug("Preparing notification: receiverUserId={}, senderUserId={}, boardId={}",
            receiverUserId, userId, boardId);
        NotificationPushResponse payload = notificationService.sendNotification(
            receiverUserId,
            userId,
            boardId,
            NotificationType.comment
        );
        log.info("Notification saved: id={}, type={}", payload.id(), payload.type());
        boolean delivered = notificationSseService.sendNotification(receiverUserId, payload);
        log.info("SSE notification dispatched: userId={}, notificationId={}, delivered={}",
            receiverUserId, payload.id(), delivered);
      } else {
        log.debug("Skip notification: receiver is null or same as sender. receiverUserId={}, senderUserId={}",
            receiverUserId, userId);
      }
    });

  }

  public void deleteComment(Long commentId, Long boardId, String nickname) {
    Comment targetComment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(
        CommentErrorCode.COMMENT_NOT_FOUND));
    if(!targetComment.getNickname().equals(nickname)) {
      throw new CustomException(CommentErrorCode.COMMENT_USER_NOT_EQUAL,nickname);
    }
    commentRepository.deleteById(commentId);
    boardRepository.findById(boardId).ifPresent(board -> {
      board.setCommentsCount(board.getCommentsCount() - 1);
      boardRepository.save(board);
    });
  }

  @Transactional(readOnly = true)
  public Page<Comment> commentList(Long boardId, int Page) {
    Pageable pageable = PageRequest.of(Page - 1, 8, Sort.by(Direction.ASC, "createdAt"));

    return commentRepository.findByBoardId(boardId, pageable);
  }

  @Transactional(readOnly = true)
  public Integer commentCount(Long boardId) {
    return boardRepository.findCommentsCountById(boardId);
  }
}
