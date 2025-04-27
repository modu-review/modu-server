package com.modureview.service;


import com.modureview.dto.BoardDetailResponse;
import com.modureview.entity.Board;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

  private final BoardRepository boardRepository;

  private final UserRepository userRepository;

  public BoardDetailResponse boardDetail(Long boardId) {

    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "게시글(id=" + boardId + ")을 찾을 수 없습니다."
        ));


    String email = userRepository.findById(board.getUserId())
        .map(user -> user.getEmail())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "유저아이디(id=" + board.getUserId() + ")가 존재하지 않습니다."
        ));

    board.upViewCount();




    return BoardDetailResponse.fromEntity(board,email);
  }

}
