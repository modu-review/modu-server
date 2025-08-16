package com.modureview.repository;

import com.modureview.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MyPageRepository extends JpaRepository<Board, Long> {

  @Query(value = "SELECT b FROM Board b WHERE b.nickname = :nickname")
  Page<Board> findBoardByNickname(@Param("nickname") String nickname, Pageable pageable);



}
