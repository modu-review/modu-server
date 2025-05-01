package com.modureview.repository;

import com.modureview.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SearchBoardRepository extends JpaRepository<Board,Long> {
  @Query("""
      SELECT DISTINCT b
      FROM Board b
      WHERE b.title   LIKE %:keyword%
      OR b.content LIKE %:keyword%
      OR b.email  LIKE %:keyword%
      """)
  Page<Board> searchBoardByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
