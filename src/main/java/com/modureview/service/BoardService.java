package com.modureview.service;

import com.modureview.dto.request.BookmarkRequest;
import com.modureview.entity.Board;
import com.modureview.entity.User;
import com.modureview.exception.user.BoardNotFoundException;
import com.modureview.exception.user.UserNotFoundException;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BoardService {

  private final UserRepository userRepository;
  private final BoardRepository boardRepository;

  public void addBookmark(BookmarkRequest request) {
    User user = userRepository.findByEmail(request.userEmail())
        .orElseThrow(UserNotFoundException::new);
    Board board = boardRepository.findBoardById(request.boardId())
        .orElseThrow(BoardNotFoundException::new);

  }
}
