package com.modureview.repository;

import com.modureview.entity.BookMark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyPageBookMarkRepository extends JpaRepository<BookMark, Long> {

  @Query(
      value = "SELECT bm.boardId "
          + "FROM BookMark bm "
          + "WHERE bm.email = :email"
  )
  Page<Long> findBookMarksByEmail(String email, Pageable pageable);


}
