package com.modureview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.repository.BoardSearchRepository;
import com.modureview.service.SearchService;
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
@ActiveProfiles("h2")
class BoardCategoryBoardControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private SearchService searchService;

  @Autowired
  private BoardSearchRepository boardSearchRepository;

  @BeforeEach
  void setUp() {
    List<Board> boards = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
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
    }
    boardSearchRepository.saveAll(boards);
  }

  @AfterEach
  void cleanUp() {
    boardSearchRepository.deleteAll();
  }

  @Test
  @DisplayName("GET /reviews - Category테스트 성공시 201")
  void Category_success_reviews_recent() throws Exception {
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(get("/reviews")
            .param("category", "car")
            .param("cursorId", "0")
            .param("sort", "recent")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info(
        "BoardCategoryBoardControllerTest.Category_success_reviews_recent() 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);

    MvcResult mvcResult1 = mockMvc.perform(get("/reviews")
            .param("category", "car")
            .param("cursorId", "25")
            .param("sort", "recent")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody1 = mvcResult1.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject1 = objectMapper.readValue(responseBody1, Object.class);
    String prettyJson1 = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject1);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson1);
  }
}