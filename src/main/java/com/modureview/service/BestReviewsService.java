package com.modureview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.BestReviewDto;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.enums.errors.BestReviewErrorCode;
import com.modureview.exception.bestReviewException.JsonParsingException;
import com.modureview.repository.BoardRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
          throw new JsonParsingException(BestReviewErrorCode.JSON_PROCESSING_ERROR);
        }
      }
    }
    return Collections.unmodifiableList(bestReviews);

  }

  public void aggregate(){
    List<Board> topSix= boardRepository.findTop6BoardsPerCategory();
    List<Board> allSix = boardRepository.findallCategory();

    List<String> zsetKeysToDelete = Arrays.stream(Category.values())
        .map(c -> "best_reviews:" + c.name())
        .collect(Collectors.toList());

    if (!zsetKeysToDelete.isEmpty()) {
      redisTemplate.delete(zsetKeysToDelete);
      log.info("베스트 리뷰 랭킹(ZSET) 키 {}개를 삭제했습니다.", zsetKeysToDelete.size());
    }

    log.info("카테고리별 베스트 리뷰 랭킹을 Redis에 저장합니다");
    topSix.forEach(board -> {
      String categoryName = board.getCategory().name();
      String zsetKey = "best_reviews:" + categoryName;
      double score = calculatePopularityScore(board);

      redisTemplate.opsForZSet().add(zsetKey, board.getId().toString(), score);

      cacheBoardDetails(board);
    });
    log.info("카테고리별 랭킹 저장 완료. 총 {}건", topSix.size());


    log.info("'all' 카테고리의 베스트 리뷰 랭킹을 Redis에 저장합니다...");
    String allZsetKey = "best_reviews:all";
    allSix.forEach(board -> {
      double score = calculatePopularityScore(board);
      redisTemplate.opsForZSet().add(allZsetKey, board.getId().toString(), score);

      cacheBoardDetails(board);
    });
    log.info("'all' 카테고리 랭킹 저장 완료. 총 {}건", allSix.size());

  }

  private void cacheBoardDetails(Board board) {
    String boardKey = "board:" + board.getId();
    try {
      String boardJson = objectMapper.writeValueAsString(board);
      redisTemplate.opsForValue().set(boardKey, boardJson);
    } catch (JsonProcessingException e) {
      throw new JsonParsingException(BestReviewErrorCode.JSON_PROCESSING_ERROR);
    }
  }

  private double calculatePopularityScore(Board board) {
    return (board.getBookmarksCount() * 4) +
        (board.getCommentsCount() * 2) +
        (board.getViewCount());
  }
}
