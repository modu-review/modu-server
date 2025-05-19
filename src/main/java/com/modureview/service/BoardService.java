package com.modureview.service;

import com.modureview.dto.BoardDetailResponse;
import com.modureview.entity.Board;
import com.modureview.enums.BoardErrorCode;
import com.modureview.exception.BoardCustomException;
import com.modureview.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
  private final BoardRepository boardRepository;

  public BoardDetailResponse boardDetail(Long boardId) {
    Board findBoard = boardRepository.findById(boardId).orElseThrow(
        () -> new BoardCustomException(BoardErrorCode.BOARD_ID_NOTFOUND)
    );
    BoardDetailResponse response = BoardDetailResponse.builder()
        .board_id(findBoard.getId())
        .title(findBoard.getTitle())
        .category(findBoard.getCategory())
        .author(findBoard.getAuthorEmail())
        .create_At(findBoard.getCreatedAt())
        .content(findBoard.getContent())
        .comment_count(findBoard.getCommentsCount())
        .bookmarks(findBoard.getBookmarksCount())
        .build();

    return response;
    }
}
