package com.modureview.repository;

import com.modureview.entity.BookMark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {

  @Query("SELECT b.isBookmarked FROM BookMark b WHERE b.boardId = :boardId AND b.email = :email")
  Optional<Boolean> findIsBookmarkedByBoardIdAndEmail(@Param("boardId") Long boardId,
      @Param("email") String email);

}
