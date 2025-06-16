package com.modureview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.BestReviewDto;
import com.modureview.entity.Board;
import com.modureview.enums.errors.BestReviewErrorCode;
import com.modureview.exception.bestReviewException.JsonParsingFromRedisException;
import com.modureview.repository.BoardRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class BestReviewsService {

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;
  private final BoardRepository boardRepository;

  public List<BestReviewDto> getBestReviews(String category) {
    String sortedSetKey = "best_reviews:" + category;
    Set<String> boardIds = redisTemplate.opsForZSet().reverseRange(sortedSetKey, 0, 5);

    if (boardIds == null || boardIds.isEmpty()) {
      return Collections.emptyList();
    }

    List<BestReviewDto> bestReviews = new ArrayList<>();
    for (String boardId : boardIds) {
      String boardJson = redisTemplate.opsForValue().get("board:" + boardId);
      if (boardJson != null) {
        try {
          BestReviewDto dto = objectMapper.readValue(boardJson, BestReviewDto.class);
          bestReviews.add(dto);
        } catch (JsonProcessingException e) {
          log.error("BestReview Board정보 json파싱 실패: {}. JSON data: {}", boardId, boardJson, e);
          throw new JsonParsingFromRedisException(BestReviewErrorCode.JSON_PROCESSING_ERROR);
        }
      }
    }
    return Collections.unmodifiableList(bestReviews);

  }

  public void aggregate(){
    List<Board> topSix= boardRepository.findTop6BoardsPerCategory();
    /*
    TODO:
    - Redis데이터 삭제
    - Redis에 데이터 저장 및 로그 표시
    - 각 Board정보 Redis에 저장 및 로그 표시
    * */
  }
}
