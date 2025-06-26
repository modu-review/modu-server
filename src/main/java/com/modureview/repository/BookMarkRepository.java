package com.modureview.repository;

import com.modureview.entity.BookMark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
  
  Optional<Boolean> existsByBoardIdAndEmail(Long boardId, String email);

}
