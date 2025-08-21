package com.modureview.service;

import com.modureview.dto.request.CommentSaveRequest;
import com.modureview.entity.Comment;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final BoardRepository boardRepository;

  public void saveComment(Long boardId,String nickname, Long userId, CommentSaveRequest commentSaveRequest) {
    Comment comment = Comment.builder()
        .boardId(boardId)
        .nickname(nickname)
        .userId(userId)
        .content(commentSaveRequest.content())
        .build();

    commentRepository.save(comment);

    boardRepository.findById(boardId).ifPresent(board -> {
      board.setCommentsCount(board.getCommentsCount() + 1);
      boardRepository.save(board);
    });

  }

  public void deleteComment(Long commentId, Long boardId) {
    commentRepository.deleteById(commentId);
    boardRepository.findById(boardId).ifPresent(board -> {
      board.setCommentsCount(board.getCommentsCount() - 1);
      boardRepository.save(board);
    });
  }

  public Page<Comment> commentList(Long boardId, int Page) {
    Pageable pageable = PageRequest.of(Page - 1, 8, Sort.by(Direction.ASC, "createdAt"));

    return commentRepository.findByBoardId(boardId, pageable);
  }

  public Integer commentCount(Long boardId) {
    return boardRepository.findCommentsCountById(boardId);
  }
}
