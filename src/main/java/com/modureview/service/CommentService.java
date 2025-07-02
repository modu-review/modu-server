package com.modureview.service;

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
@RequiredArgsConstructor
@Transactional
public class CommentService {

  private final CommentRepository commentRepository;
  private final BoardRepository boardRepository;

  public Page<Comment> commentList(Long boardId, int Page) {
    Pageable pageable = PageRequest.of(Page - 1, 12, Sort.by(Direction.DESC, "createdAt"));

    return commentRepository.findByBoardId(boardId, pageable);
  }

  public Integer commentCount(Long boardId) {
    return boardRepository.findCommentsCountById(boardId);
  }
}
