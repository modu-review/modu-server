package com.modureview.service;


import com.modureview.dto.response.BookMarkDetailResponse;
import com.modureview.entity.Board;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.BookMarkRepository;
import com.modureview.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookMarkService {

  private final BookMarkRepository bookMarkRepository;
  private final UserRepository userRepository;
  private final BoardRepository boardRepository;

  public BookMarkDetailResponse bookMarkDetail(Long reviewId, String email) {
    log.info("reviewId == {}", reviewId);
    log.info("email == {}", email);
    Board targetBoard = boardRepository.findById(reviewId).orElseThrow(
        () -> new CustomException(BoardErrorCode.BOARD_ID_NOTFOUND)
    );
    if (!"null".equals(email)) {
      userRepository.findByEmail(email).orElseThrow(
          () -> new CustomException(JwtErrorCode.FORBIDDEN)
      );
      bookMarkRepository.existsByBoardIdAndEmail(reviewId, email)
          .orElse(false);
      return BookMarkDetailResponse.fromEntity(true, targetBoard.getBookmarksCount());
    } else {
      return BookMarkDetailResponse.fromEntity(false, targetBoard.getBookmarksCount());
    }
  }
}
