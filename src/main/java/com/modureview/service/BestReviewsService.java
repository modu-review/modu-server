package com.modureview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.response.BestReviewResponse;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.repository.BoardRepository;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

  public List<BestReviewResponse> getBestReviewsForCategory(String categoryName) {
    String zsetKey = "best_reviews:" + categoryName;

    Set<String> boardIds = redisTemplate.opsForZSet().reverseRange(zsetKey, 0, -1);

    if (boardIds == null || boardIds.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> boardCacheKeys = boardIds.stream()
        .map(id -> "board:" + id)
        .toList();

    List<String> dtoJSONs = redisTemplate.opsForValue().multiGet(boardCacheKeys);

    return dtoJSONs.stream()
        .filter(Objects::nonNull).
        map(json -> {
          try {
            return objectMapper.readValue(json, BestReviewResponse.class);
          } catch (JsonProcessingException e) {
            log.error("Redis의 JSON을 DTO로 변환하는 데 실패했습니다.", e);
            return null;
          }

        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public void aggregate() {
    log.info("======================================================");
    log.info("베스트 리뷰 집계 작업을 시작합니다.");
    log.info("======================================================");

    List<Long> topBoardIdsPerCategory = boardRepository.findTop6BoardsPerCategory();
    log.info(">>>> [1-1] 카테고리별 Top 6 ID 목록 조회 결과 ({}건): {}",
        topBoardIdsPerCategory != null ? topBoardIdsPerCategory.size() : "null",
        topBoardIdsPerCategory);

    List<Long> topBoardIdsAll = boardRepository.findallCategory();
    log.info(">>>> [1-2] 전체 Top 6 ID 목록 조회 결과 ({}건): {}",
        topBoardIdsAll != null ? topBoardIdsAll.size() : "null",
        topBoardIdsAll);

    List<Board> topBoardsPerCategory;
    if (topBoardIdsPerCategory == null || topBoardIdsPerCategory.isEmpty()) {
      log.warn("카테고리별 상위 게시물 ID가 없어 조회를 건너뜁니다.");
      topBoardsPerCategory = Collections.emptyList();
    } else {
      topBoardsPerCategory = boardRepository.findByIdsWithUser(topBoardIdsPerCategory);
      log.info(">>>> [2-1] ID로 조회된 카테고리별 Board 엔티티 목록 ({}건)", topBoardsPerCategory.size());
    }

    List<Board> topBoardsAll;
    if (topBoardIdsAll == null || topBoardIdsAll.isEmpty()) {
      log.warn("전체 상위 게시물 ID가 없어 조회를 건너뜁니다.");
      topBoardsAll = Collections.emptyList();
    } else {
      topBoardsAll = boardRepository.findByIdsWithUser(topBoardIdsAll);
      log.info(">>>> [2-2] ID로 조회된 전체 Board 엔티티 목록 ({}건)", topBoardsAll.size());
    }

    List<String> zsetKeysToDelete = Arrays.stream(Category.values())
        .map(c -> "best_reviews:" + c.name())
        .collect(Collectors.toList());

    if (!zsetKeysToDelete.isEmpty()) {
      redisTemplate.delete(zsetKeysToDelete);
      log.info("베스트 리뷰 랭킹(ZSET) 키 {}개를 삭제했습니다.", zsetKeysToDelete.size());
    }

    log.info("카테고리별 베스트 리뷰 랭킹을 Redis에 저장합니다...");
    topBoardsPerCategory.forEach(board -> {
      String categoryName = board.getCategory().name();
      String zsetKey = "best_reviews:" + categoryName;
      double score = calculatePopularityScore(board);
      redisTemplate.opsForZSet().add(zsetKey, board.getId().toString(), score);
      cacheBoardDetails(board);
    });
    log.info("카테고리별 랭킹 저장 완료. 총 {}건", topBoardsPerCategory.size());

    log.info("'all' 카테고리의 베스트 리뷰 랭킹을 Redis에 저장합니다...");
    String allZsetKey = "best_reviews:all";
    topBoardsAll.forEach(board -> {
      double score = calculatePopularityScore(board);
      redisTemplate.opsForZSet().add(allZsetKey, board.getId().toString(), score);
      cacheBoardDetails(board);
    });
    log.info("'all' 카테고리 랭킹 저장 완료. 총 {}건", topBoardsAll.size());

    log.info("베스트 리뷰 집계 작업을 성공적으로 완료했습니다.");
  }

  private void cacheBoardDetails(Board board) {
    String boardKey = "board:" + board.getId();
    try {
      BestReviewResponse dto = BestReviewResponse.from(board);
      String boardJson = objectMapper.writeValueAsString(dto);
      redisTemplate.opsForValue().set(boardKey, boardJson, Duration.ofHours(24));

    } catch (JsonProcessingException e) {
      log.error("Board 객체를 JSON으로 변환하는 데 실패했습니다. boardId: {}", board.getId(), e);
    }
  }

  private double calculatePopularityScore(Board board) {
    return (board.getBookmarksCount() * 4) +
        (board.getCommentsCount() * 2) +
        (board.getViewCount());
  }
}
