package com.modureview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.modureview.dto.request.BoardSearchRequest;
import com.modureview.dto.response.BoardSearchResponse;
import com.modureview.dto.response.CustomPageResponse;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.repository.BoardSearchRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BoardSearchServiceTest {

  @Autowired
  private BoardSearchService boardSearchService;
  @Autowired
  private BoardSearchRepository boardSearchRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    List<Board> boards = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      boards.add(
          Board.builder()
              .title("테스트" + i)
              .authorEmail("작성자" + i)
              .category(Category.car)
              .content("<p>내용 예시 " + i + "<p/>")
              .commentsCount(i + i)
              .bookmarksCount(i + i + i)
              .build()
      );
      boards.add(
          Board.builder()
              .title("target_1")
              .authorEmail("target_1")
              .category(Category.car)
              .content("<p> target_1 <p/>")
              .commentsCount(24)
              .bookmarksCount(46)
              .build()
      );
      for (int j = 10; j < 20; j++) {
        boards.add(
            Board.builder()
                .title("테스트" + i)
                .authorEmail("작성자" + i)
                .category(Category.car)
                .content("<p>내용 예시 " + i + "<p/>")
                .commentsCount(i + i)
                .bookmarksCount(i + i + i)
                .build()
        );
        boards.add(
            Board.builder()
                .title("target_2")
                .authorEmail("target_2")
                .category(Category.car)
                .content("<p> target_2 <p/>")
                .commentsCount(22)
                .bookmarksCount(22)
                .build()
        );
        boards.add(
            Board.builder()
                .title("target_3")
                .authorEmail("target_4")
                .category(Category.car)
                .content("<p> Target_5 <p/>")
                .commentsCount(22)
                .bookmarksCount(22)
                .build()
        );
      }
    }
    boardSearchRepository.saveAll(boards);
  }

  @AfterEach
  void cleanUp() {
    boardSearchRepository.deleteAll();
  }

  @Test
  @DisplayName("Search 성공")
  void Search_Success_recent() throws Exception {

    String keyword = "target";
    int page = 1;
    String sort = "recent";
    long startTime = System.nanoTime();
    Page<Board> rep = boardSearchService.boardSearch(keyword, page, sort);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime); // 나노초 단위
    double durationMs = duration / 1_000_000.0; // 밀리초 단위
    log.info("boardSearchServiceTest.Search_Success_recent() 실행 시간: {} ns ({} ms)", duration,
        String.format("%.3f", durationMs));
    String realJson = objectMapper.
        enable(SerializationFeature.INDENT_OUTPUT)
        .writeValueAsString(rep);
    log.info("realJson == {}", realJson);
  }

  @Test
  @DisplayName("조회개수가 0개")
  void Search_fail_recent() throws Exception {
    String keyword = "장충동왕족발보쌈";
    int page = 1;
    String sort = "recent";
    long startTime = System.nanoTime();
    Page<Board> rep = boardSearchService.boardSearch(keyword, page, sort);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime); // 나노초 단위
    double durationMs = duration / 1_000_000.0; // 밀리초 단위
    log.info("boardSearchServiceTest.Search_fail_recent() 실행 시간: {} ns ({} ms)", duration,
        String.format("%.3f", durationMs));
    String realJson = objectMapper.
        enable(SerializationFeature.INDENT_OUTPUT)
        .writeValueAsString(rep);
    log.info("realJson == {}", realJson);

  }
}