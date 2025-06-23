package com.modureview.repository;

import com.modureview.entity.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

  @Query(value =
      "WITH RankedBoards AS (" +
          "    SELECT b.*, " +
          "           ROW_NUMBER() OVER (PARTITION BY b.category ORDER BY (b.bookmarks_count * 4 + b.view_count + b.comments_count * 2) DESC) as rn " +
          "    FROM board b " +
          "    LEFT JOIN user u ON b.user_id = u.id" +
          ") " +
          "SELECT * FROM RankedBoards WHERE rn <= 6",
      nativeQuery = true)
  List<Long> findTop6BoardsPerCategory();

  @Query(value =
      "WITH RankedBoards AS (" +
          "    SELECT b.*, " +
          "           ROW_NUMBER() OVER (ORDER BY (b.bookmarks_count * 4 + b.view_count + b.comments_count * 2) DESC) as rn " +
          "    FROM board b " +
          ") " +
          "SELECT rb.* FROM RankedBoards rb WHERE rb.rn <= 6",
      nativeQuery = true)
  List<Long> findallCategory();

  @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.id IN :ids")
  List<Board> findByIdsWithUser(@Param("ids") List<Long> ids);

}
