package com.modureview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.request.CommentDeleteRequest;
import com.modureview.dto.response.CommentListResponse;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.CommentRepository;
import com.modureview.repository.UserRepository;
import com.modureview.utill.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CommentControllerTest {

  @Autowired
  CommentController commentController;

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
  @DisplayName("delete 결과")
  void deleteComment() {
    CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest("user1@example.com",1L);

    commentController.deleteComment(1L, commentDeleteRequest);

  }

  @Test
  @DisplayName("GET /reviews/{reviewId}/comments 성공")
  void getBoardDetail_success() throws Exception {
    // when
    ResponseEntity<CommentListResponse> responseEntity = commentController.getCommentList(1L, 1);

    // then
    CommentListResponse responseBody = responseEntity.getBody();

    String responseBodyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(responseBody);
    log.info("===== 응답 결과 (JSON) =====");
    log.info(responseBodyJson);
    log.info("==========================");

  }


}
