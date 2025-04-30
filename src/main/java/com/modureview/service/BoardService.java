package com.modureview.service;


import com.modureview.dto.Response.BoardDetailResponse;
import com.modureview.entity.Board;
import com.modureview.entity.User;
import com.modureview.exception.board.BoardIdNotFoundException;
import com.modureview.exception.user.UserEmailNotFound;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

  private final BoardRepository boardRepository;

  private final UserRepository userRepository;

  public BoardDetailResponse boardDetail(Long boardId) {

    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new BoardIdNotFoundException(boardId));

    String email = userRepository.findByEmail(board.getEmail())
        .map(User::getEmail)
        .orElseThrow(()->new UserEmailNotFound(board.getEmail()));

    board.upViewCount();

    return BoardDetailResponse.fromEntity(board, email);
  }
}
