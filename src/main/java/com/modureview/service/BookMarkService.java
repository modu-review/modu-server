package com.modureview.service;

import com.modureview.entity.Bookmarks;
import com.modureview.enums.errors.BookmarkErrorCode;
import com.modureview.exception.bookmark.BookmarkNotExistException;
import com.modureview.repository.BookMarkRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookMarkService {

  private final BookMarkRepository bookmarkRepository;

  private final StringRedisTemplate stringRedisTemplate;

  @Transactional
  public void saveBookmark(Long boardId, Long userId, String email) {
    Bookmarks bookmark = Bookmarks.builder()
        .boardId(boardId)
        .userId(userId)
        .userEmail(email)
        .build();

    bookmarkRepository.save(bookmark);

  }

  @Transactional
  public void deleteBookmark(Long boardId, String email) {
    Bookmarks bookmarks = bookmarkRepository.findByUserEmailAndBoardId(email, boardId)
        .orElseThrow(() -> new BookmarkNotExistException(BookmarkErrorCode.BOOKMARK_NOT_FOUND));
    bookmarkRepository.delete(bookmarks);
  }

}
