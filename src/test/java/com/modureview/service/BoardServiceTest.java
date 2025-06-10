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
import com.modureview.entity.User;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.BoardError.NotAllowedHtmlError;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class BoardServiceTest {

  @Autowired
  private BoardService boardService;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;


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

  @Test
  @DisplayName("게시글 저장 통합 테스트 - 이미지 포함")
  void saveBoardWithImages() {
    // given
    String html = """
            <p>본문입니다</p>
            <img src="https://cdn.example.com/uuid1-aaaa.jpg" />
            <img src="https://cdn.example.com/uuid2-bbbb.png" />
        """;

    BoardSaveRequest request = new BoardSaveRequest(
        "통합 테스트 제목",
        html,
        "food", // enum이 대문자여야 from() 매칭됨
        "author@test.com"
    );

    List<String> uuids = boardService.extractImageInfo(request);

    // when
    Board newboard = boardService.saveBoard(request, uuids);
    log.info("newboard.getPreview() == {}", newboard.getPreview());
  }

  @Test
  @DisplayName("게시글 저장 통합 테스트 - 이미지 포함,Preview도 포함")
  void saveBoardWithImagesWithPreview() {
    // given
    String html = """
        작년 8월부터 공사하던
        
        점선 에스프레소바를 보면서
        근처에 집과 회사가 있는 나는
        
        얼른 생기길 기다리고있었다 💨
        
        인스타로 간간히 소식을 훑어보던 중
        
        오픈한것같은 느낌이 들어 방문했더니
        
        드디어 가오픈을 한다고 했다 !!!!
        
        청주 강서동에 진짜 까눌레랑
        
        맛있는 마들렌 휘낭시에 파는 집이 없는데
        
        드디어 생겨서 행복했다
        
        갓 오픈했다고 엉성한게 아니라
        
        까눌레는 타지도 않고 겉바속촉에
        
        너무너무 행복한 맛이였다
        
        우리는 레몬 커스타드 마들렌, 까눌레,\s
        
        무화과 크림치즈 휘낭시에를 주문했다
        
        에스프레소도 3천원으로 생각보다 저렴했고
        
        원두가 산미없이 고소한게 있어서
        
        너무너무 좋았다
        
        !! 사장님 디카페인 원두도 추가해주세요 !!
        
        더 맨날갈게요 제발 ㅠㅠㅠㅠㅠㅠ
        내부는 크지 않지만
        
        청주 에스프레소바 치고 자리도 ㄱㅊ고
        
        들어가기 편한 공간이였다
        아치형 바테이블도 있는데
        
        여기서 핸드드리부구경하고싶다ㅜ,,,,
        공간 하나하나 갬성 개쩔고
        
        가격도 비싸지 않아서
        
        자주자주 갈 예정이다 !!
        
        저 요시고 포토카드북 탐난다,,,,🤍
        
        스탠딩 테이블은 한개밖에 없어서
        
        한번 서서 마셔볼까? 하다가
        
        포기 ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ
        이런거 너무 좋아 ~~~❤️❤️❤️🤍🤍
        
        이 의자는 앉거나 디저트 올려놓거나
        
        만능으로 사용 가능하답니다 ㅋㅋ
        
        사진 너무 잘나와~~~~
        
        재방문 포스팅 조만간 갑니다
        
        진짜 3일에 1번씩 갈듯
        이것도 짐이나 가방 놓는 용도로
        
        있으니깐 편하더라구요
        좌식은 다 이런편 !
        
        생각보다 오래앉아있을수는 없는 구조,,, ㅎㅎㅎ
        
        방석 주세욥 !
        생각보다 금방나온
        
        우리의 쇼콜라어쩌구!!!
        인스타에서 다른 청주 에스프레소바 사진보면
        
        엄청 디럽게 나오던데
        
        점선에스프레소바는 비교적 깔끔하게 나와서
        
        좋았다 ㅋㅋ
        응 갬성 개쩔어~
        
        우리는 피스타치오1 소콜라 2
        이렇게 주문했다
        이건 우리의 디저트
        
        맛평가는,,,,,,,!
        내 픽 순위는 !!
        1. 레몬커스타드마들렌
        
        촉촉하고 상큼하니 와,,, 내가 먹은 마들렌 원탑
        
        ​
        
        2. 까눌레
        
        겉은 바삭하지만 타지않고
        
        속이 촉촉하니 바닐라향 가득한
        
        잘 만들어진 까눌레
        3. 무화과 크림치즈 휘낭시에
        너무 맛있었지만 그 휘낭시에의 촉촉 쫀득한
        맛보다는 오히려 마들렌같은 파사삭한 느낌 ..?
        살짝 더 쫀쫀했으며누맛있을것같다
        저 씨쏠트 초콜릿도 존맛 ㅋ
        주문서가 이렇게 나왔다
        귀엽고 갬성쩔어
        이건 피스타치오 에스프레소 어쩌구
        크림이 있는거라
        엄청 달달한 피스타치오커피맛이였다
        여기 점선 에스프레소바는 크레마 예술이다 진짜
        다들 사진찍느라ㅜ바쁨 ㅋㅋㅋ
        확대샷,,,,,
        이건 무조건이지 ~
        에스프레소바 오면 무조건 찍어야하는 국룰 ~~~
        ㅋㅋㅋㅋㅋ참고로
        내가 쌓음 v
        
        청주 에스프레소바 점선
        
        진짜 커피맛집 커피존맛 !!!!
        
        강서동에 있어서 청주터미널이랑도 매우 가깝고
        
        진짜 자주올 단골예약할 카페이다!!!
        
        별이 다섯개!!!
        
        """;

    User user = User.builder()
        .email("author@test.com")
        .build();

    User newUser = userRepository.save(user);

    BoardSaveRequest request = new BoardSaveRequest(
        "구움과자까지 존맛인 청주 에스프레소바 점선",
        html,
        "food", // enum이 대문자여야 from() 매칭됨
        newUser.getEmail()
    );

    List<String> uuids = boardService.extractImageInfo(request);

    // when
    Board newboard = boardService.saveBoard(request, uuids);
    log.info("newboard.getPreview() == {}", newboard.getPreview());
  }


}
