package com.modureview.repository;

import com.modureview.entity.Bookmarks;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmarks, Long> {
  Optional<Bookmarks> findByUserEmailAndBoardId(String userEmail, Long boardId);

}
