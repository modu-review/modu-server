package com.modureview.service;

import com.modureview.dto.response.BookmarkDetailResponse;
import com.modureview.entity.Board;
import com.modureview.entity.BookMark;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.enums.errors.BookmarkErrorCode;
import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.exception.bookmark.BookmarkNotExistException;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.BookmarkRepository;
import com.modureview.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final UserRepository userRepository;
  private final BoardRepository boardRepository;
  private final StringRedisTemplate stringRedisTemplate;

  @Transactional
  public void saveBookmark(Long boardId, Long userId, String nickname) {
    BookMark bookmark = BookMark.builder()
        .boardId(boardId)
        .userId(userId)
        .nickname(nickname)
        .build();

    bookmarkRepository.save(bookmark);

    boardRepository.findById(boardId).ifPresent(board -> {
      board.setBookmarksCount(board.getBookmarksCount() + 1);
      boardRepository.save(board);
    });
  }

  @Transactional
  public void deleteBookmark(Long boardId, String nickname) {
    BookMark bookmarks = bookmarkRepository.findByNicknameAndBoardId(nickname, boardId)
        .orElseThrow(() -> new BookmarkNotExistException(BookmarkErrorCode.BOOKMARK_NOT_FOUND));
    bookmarkRepository.delete(bookmarks);

    boardRepository.findById(boardId).ifPresent(board -> {
      board.setBookmarksCount(board.getBookmarksCount() - 1);
      boardRepository.save(board);
    });
  }

  public BookmarkDetailResponse bookmarkDetail(Long reviewId, String nickname) {
    log.info("reviewId == {}", reviewId);
    log.info("email    == {}", nickname);

    Board targetBoard = boardRepository.findById(reviewId)
        .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_ID_NOTFOUND));
    
    if (!"null".equals(nickname)) {
      userRepository.findByNickname(nickname)
          .orElseThrow(() -> new CustomException(JwtErrorCode.FORBIDDEN));

      boolean hasBookmark = bookmarkRepository.existsByNicknameAndBoardId(nickname, reviewId);
      return BookmarkDetailResponse.fromEntity(
          hasBookmark,
          targetBoard.getBookmarksCount()
      );
    }

    return BookmarkDetailResponse.fromEntity(
        false,
        targetBoard.getBookmarksCount()
    );
  }
}
