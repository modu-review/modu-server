package com.modureview.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.entity.Board;
import com.modureview.entity.User;
import com.modureview.repository.MyPageRepository;
import com.modureview.repository.UserRepository;
import com.modureview.service.MyPageService;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class MyPageControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MyPageService myPageService;
  @Autowired
  private MyPageRepository myPageRepository;
  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {

    User newUser = userRepository.save(User.builder()
        .email("작성자")
        .build());
    User targetUser = userRepository.save(User.builder()
        .email("test")
        .build());

    List<Board> boards = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      if (i % 2 != 0) {
        boards.add(
            Board.builder().title("TestTitle" + i).user(newUser).authorEmail(newUser.getEmail())
                .content("<p>내용 예시 " + i + "</p>")
                .commentsCount(ThreadLocalRandom.current().nextInt(1, 101))
                .bookmarksCount(ThreadLocalRandom.current().nextInt(1, 101)).build());
      } else {
        boards.add(
            Board.builder().user(targetUser).authorEmail(targetUser.getEmail())
                .content("<p>내용 예시 " + i + "</p>")
                .commentsCount(ThreadLocalRandom.current().nextInt(1, 101))
                .bookmarksCount(ThreadLocalRandom.current().nextInt(1, 101)).build());
      }
      myPageRepository.saveAll(boards);
    }
  }

  @AfterEach
  void tearDown() {
    myPageRepository.deleteAll();
  }

  @Test
  @DisplayName("GET /users/me/reviews -- 성공")
  void MyPage_Success() throws Exception {
    long startTime = System.currentTimeMillis();
    MvcResult mvcResult = mockMvc.perform(
            get("/users/me/reviews")
                .cookie(new Cookie("userEmail", "test"))
                .param("page", "1")
        )
        .andExpect(status().isOk())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info(
        "MyPageControllerTest.GET /users/me/reviews -- 성공 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));

    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("prettyJson == {}", prettyJson);


  }
}