package com.modureview.repository;

import com.modureview.entity.BookMark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<BookMark, Long> {


  Optional<BookMark> findByNicknameAndBoardId(String nickname, Long boardId);

  Boolean existsByNicknameAndBoardId(String nickname, Long boardId);

}
