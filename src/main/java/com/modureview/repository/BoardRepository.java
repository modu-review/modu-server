package com.modureview.repository;

import com.modureview.entity.Board;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
  Optional<Board> findBoardById(int id);
}
