package com.modureview.service;

import com.modureview.dto.request.CommentSaveRequest;
import com.modureview.entity.Comment;
import com.modureview.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  public void saveComment(Long boardId, Long userId, CommentSaveRequest commentSaveRequest) {
    Comment comment = Comment.builder()
        .boardId(boardId)
        .userEmail(commentSaveRequest.userEmail())
        .userId(userId)
        .content(commentSaveRequest.content())
        .build();

    commentRepository.save(comment);

  }
}
