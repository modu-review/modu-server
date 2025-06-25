package com.modureview.service;

import com.modureview.dto.request.BookmarkRequest;
import com.modureview.entity.Bookmark;
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

  public void redisUpdate(Long boardId) {
    String redisKey = "board:" + boardId;
    if (stringRedisTemplate.hasKey(redisKey)){
      stringRedisTemplate.opsForValue().increment(redisKey, 4);
    }
  }

  @Transactional
  public void saveBookmark(BookmarkRequest bookmarkRequest) {
    Bookmark bookmark = Bookmark.builder()
        .boardId(bookmarkRequest.boardId())
        .userId(bookmarkRequest.userId())
        .build();

    bookmarkRepository.save(bookmark);
  }
}
