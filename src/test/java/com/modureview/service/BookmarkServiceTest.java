package com.modureview.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

  @Mock
  private StringRedisTemplate stringRedisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @InjectMocks
  private BookmarkService bookmarkService;

  private final Long TEST_BOARD_ID = 123L;
  private final String REDIS_KEY = "board:" + TEST_BOARD_ID;

  @BeforeEach
  void setUp() {
    lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  @DisplayName("랭킹에 존재해서 점수가 증가하는 경우")
  void redisScoreUpdate(){
    //Given
    when(stringRedisTemplate.hasKey(REDIS_KEY)).thenReturn(true);

    //when
    bookmarkService.redisUpdate(TEST_BOARD_ID);

    //then
    verify(stringRedisTemplate, times(1)).hasKey(REDIS_KEY);
    verify(valueOperations, times(1)).increment(REDIS_KEY, 4);

  }

  @Test
  @DisplayName("key가 없는 경우")
  public void noRedisScoreUpdate(){
    //Given
    when(stringRedisTemplate.hasKey(REDIS_KEY)).thenReturn(false);

    //when
    bookmarkService.redisUpdate(TEST_BOARD_ID);

    //then
    verify(stringRedisTemplate, times(1)).hasKey(REDIS_KEY);
    verify(valueOperations, never()).increment(anyString(), anyLong());
  }
}