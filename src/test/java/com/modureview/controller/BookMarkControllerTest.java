package com.modureview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.entity.Board;
import com.modureview.entity.BookMark;
import com.modureview.entity.User;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.BookMarkRepository;
import com.modureview.repository.UserRepository;
import com.modureview.service.BookMarkService;
import com.modureview.utill.TestUtil;
import jakarta.servlet.http.Cookie;
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
@ActiveProfiles("h2")
class BookMarkControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BookMarkService bookMarkService;

  @Autowired
  private BookMarkRepository bookMarkRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BoardRepository boardRepository;

  private TestUtil testUtil;


  @BeforeEach
  void setUp() {
    this.testUtil = new TestUtil();
    User user = userRepository.save(testUtil.newUser("test@test.com"));
    User user1 = userRepository.save(testUtil.newUser("test1@test.com"));
    Board board = boardRepository.save(testUtil.newBoard(user));
    List<BookMark> bookMarks = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      bookMarks.add(
          BookMark.builder()
              .email("test" + i + "@test.com")
              .boardId(board.getId())
              .isBookmarked(true)
              .build()
      );
    }
    bookMarkRepository.saveAll(bookMarks);
  }

  @Test
  @DisplayName("GET /reviews/{reviewId}/bookMarkController - isBookmarked : false")
  void getBookmark_false() throws Exception {
    Board byAuthorEmail = boardRepository.findByAuthorEmail("test@test.com");
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/reviews/{reviewId}/bookmarks", byAuthorEmail.getId())
                .cookie(new Cookie("email", "test@test.com"))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_100_000.0;
    log.info("BookMarkControllerTest.getBoardDetail()-false 실행 시간 : {} ns ({} ms)", duration,
        String.format("%.3f", durationMs));
    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);
    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }


  @Test
  @DisplayName("GET /reviews/{reviewId}/bookMarkController - isBookmarked : true")
  void getBookmark_success() throws Exception {
    Board byAuthorEmail = boardRepository.findByAuthorEmail("test@test.com");
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/reviews/{reviewId}/bookmarks", byAuthorEmail.getId())
                .cookie(new Cookie("email", "test1@test.com"))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_100_000.0;
    log.info("BookMarkControllerTest.getBoardDetail()-true 실행 시간 : {} ns ({} ms)", duration,
        String.format("%.3f", durationMs));
    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);
    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }
}