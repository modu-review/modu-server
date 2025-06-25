package com.modureview.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookmarkService {

  private final StringRedisTemplate stringRedisTemplate;

  public void redisUpdate(Long boardId) {
    String redisKey = "board:" + boardId;
    if (stringRedisTemplate.hasKey(redisKey)){
      stringRedisTemplate.opsForValue().increment(redisKey, 4);
    }
  }

}
