package com.modureview.repository;

import com.modureview.entity.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {
  @Query(
      value = """
            WITH RankedBoards AS (
                SELECT
                    b.*,
                    ROW_NUMBER() OVER (
                        PARTITION BY b.category
                        ORDER BY (b.bookmarks_count * 4 + b.view_count + b.comments_count * 2) DESC
                    ) AS rn
                FROM
                    board b
            )
            SELECT *
            FROM RankedBoards
            WHERE rn <= 6
        """,
      nativeQuery = true)
  List<Board> findTop6BoardsPerCategory();
}
