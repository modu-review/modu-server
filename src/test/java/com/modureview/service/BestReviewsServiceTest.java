package com.modureview.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.BestReviewDto;
import com.modureview.enums.errors.BestReviewErrorCode;
import com.modureview.exception.bestReviewException.JsonParsingException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

@ExtendWith(MockitoExtension.class)
class BestReviewsServiceTest {


  @InjectMocks
  private BestReviewsService bestReviewsService;

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private ZSetOperations<String, String> zSetOperations;
  @Mock
  private ValueOperations<String, String> valueOperations;

  @BeforeEach
  void setUp() {
    when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  @DisplayName("베스트 리뷰 조회 성공")
  void getBestReviews_성공() throws JsonProcessingException {
    // given
    String category = "food";
    String sortedSetKey = "best_reviews:" + category;

    // Redis에서 가져올 가짜 데이터 설정
    Set<String> boardIds = Set.of("101", "102");
    String boardJson101 = "{\"id\":101, \"title\":\"맛집 리뷰\"}";
    String boardJson102 = "{\"id\":102, \"title\":\"두번째 맛집 리뷰\"}";

    // 2. 실제 DTO 생성
    BestReviewDto dto101 = BestReviewDto.builder()
        .board_id(101L)
        .title("정말 맛있는 국밥집 후기")
        .author("김리뷰")
        .bookmarks(99)
        .thumbnail("/images/food/101_main.jpg")
        .build();

    BestReviewDto dto102 = BestReviewDto.builder()
        .board_id(102L)
        .title("인생 파스타 맛집 찾았어요")
        .author("이테스트")
        .bookmarks(80)
        .thumbnail("/images/pasta/102_main.jpg")
        .build();

    // 1. ZSet에서 boardId 목록을 가져오는 동작 Mocking
    when(zSetOperations.reverseRange(sortedSetKey, 0, 5)).thenReturn(boardIds);

    // 2. 각 boardId로 JSON 데이터를 가져오는 동작 Mocking
    when(valueOperations.get("board:101")).thenReturn(boardJson101);
    when(valueOperations.get("board:102")).thenReturn(boardJson102);

    // 3. JSON을 DTO로 변환하는 동작 Mocking
    when(objectMapper.readValue(boardJson101, BestReviewDto.class)).thenReturn(dto101);
    when(objectMapper.readValue(boardJson102, BestReviewDto.class)).thenReturn(dto102);

    // when
    List<BestReviewDto> result = bestReviewsService.getBestReviews(category);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(dto101, dto102);
  }

  @Test
  @DisplayName("조회된 베스트 리뷰가 없을 때 빈 리스트 반환")
  void getBestReviews_결과없음() {
    // given
    String category = "sports";
    String sortedSetKey = "best_reviews:" + category;
    when(zSetOperations.reverseRange(sortedSetKey, 0, 5)).thenReturn(Collections.emptySet());

    // when
    List<BestReviewDto> result = bestReviewsService.getBestReviews(category);

    // then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }
  @Test
  @DisplayName("Redis의 JSON 데이터 파싱 실패 시 예외 발생")
  void getBestReviews_JSON파싱실패() throws JsonProcessingException, JsonProcessingException {
    // given
    String category = "car";
    String sortedSetKey = "best_reviews:" + category;
    Set<String> boardIds = Set.of("201");
    String malformedJson = "{\"id\":201, \"title\":\"잘못된 JSON 형식"; // 닫는 괄호 없음

    when(zSetOperations.reverseRange(sortedSetKey, 0, 5)).thenReturn(boardIds);
    when(valueOperations.get("board:201")).thenReturn(malformedJson);

    when(objectMapper.readValue(malformedJson, BestReviewDto.class))
        .thenThrow(new JsonParsingException(BestReviewErrorCode.JSON_PROCESSING_ERROR)); // 또는 JsonProcessingException

    // when & then
    assertThrows(JsonParsingException.class, () -> {
      bestReviewsService.getBestReviews(category);
    });
  }


}