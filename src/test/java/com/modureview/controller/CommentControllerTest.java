package com.modureview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.entity.Board;
import com.modureview.entity.Comment;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.CommentRepository;
import com.modureview.repository.UserRepository;
import com.modureview.utill.TestUtil;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private UserRepository userRepository;

  private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    this.testUtil = new TestUtil();
  }

  @Test
  @DisplayName("GET /reviews/{reviewId}/comments 성공")
  void getBoardDetail_success() throws Exception {
    Board board = testUtil.newBoard(userRepository.save(testUtil.newUser("test@test.com")));
    Board newBoard = boardRepository.save(board);
    Long newBoardId = newBoard.getId();
    List<Comment> comments = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      comments.add(
          Comment.builder()
              .boardId(newBoardId)
              .author(newBoard.getAuthorEmail())
              .content("test content" + i)
              .createdAt(LocalDateTime.now().minusMinutes(i))
              .build());
    }
    commentRepository.saveAll(comments);
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/reviews/{reviewId}/comments", newBoardId)
                .param("page", "1")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info("CommentControllerTest.getBoardDetail() 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));
    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);
    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);

  }


}