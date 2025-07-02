package com.modureview.service;


import com.modureview.dto.response.BookMarkDetailResponse;
import com.modureview.entity.Board;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardRepository;
import com.modureview.entity.Bookmarks;
import com.modureview.enums.errors.BookmarkErrorCode;
import com.modureview.exception.bookmark.BookmarkNotExistException;
import com.modureview.dto.response.BookMarkDetailResponse;
import com.modureview.entity.Board;
import com.modureview.entity.BookMark;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.BookMarkRepository;
import com.modureview.repository.UserRepository;
import com.modureview.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookMarkService {

  private final BookMarkRepository bookmarkRepository;
  private final UserRepository userRepository;
  private final BoardRepository boardRepository;
  private final StringRedisTemplate stringRedisTemplate;

  @Transactional
  public void saveBookmark(Long boardId, Long userId, String email) {
    BookMark bookmark = BookMark.builder()
        .boardId(boardId)
        .userId(userId)
        .userEmail(email)
        .build();

    bookmarkRepository.save(bookmark);

  }

  @Transactional
  public void deleteBookmark(Long boardId, String email) {
    BookMark bookmarks = bookmarkRepository.findByUserEmailAndBoardId(email, boardId)
        .orElseThrow(() -> new BookmarkNotExistException(BookmarkErrorCode.BOOKMARK_NOT_FOUND));
    bookmarkRepository.delete(bookmarks);
  }

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
      bookmarkRepository.existsByBoardIdAndEmail(reviewId, email)
          .orElse(false);
      return BookMarkDetailResponse.fromEntity(true, targetBoard.getBookmarksCount());
    } else {
      return BookMarkDetailResponse.fromEntity(false, targetBoard.getBookmarksCount());
    }
  }
}
