package com.modureview.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.response.BestReviewResponse;
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
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  @DisplayName("베스트 리뷰 조회 성공")
  void getBestReviews_성공() throws JsonProcessingException {
    String category = "food";
    String sortedSetKey = "best_reviews:" + category;

    Set<String> boardIds = Set.of("101", "102");
    String boardJson101 = "{\"board_id\":101, \"title\":\"맛집 리뷰\"}";
    String boardJson102 = "{\"board_id\":102, \"title\":\"두번째 맛집 리뷰\"}";
    List<String> boardCacheKeys = List.of("board:101", "board:102");
    List<String> dtoJSONs = List.of(boardJson101, boardJson102);

    BestReviewResponse dto101 = BestReviewResponse.builder().board_id(101L).title("맛집 리뷰").build();
    BestReviewResponse dto102 = BestReviewResponse.builder().board_id(102L).title("두번째 맛집 리뷰")
        .build();

    when(zSetOperations.reverseRange(sortedSetKey, 0L, -1L)).thenReturn(boardIds);

    when(valueOperations.multiGet(anyList())).thenReturn(dtoJSONs);

    when(objectMapper.readValue(boardJson101, BestReviewResponse.class)).thenReturn(dto101);
    when(objectMapper.readValue(boardJson102, BestReviewResponse.class)).thenReturn(dto102);

    // when
    List<BestReviewResponse> result = bestReviewsService.getBestReviewsForCategory(category);

    // then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).board_id()).isEqualTo(101L);
    assertThat(result.get(1).board_id()).isEqualTo(102L);
  }

  @Test
  @DisplayName("조회된 베스트 리뷰가 없을 때 빈 리스트 반환")
  void getBestReviews_결과없음() {
    // given
    String category = "sports";
    String sortedSetKey = "best_reviews:" + category;

    when(zSetOperations.reverseRange(sortedSetKey, 0L, -1L)).thenReturn(Collections.emptySet());

    // when
    List<BestReviewResponse> result = bestReviewsService.getBestReviewsForCategory(category);

    // then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Redis의 JSON 데이터 파싱 실패 시 해당 건은 제외하고 반환")
  void getBestReviews_JSON파싱실패() throws JsonProcessingException {
    // given
    String category = "car";
    String sortedSetKey = "best_reviews:" + category;
    Set<String> boardIds = Set.of("201");
    String malformedJson = "{\"id\":201, \"title\":\"잘못된 JSON 형식"; // 닫는 괄호 없음
    List<String> dtoJSONs = List.of(malformedJson);

    when(zSetOperations.reverseRange(sortedSetKey, 0L, -1L)).thenReturn(boardIds);

    when(valueOperations.multiGet(anyList())).thenReturn(dtoJSONs);

    when(objectMapper.readValue(malformedJson, BestReviewResponse.class))
        .thenThrow(new JsonProcessingException("파싱 실패") {
        });

    // when
    List<BestReviewResponse> result = bestReviewsService.getBestReviewsForCategory(category);

    // then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }
}