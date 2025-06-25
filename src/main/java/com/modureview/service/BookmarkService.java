package com.modureview.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookmarkService {

  private final StringRedisTemplate stringRedisTemplate;

  public void redisUpdate(Long boardId) {
    /*
    Todo :1. Redis에 id있나 체크
    * */
    String redisKey = "board:" + boardId;
    if (stringRedisTemplate.hasKey(redisKey)){
      // TODO 2. if redis has the board id then update the score + 4 else skip

    }
  }

}
