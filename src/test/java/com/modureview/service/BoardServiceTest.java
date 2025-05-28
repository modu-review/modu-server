package com.modureview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.modureview.dto.BoardDetailResponse;
import com.modureview.dto.request.BoardSaveRequest;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.BoardError.NotAllowedHtmlError;
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
@ActiveProfiles("test")
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

    log.info("boardService.boardDetail() 실행 시간: {} ns ({} ms)", duration, String.format("%.3f", durationMs));


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

    log.info("boardService.boardDetail() (에러 발생) 실행 시간: {} ns ({} ms)", duration, String.format("%.3f", durationMs));


    assertThat(ex.getMessage()).isEqualTo("게시글을 찾을 수 없습니다.");

    String errorJson = objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(Map.of("error",ex.getClass().getSimpleName(),
            "message",ex.getMessage()));
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

  @Test
  @DisplayName("xss가 없는 html코드")
  void xssCheckSuccess() {
    // given: 정상적인 HTML
    String safeHtml = """  
        <h2>서울 근처 주말 여행지 추천</h2>
        
        <p>요즘 날씨가 좋아서 주말마다 짧은 여행을 다녀오고 있어요! 이번 주에는 <strong>남산서울타워</strong>를 다녀왔습니다.</p>
        
        <p>걷는 길도 잘 되어 있고, <a href="https://www.seoultower.co.kr">공식 웹사이트</a>에서 미리 정보를 확인하고 가시면 편해요.</p>
        
        <ul>
          <li>대중교통 이용 가능</li>
          <li>야경이 정말 예쁨</li>
          <li>근처 맛집도 많음 (특히 닭갈비!)</li>
        </ul>
        
        <blockquote>
          <p>“도시에서 가까우면서도 힐링할 수 있는 곳이에요!”</p>
        </blockquote>
        
        <p>사진은 아래에 첨부할게요. 혹시 다른 추천 여행지가 있다면 댓글로 알려주세요 :)</p>
        
        <img src="https://example.com/seoul-tower.jpg" alt="서울타워 사진">
        
        <hr>
        
        <pre><code>추천 일정:
        - 오전: 남산 산책
        - 오후: 전망대 관람
        </code></pre>
        
        """;
    BoardSaveRequest request = new BoardSaveRequest("Title", safeHtml, "user@example.com", "food");

    // when & then
    assertDoesNotThrow(() -> boardService.htmlSanitizer(request));
  }

  @Test
  @DisplayName("xss가 주입된 공격 코드")
  void xssCheckFailure() {
    // given: XSS 포함 HTML
    String maliciousHtml = """
        <div>        <h2>서울 근처 주말 여행지 추천</h2>
        <p>요즘 날씨가 좋아서 주말마다 짧은 여행을 다녀오고 있어요! 이번 주에는 <strong>남산서울타워</strong>를 다녀왔습니다.</p>
        <p>걷는 길도 잘 되어 있고, <a href="https://www.seoultower.co.kr">공식 웹사이트</a>에서 미리 정보를 확인하고 가시면 편해요.</p>
        <ul>          <li>대중교통 이용 가능</li>
        <li>야경이 정말 예쁨</li>
        <li>근처 맛집도 많음 (특히 닭갈비!)</li>
        </ul>        <p>사진은 아래에 첨부할게요. 혹시 다른 추천 여행지가 있다면 댓글로 알려주세요 :)</p>        <script>alert('XSS 공격입니다. 쿠키 탈취 시도!');</script>
        <img src="https://example.com/seoul-tower.jpg" alt="서울타워 사진" />
        </div>      """;
    BoardSaveRequest request = new BoardSaveRequest("Title", maliciousHtml, "user@example.com",
        "food");

    // when & then
    NotAllowedHtmlError ex = assertThrows(NotAllowedHtmlError.class, () ->
        boardService.htmlSanitizer(request));

    assertEquals(BoardErrorCode.NOT_ALLOWED_HTML_ERROR, ex.getErrorCode());
  }


}
