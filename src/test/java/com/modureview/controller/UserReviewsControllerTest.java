package com.modureview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.entity.User;
import com.modureview.repository.UserReviewsRepository;
import com.modureview.repository.UserRepository;
import com.modureview.service.UserReviewsService;
import com.modureview.utill.TestUtil;
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
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("h2")
class UserReviewsControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserReviewsService userReviewsService;
  @Autowired
  private UserReviewsRepository userReviewsRepository;
  @Autowired
  private UserRepository userRepository;

  private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    this.testUtil = new TestUtil();
    
    User testUser = userRepository.save(testUtil.newUser("test@example.com"));
    User otherUser = userRepository.save(testUtil.newUser("other@example.com"));
    
    List<Board> boards = new ArrayList<>();
    
    for (int i = 0; i < 5; i++) {
      boards.add(
          Board.builder()
              .title("테스트 게시물 " + i)
              .user(testUser)
              .authorEmail("test@example.com")
              .category(Category.car)
              .content("<p>테스트 내용 " + i + "</p>")
              .commentsCount(i * 2)
              .bookmarksCount(i * 3)
              .build()
      );
    }
    
    for (int i = 0; i < 3; i++) {
      boards.add(
          Board.builder()
              .title("다른 사용자 게시물 " + i)
              .user(otherUser)
              .authorEmail("other@example.com")
              .category(Category.food)
              .content("<p>다른 사용자 내용 " + i + "</p>")
              .commentsCount(i * 5)
              .bookmarksCount(i * 4)
              .build()
      );
    }
    
    userReviewsRepository.saveAll(boards);
  }

  @AfterEach
  void cleanUp() {
    userReviewsRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("GET /users/{memberEmail}/reviews - recent 정렬로 사용자 리뷰 조회 성공")
  void getUserReviews_Success_Recent() throws Exception {
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/users/test@example.com/reviews")
                .param("cursor", "0")
                .param("sort", "recent")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info("UserReviewsControllerTest.getUserReviews_Success_Recent() 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }

  @Test
  @DisplayName("GET /users/{memberEmail}/reviews - hotcomments 정렬로 사용자 리뷰 조회 성공")
  void getUserReviews_Success_HotComment() throws Exception {
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/users/test@example.com/reviews")
                .param("cursor", "0")
                .param("sort", "hotcomments")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info("UserReviewsControllerTest.getUserReviews_Success_HotComment() 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }

  @Test
  @DisplayName("GET /users/{memberEmail}/reviews - hotbookmarks 정렬로 사용자 리뷰 조회 성공")
  void getUserReviews_Success_HotBookmark() throws Exception {
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/users/test@example.com/reviews")
                .param("cursor", "0")
                .param("sort", "hotbookmarks")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info("UserReviewsControllerTest.getUserReviews_Success_HotBookmark() 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }

  @Test
  @DisplayName("GET /users/{memberEmail}/reviews - 존재하지 않는 사용자 이메일로 조회")
  void getUserReviews_Success_NonExistentUser() throws Exception {
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/users/nonexistent@example.com/reviews")
                .param("cursor", "0")
                .param("sort", "recent")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info("UserReviewsControllerTest.getUserReviews_Success_NonExistentUser() 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }

  @Test
  @DisplayName("GET /users/{memberEmail}/reviews - 커서 기반 페이징 테스트")
  void getUserReviews_Success_CursorPaging() throws Exception {
    List<Board> savedBoards = userReviewsRepository.findAll();
    Long existingBoardId = savedBoards.get(2).getId();
    
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/users/test@example.com/reviews")
                .param("cursor", existingBoardId.toString())
                .param("sort", "recent")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info("UserReviewsControllerTest.getUserReviews_Success_CursorPaging() 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }
}
