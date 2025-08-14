package com.modureview.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.modureview.entity.Board;

@Repository
public interface UserReviewsRepository extends JpaRepository<Board, Long> {
	@Query("SELECT b FROM Board b WHERE b.authorEmail = :authorEmail "
		+ "AND (b.createdAt < :createdAt OR "
		+ " (b.createdAt = :createdAt AND b.id < :boardId)) "
		+ "ORDER BY b.createdAt DESC , b.id DESC")
	Slice<Board> findByAuthorEmailByCreatedAt(
		@Param("authorEmail") String authorEmail,
		@Param("createdAt") LocalDateTime createdAt,
		@Param("boardId") Long boardId,
		Pageable pageable
	);

	@Query("SELECT b FROM Board b WHERE b.authorEmail = :authorEmail "
		+ "AND(b.bookmarksCount < :bookmarksCount OR "
		+ "(b.bookmarksCount = :bookmarksCount AND b.id < :boardId)) "
		+ "ORDER BY b.bookmarksCount DESC , b.id DESC")
	Slice<Board> findByAuthorEmailByBookmarksCount(
		@Param("authorEmail") String authorEmail,
		@Param("bookmarksCount") Integer bookmarksCount,
		@Param("boardId") Long boardId,
		Pageable pageable
	);

	@Query("SELECT b FROM Board b WHERE b.authorEmail = :authorEmail "
		+ "AND(b.commentsCount < :commentsCount OR "
		+ "(b.commentsCount = :commentsCount AND b.id < :boardId)) "
		+ "ORDER BY b.commentsCount DESC , b.id DESC")
	Slice<Board> findByAuthorEmailByCommentsCount(
		@Param("authorEmail") String authorEmail,
		@Param("commentsCount") Integer commentsCount,
		@Param("boardId") Long boardId,
		Pageable pageable
	);

	Board findTopByAuthorEmailOrderByCreatedAtDesc(String authorEmail);

	Board findTopByAuthorEmailOrderByBookmarksCountDesc(String authorEmail);

	Board findTopByAuthorEmailOrderByCommentsCountDesc(String authorEmail);

	long countByAuthorEmail(String authorEmail);

}
