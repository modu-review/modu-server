package com.modureview.repository;


import java.util.Optional;

import com.modureview.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  Page<Comment> findByBoardId(Long boardId, Pageable pageable);

  Optional<Comment> findByNickname(String nickname);

  Optional<Comment> findByCommentId(Long commentId);
}
