package com.modureview.repository;

import com.modureview.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardSearchRepository extends JpaRepository<Board, Long> {

  @Query(
      value = """
            SELECT b
            FROM Board b
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR b.content      LIKE CONCAT('%', :keyword, '%')
               OR LOWER(b.authorEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))
          """
      /*,
      countQuery = """
            SELECT COUNT(b)
            FROM Board b
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR b.content      LIKE CONCAT('%', :keyword, '%')
               OR LOWER(b.authorEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))
          """*/
  )
  Page<Board> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
