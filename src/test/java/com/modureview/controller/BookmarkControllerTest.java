package com.modureview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.entity.Board;
import com.modureview.entity.BookMark;
import com.modureview.entity.User;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.BookmarkRepository;
import com.modureview.repository.UserRepository;
import com.modureview.service.BookmarkService;
import com.modureview.utill.TestUtil;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
@Transactional
@ActiveProfiles("h2")
class BookmarkControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private BookmarkService bookmarkService;
  @Autowired private BookmarkRepository bookmarkRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private BoardRepository boardRepository;

  private TestUtil testUtil;
  private Board savedBoard;
  private User user1;
  private User user2;

  @BeforeEach
  void setUp() {
    this.testUtil = new TestUtil();
    user1 = userRepository.save(testUtil.newUser("test@test.com"));
    user2 = userRepository.save(testUtil.newUser("test1@test.com"));
    savedBoard = boardRepository.save(testUtil.newBoard(user1));

    List<BookMark> bookMarks = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      bookMarks.add(
          BookMark.builder()
              .nickname(user2.getNickname())
              .boardId(savedBoard.getId())
              .build());
    }
    bookmarkRepository.saveAll(bookMarks);
  }

  @Test
  @DisplayName("GET /reviews/{reviewId}/bookmarks - isBookmarked : false")
  void getBookmark_false() throws Exception {
    long startTime = System.nanoTime();
    MvcResult mvcResult =
        mockMvc
            .perform(
                get("/reviews/{reviewId}/bookmarks", savedBoard.getId())
                    // no nickname cookie -> defaultValue="null"
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_100_000.0;
    log.info(
        "BookmarkControllerTest.getBoardDetail()-false 실행 시간 : {} ns ({} ms)",
        duration,
        String.format("%.3f", durationMs));
    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson =
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }

  @Test
  @DisplayName("GET /reviews/{reviewId}/bookmarks - isBookmarked : true")
  void getBookmark_success() throws Exception {
    long startTime = System.nanoTime();
    MvcResult mvcResult =
        mockMvc
            .perform(
                get("/reviews/{reviewId}/bookmarks", savedBoard.getId())
                    .cookie(new Cookie("userNickname", user2.getNickname()))
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_100_000.0;
    log.info(
        "BookmarkControllerTest.getBoardDetail()-true 실행 시간 : {} ns ({} ms)",
        duration,
        String.format("%.3f", durationMs));
    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson =
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }
}
