package com.modureview.controller;

import static org.junit.jupiter.api.Assertions.*;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.request.BoardSearchRequest;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.repository.BoardSearchRepository;
import com.modureview.service.BoardSearchService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoardSearchBoardControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private BoardSearchService boardSearchService;
  @Autowired
  private BoardSearchRepository boardSearchRepository;
  @BeforeEach
  void setUp(){
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
      for (int j = 10; j<20 ; j++){
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
  void cleanUp(){
    boardSearchRepository.deleteAll();
  }

  @Test
  @DisplayName("GET /reviews -keyword:테스트 page:0 sort:recent 성공시 201")
  void Search_Success_reviews_recent() throws Exception{
    long startTime = System.nanoTime();
    BoardSearchRequest request = BoardSearchRequest.builder()
        .keyword("테스트")
        .page(0)
        .sort("recent")
        .build();

    MvcResult mvcResult = mockMvc.perform(get("/reviews")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime); // 나노초 단위
    double durationMs = duration / 1_000_000.0; // 밀리초 단위
    log.info("BoardSearchBoardControllerTest.Search_Success_reviews_recent() 실행 시간: {} ns ({} ms)", duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);


    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }

  @Test
  @DisplayName("GET /reviews -keyword:테스트 page:2 sort:hotcomment 성공시 201")
  void Search_Success_reviews_hotcomment() throws Exception{
    long startTime = System.nanoTime();
    BoardSearchRequest request = BoardSearchRequest.builder()
        .keyword("테스트")
        .page(2)
        .sort("hotcomment")
        .build();

    MvcResult mvcResult = mockMvc.perform(get("/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    long endTime = System.nanoTime();
    long duration = (endTime - startTime); // 나노초 단위
    double durationMs = duration / 1_000_000.0; // 밀리초 단위
    log.info("BoardSearchBoardControllerTest.Search_Success_reviews_hotcomment() 실행 시간: {} ns ({} ms)", duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }
  @Test
  @DisplayName("GET /reviews -keyword:테스트 page:2 sort:hotbookmark 성공시 201")
  void Search_Success_reviews_hotbookmark() throws Exception{
    long startTime = System.nanoTime();
    BoardSearchRequest request = BoardSearchRequest.builder()
        .keyword("테스트")
        .page(2)
        .sort("hotbookmark")
        .build();

    MvcResult mvcResult = mockMvc.perform(get("/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime); // 나노초 단위
    double durationMs = duration / 1_000_000.0; // 밀리초 단위
    log.info("BoardSearchBoardControllerTest.Search_Success_reviews_hotbookmark() 실행 시간: {} ns ({} ms)", duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }
  @Test
  @DisplayName("GET /reviews -keyword:테스트 page:2 sort:hotbookmark 성공시 201")
  void Search_Content_reviews_recent() throws Exception{
    long startTime = System.nanoTime();
    BoardSearchRequest request = BoardSearchRequest.builder()
        .keyword("장충동왕족발보쌈")
        .page(2)
        .sort("recent")
        .build();

    MvcResult mvcResult = mockMvc.perform(get("/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    long endTime = System.nanoTime();
    long duration = (endTime - startTime); // 나노초 단위
    double durationMs = duration / 1_000_000.0; // 밀리초 단위
    log.info("BoardSearchBoardControllerTest.Search_Content_reviews_recent() 실행 시간: {} ns ({} ms)", duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

    log.info("Formatted JSON Response");
    log.info("prettyJson == {}", prettyJson);
  }

}

