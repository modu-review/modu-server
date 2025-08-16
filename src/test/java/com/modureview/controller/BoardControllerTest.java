package com.modureview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.entity.Board;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.UserRepository;
import com.modureview.utill.TestUtil;
import com.modureview.service.utill.SummarizationService;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class BoardControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BoardRepository boardRepository;

  private TestUtil testUtil;

  @MockBean
  private SummarizationService summarizationService;

  
  @BeforeEach
  void setUp() {
    this.testUtil = new TestUtil();
  }


  @Test
  @DisplayName("/reviews/{id}테스트 성공")
  void getBoardDetail() throws Exception {
    Board board = testUtil.newBoard(userRepository.save(testUtil.newUser("test@test.com")));
    Board newBoard = boardRepository.save(board);
    Long newBoardId = newBoard.getId();
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/reviews/{Id}", newBoardId)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info(
        "BoardControllerTest.getBoardDetail 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));
    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);
  }

  @Test
  @DisplayName("POST /reviews/new XSS 차단: script 포함 시 400")
  void createBoard_rejects_xss_script() throws Exception {
    // given
    String email = "xss@test.com";
    userRepository.save(testUtil.newUser(email));

    Map<String, Object> req = new HashMap<>();
    req.put("title", "악성 스크립트 테스트");
    req.put("content", "<p>정상</p><script>alert('x')</script>");
    req.put("category", "car");
    req.put("nickname", email.split("@")[0]);

    String json = objectMapper.writeValueAsString(req);

    // when/then
    mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/reviews/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /reviews/{id} XSS 차단: onerror 포함 시 400")
  void updateBoard_rejects_xss_onerror() throws Exception {
    // given: 기존 게시글
    String email = "owner2@test.com";
    Board board = testUtil.newBoard(userRepository.save(testUtil.newUser(email)));
    board = boardRepository.save(board);

    Map<String, Object> req = new HashMap<>();
    req.put("title", "수정 제목");
    req.put("content", "<img src=\"a.png\" onerror=\"alert(1)\"/>");
    req.put("category", "car");
    req.put("nickname", email.split("@")[0]);

    String json = objectMapper.writeValueAsString(req);

    // when/then
    mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/reviews/{boardId}", board.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST /reviews/new 닉네임 기반 저장 성공")
  void createBoard_withNickname_success() throws Exception {
    // given: 사용자와 요청 바디
    String email = "creator@test.com";
    userRepository.save(testUtil.newUser(email));

    when(summarizationService.summarize(anyString(), anyString())).thenReturn("요약 본문");

    Map<String, Object> req = new HashMap<>();
    req.put("title", "새 게시글 제목");
    req.put("content", "<p>내용</p><img src=\"https://cdn.example.com/uuid-1111.png\"/>");
    req.put("category", "car");
    req.put("nickname", email.split("@")[0]);

    String json = objectMapper.writeValueAsString(req);

    // when
    MvcResult res = mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/reviews/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andReturn();

    // then
    String body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
    log.info("[CREATE] response: {}", body);
  }

  @Test
  @DisplayName("PATCH /reviews/{id} 닉네임 기반 수정 성공")
  void updateBoard_withNickname_success() throws Exception {
    // given: 기존 게시글 및 사용자
    String email = "owner@test.com";
    Board board = testUtil.newBoard(userRepository.save(testUtil.newUser(email)));
    board = boardRepository.save(board);

    when(summarizationService.summarize(anyString(), anyString())).thenReturn("요약 본문");

    Map<String, Object> req = new HashMap<>();
    req.put("title", "수정된 제목");
    req.put("content", "<p>수정된 내용</p><img src=\"https://cdn.example.com/uuid-2222.jpg\"/>");
    req.put("category", "car");
    req.put("nickname", email.split("@")[0]);

    String json = objectMapper.writeValueAsString(req);

    // when
    mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/reviews/{boardId}", board.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isNoContent());

    // then: DB 반영 확인
    Board updated = boardRepository.findById(board.getId()).orElseThrow();
    org.assertj.core.api.Assertions.assertThat(updated.getTitle()).isEqualTo("수정된 제목");
    org.assertj.core.api.Assertions.assertThat(updated.getNickname()).isEqualTo(email.split("@")[0]);
  }

  @Test
  @DisplayName("/reviews/{id} 잘못된 게시글 잘못들어옴")
  void error_getBoardDetail() throws Exception {
    Board board = testUtil.newBoard(userRepository.save(testUtil.newUser("test@test.com")));
    Board newBoard = boardRepository.save(board);
    Long newBoardId = newBoard.getId();
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/reviews/{Id}", newBoardId + 1)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info(
        "BoardControllerTest.error_getBoardDetail 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));
    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);

  }

  @Test
  @DisplayName("/reviews/{id} 잘못된 게시글 잘못들어옴")
  void error_parameter_getBoardDetail() throws Exception {
    Board board = testUtil.newBoard(userRepository.save(testUtil.newUser("test@test.com")));
    Board newBoard = boardRepository.save(board);
    Long newBoardId = newBoard.getId();
    long startTime = System.nanoTime();
    MvcResult mvcResult = mockMvc.perform(
            get("/reviews/{Id}", "장충동왕족발보쌈")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info(
        "BoardControllerTest.error_parameter_getBoardDetail 실행 시간: {} ns ({} ms)",
        duration, String.format("%.3f", durationMs));
    String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

    Object jsonObject = objectMapper.readValue(responseBody, Object.class);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(jsonObject);

    log.info("Formatted JSON Response:");
    log.info("prettyJson == {}", prettyJson);

  }
}
