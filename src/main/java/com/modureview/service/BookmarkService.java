package com.modureview.service;

import com.modureview.entity.Bookmarks;
import com.modureview.repository.BookmarkRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;

  private final StringRedisTemplate stringRedisTemplate;

  @Transactional
  public void saveBookmark(Long boardId,Long userId, String email) {
    Bookmarks bookmark = Bookmarks.builder()
        .boardId(boardId)
        .userId(userId)
        .userEmail(email)
        .build();

    bookmarkRepository.save(bookmark);

  }
}
