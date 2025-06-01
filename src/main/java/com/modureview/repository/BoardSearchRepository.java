package com.modureview.repository;

import com.modureview.entity.Board;
import com.modureview.entity.Category;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardSearchRepository extends JpaRepository<Board, Long> {

  @Query("SELECT b FROM Board b WHERE " +
      "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      " b.content      LIKE CONCAT('%', :keyword, '%')        OR " +
      "LOWER(b.authorEmail) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<Board> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

  @Query("SELECT b FROM Board b WHERE b.category = :category"
      + " ORDER BY b.createdAt DESC,b.id DESC")
  Slice<Board> findByCategoryOrderByCreatedAtFirst(@Param("category") Category category,
      Pageable pageable);

  @Query("SELECT b FROM Board b WHERE b.category = :category " +
      "AND (b.createdAt < :createAt OR" +
      " (b.createdAt = :createAt AND b.id < :boardId)) " +
      "ORDER BY b.createdAt DESC, b.id DESC")
  Slice<Board> findByCategoryOrderByCreatedAt(
      @Param("category") Category category,
      @Param("createAt") LocalDateTime createAt,
      @Param("boardId") Long boardId,
      Pageable pageable
  );

  @Query("SELECT b FROM Board b WHERE b.category = :category ORDER BY b.commentsCount DESC, b.id DESC")
  Slice<Board> findByCategoryOrderByCommentsCountFirst(
      @Param("category") Category category,
      Pageable pageable
  );

  @Query("SELECT b FROM Board b WHERE b.category = :category " +
      "AND (b.commentsCount < :commentCount OR" +
      " (b.commentsCount = :commentCount AND b.id < :boardId)) " +
      "ORDER BY b.commentsCount DESC, b.id DESC")
  Slice<Board> findByCategoryOrderByCommentsCount(
      @Param("category") Category category,
      @Param("commentCount") Integer commentCount,
      @Param("boardId") Long boardId,
      Pageable pageable
  );

  @Query("SELECT b FROM Board b WHERE b.category = :category ORDER BY b.bookmarksCount DESC, b.id DESC")
  Slice<Board> findByCategoryOrderByBookmarksCountFirst(
      @Param("category") Category category,
      Pageable pageable
  );

  @Query("SELECT b FROM Board b WHERE b.category = :category " +
      "AND (b.bookmarksCount < :bookmarksCount OR" +
      " (b.bookmarksCount = :bookmarksCount AND b.id < :boardId)) " +
      "ORDER BY b.bookmarksCount DESC, b.id DESC")
  Slice<Board> findByCategoryOrderByBookmarksCount(
      @Param("category") Category category,
      @Param("bookmarksCount") Integer bookmarksCount,
      @Param("boardId") Long boardId,
      Pageable pageable
  );

  @Query(
      "SELECT b " +
          "FROM Board b " +
          "ORDER BY b.createdAt DESC, b.id DESC"
  )
  Slice<Board> findAllOrderByCreatedAtFirst(Pageable pageable);


  @Query(
      "SELECT b " +
          "FROM Board b " +
          "WHERE (b.createdAt < :createAt OR (b.createdAt = :createAt AND b.id < :boardId)) " +
          "ORDER BY b.createdAt DESC, b.id DESC"
  )
  Slice<Board> findAllOrderByCreatedAt(
      @Param("createAt") LocalDateTime createAt,
      @Param("boardId") Long boardId,
      Pageable pageable
  );


  @Query(
      "SELECT b " +
          "FROM Board b " +
          "ORDER BY b.commentsCount DESC, b.id DESC"
  )
  Slice<Board> findAllOrderByCommentsCountFirst(Pageable pageable);


  @Query(
      "SELECT b " +
          "FROM Board b " +
          "WHERE (b.commentsCount < :commentCount OR (b.commentsCount = :commentCount AND b.id < :boardId)) "
          +
          "ORDER BY b.commentsCount DESC, b.id DESC"
  )
  Slice<Board> findAllOrderByCommentsCount(
      @Param("commentCount") Integer commentCount,
      @Param("boardId") Long boardId,
      Pageable pageable
  );


  @Query(
      "SELECT b " +
          "FROM Board b " +
          "ORDER BY b.bookmarksCount DESC, b.id DESC"
  )
  Slice<Board> findAllOrderByBookmarksCountFirst(Pageable pageable);


  @Query(
      "SELECT b " +
          "FROM Board b " +
          "WHERE (b.bookmarksCount < :bookmarksCount OR (b.bookmarksCount = :bookmarksCount AND b.id < :boardId)) "
          +
          "ORDER BY b.bookmarksCount DESC, b.id DESC"
  )
  Slice<Board> findAllOrderByBookmarksCount(
      @Param("bookmarksCount") Integer bookmarksCount,
      @Param("boardId") Long boardId,
      Pageable pageable
  );


}
