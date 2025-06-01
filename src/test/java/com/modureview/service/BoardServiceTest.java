package com.modureview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.modureview.dto.BoardDetailResponse;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardRepository;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("h2")
@AutoConfigureMockMvc
class BoardServiceTest {

  @Autowired
  private BoardService boardService;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @AfterEach
  void cleanUp() {
    boardRepository.deleteAll();
  }

  @Test
  void getBoardDetail_Success() throws Exception {
    Board board = Board.builder()
        .title("Test제목")
        .authorEmail("Test작성자")
        .category(Category.car)
        .content("<h1 style=\\\"text-align: left\\\">이건 제목 1인데요..</h1><p>...</p>") // 내용은 생략
        .commentsCount(13)
        .bookmarksCount(15)
        .build();
    boardRepository.save(board);

    Long boardId = board.getId();

    // 시간 측정 시작
    long startTime = System.nanoTime();

    BoardDetailResponse findBoard = boardService.boardDetail(boardId);

    // 시간 측정 종료
    long endTime = System.nanoTime();
    long duration = (endTime - startTime); // 나노초 단위
    double durationMs = duration / 1_000_000.0; // 밀리초 단위

    log.info("boardService.boardDetail() 실행 시간: {} ns ({} ms)", duration,
        String.format("%.3f", durationMs));

    assertThat(findBoard).isNotNull();
    assertThat(findBoard.author()).isEqualTo(board.getAuthorEmail());
    assertThat(findBoard.category()).isEqualTo(board.getCategory());
    assertThat(findBoard.content()).isEqualTo(board.getContent());
    assertThat(findBoard.title()).isEqualTo(board.getTitle());
    // 중복된 검증 제거
    // assertThat(findBoard.author()).isEqualTo(board.getAuthorEmail());
    // assertThat(findBoard.category()).isEqualTo(board.getCategory());

    String realJson = objectMapper.
        enable(SerializationFeature.INDENT_OUTPUT)
        .writeValueAsString(findBoard);
    log.info("realJson == {}", realJson);
  }

  @Test
  void getBoardDetail_Error() throws Exception {
    Board board = Board.builder()
        .title("Error제목")
        .authorEmail("Error작성자")
        .category(Category.car)
        .content("<h1 style=\\\"text-align: left\\\">이건 제목 1인데요..</h1><p>...</p>") // 내용은 생략
        .commentsCount(13)
        .bookmarksCount(15)
        .build();
    boardRepository.save(board);

    Long boardId = board.getId();

    // 시간 측정 (에러 케이스지만, 필요하다면 동일하게 측정 가능)
    long startTime = System.nanoTime();

    CustomException ex = assertThrows(
        CustomException.class, () ->
            boardService.boardDetail(boardId + 1L)
    );

    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;

    log.info("boardService.boardDetail() (에러 발생) 실행 시간: {} ns ({} ms)", duration,
        String.format("%.3f", durationMs));

    assertThat(ex.getMessage()).isEqualTo("게시글을 찾을 수 없습니다.");

    String errorJson = objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(Map.of("error", ex.getClass().getSimpleName(),
            "message", ex.getMessage()));
    log.info("errorJson == {}", errorJson);
  }

  @DisplayName("S3 Presigned URL 생성 성공 테스트")
  @Test
  void createPresignedUrl_success() throws Exception {
    // given
    String keyJson = objectMapper.writeValueAsString(Map.of("fileType", "png"));

    // when & then
    mockMvc.perform(post("/presign")
            .content(keyJson)
            .contentType("application/json"))
        .andExpect(status().isOk())
        .andDo(print());
  }
}
