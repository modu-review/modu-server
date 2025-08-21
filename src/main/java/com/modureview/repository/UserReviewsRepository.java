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
    @Query("SELECT b FROM Board b WHERE b.nickname = :nickname "
        + "AND (b.createdAt < :createdAt OR "
        + " (b.createdAt = :createdAt AND b.id < :boardId)) "
        + "ORDER BY b.createdAt DESC , b.id DESC")
    Slice<Board> findByNicknameByCreatedAt(
        @Param("nickname") String nickname,
        @Param("createdAt") LocalDateTime createdAt,
        @Param("boardId") Long boardId,
        Pageable pageable
    );

    @Query("SELECT b FROM Board b WHERE b.nickname = :nickname "
        + "AND(b.bookmarksCount < :bookmarksCount OR "
        + "(b.bookmarksCount = :bookmarksCount AND b.id < :boardId)) "
        + "ORDER BY b.bookmarksCount DESC , b.id DESC")
    Slice<Board> findByNicknameByBookmarksCount(
        @Param("nickname") String nickname,
        @Param("bookmarksCount") Integer bookmarksCount,
        @Param("boardId") Long boardId,
        Pageable pageable
    );

    @Query("SELECT b FROM Board b WHERE b.nickname = :nickname "
        + "AND(b.commentsCount < :commentsCount OR "
        + "(b.commentsCount = :commentsCount AND b.id < :boardId)) "
        + "ORDER BY b.commentsCount DESC , b.id DESC")
    Slice<Board> findByNicknameByCommentsCount(
        @Param("nickname") String nickname,
        @Param("commentsCount") Integer commentsCount,
        @Param("boardId") Long boardId,
        Pageable pageable
    );

    long countByNickname(String nickname);

    @Query("SELECT b FROM Board b WHERE b.nickname = :nickname ORDER by b.createdAt DESC , b.id DESC")
    Slice<Board> findByNicknameOrderByCreatedAtFirst(
        @Param("nickname") String nickname,
        Pageable pageable
    );
    @Query("SELECT b FROM Board b WHERE b.nickname = :nickname ORDER BY b.bookmarksCount DESC , b.id DESC")
    Slice<Board> findByNicknameOrderByBookmarksCountFirst(
        @Param("nickname") String nickname,
        Pageable pageable
    );

    @Query("SELECT b FROM Board b WHERE b.nickname = :nickname ORDER BY b.commentsCount DESC, b.id DESC")
    Slice<Board> findByNicknameOrderByCommentsCountFirst(
        @Param("nickname") String nickname,
        Pageable pageable
    );





}
