package com.modureview.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.BoardDetailResponse;
import com.modureview.entity.Board;
import com.modureview.entity.User;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@SpringBootTest
@Transactional
class BoardServiceTest {

  @Autowired
  private BoardService boardService;
  @Autowired
  private BoardRepository boardRepository;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  public void setup(){

  }

  @Test
  @DisplayName("BoardDetailTest")
  void getBoardDetailTest() throws Exception {
    User user = User.builder()
        .email("test@test.com")
        .build();

    User Testuser = userRepository.save(user);

    Board board = Board.builder()
        .title("test title")
        .content("<h1 style=\"text-align: left\">이건 제목 1인데요..</h1><p style=\"text-align: left\">이건 본문 내용 1입니다.. 미리보기 화면 테스트용으로 아무 글이나 작성해보려고 합니다..</p><ul><li><p style=\"text-align: left\">그래서 이렇게 리스트도 하나 두고요..</p></li><li><p style=\"text-align: left\">아래에 하나 더 두고요..</p><ul><li><p style=\"text-align: left\">그리고 이렇게 하위 리스트도 하나 둘게요..</p></li></ul></li></ul><blockquote><p style=\"text-align: left\">그리고 인용구로 뭔가 있어보이게 적어볼게요..</p></blockquote><p style=\"text-align: left\">그리고 이렇게 본문 조금 더 쓰는데 이번엔 좀 더 길게 적어서 줄바꿈이 일어나도록 해볼게요.. 그래서 아무 말이나 적고 있습니다.. 줄 바꿈은 일어났는데 조금만 더 적어볼게요..</p><h2 style=\"text-align: left\">그리고 이렇게 제목 2를 둘게요..</h2><blockquote><p style=\"text-align: left\">여기는 바로 있어보이게 인용구로 시작할게요..</p></blockquote><p style=\"text-align: left\">그리고 여기서 잠깐 이 <strong>글자</strong>만 굵게 처리해볼게요.. 그리고 이 <em>글자</em>는 기울일게요.. 그리고 이 <s>글자</s>는 줄을 그어볼게요... 그리고 조금 더 작성하다가 이 <strong><em><s>글자</s></em></strong>는 모두 해볼게요...</p><p style=\"text-align: left\">그리고 여기는 위와 다르게 숫자 리스트를 둘까요..?</p><ol><li><p style=\"text-align: left\">여기는 1로 시작하는데요.. 이번엔 하위 리스트를 2개 둘게요..?</p><ol><li><p style=\"text-align: left\">이렇게.. 그럼 여기도 1로 시작하죠..?</p></li><li><p style=\"text-align: left\">여기는 2로 시작하네요..?!</p></li></ol></li><li><p style=\"text-align: left\">그럼 여기는 2로 다시 시작하구요..</p></li></ol><h2 style=\"text-align: center\">그럼 여기는 제목 3이겠네요..</h2><p style=\"text-align: center\">여기는 특별히 정렬을 주로 사용해볼게요..</p><p style=\"text-align: center\">이렇게 가운데에 좀 글을 써보다가..그냥<br>맘대로 줄바꿈도 해보구요...</p><p style=\"text-align: right\">오른쪽에도 써보고..</p><p style=\"text-align: left\">왼쪽에도 써볼게요...</p>")
        .category("아메리카노")
        .viewCount(0L)
        .stars(1)
        .userId(Testuser.getId())
        .build();

    Board save = boardRepository.save(board);

    Long TargetBoardID = save.getId();
    System.out.println(TargetBoardID);

    BoardDetailResponse boardDetailResponse = boardService.boardDetail(TargetBoardID);

    String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(boardDetailResponse);
    System.out.println(jsonResult);
  }

  @Test
  @DisplayName("잘못된 BoardID 가져오기")
  void Error_getBoardByIdTest() {
    // 1) User 저장
    User user = User.builder()
        .email("test@test.com")
        .build();
    User savedUser = userRepository.save(user);

    // 2) Board 저장
    Board board = Board.builder()
        .title("test title")
        .content("본문")
        .category("아메리카노")
        .viewCount(0L)
        .stars(1)
        .userId(savedUser.getId())
        .build();
    Board savedBoard = boardRepository.save(board);

    // 3) 존재하지 않는 ID
    long invalidId = savedBoard.getId() + 10L;

    // when & then: ResponseStatusException 을 던지는지 검증
    ResponseStatusException ex = assertThrows(
        ResponseStatusException.class,
        () -> boardService.boardDetail(invalidId)
    );

    // then: 404 상태 코드와 메시지 검증
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    assertEquals(
        "게시글(id=" + invalidId + ")을 찾을 수 없습니다.",
        ex.getReason()
    );

    System.out.println("🌟 발생한 예외 메시지: " + ex.getReason());
  }
  @Test
  @DisplayName("잘못된 UserID 가져오기")
  void Error_getBoardById_userEmail() {
    // 1) User 먼저 저장
    User user = User.builder()
        .email("test@test.com")
        .build();
    User savedUser = userRepository.save(user);

    // 2) Board는 유효한 userId 로 저장
    Board board = Board.builder()
        .title("test title")
        .content("본문")
        .category("아메리카노")
        .viewCount(0L)
        .stars(1)
        .userId(savedUser.getId())
        .build();
    Board savedBoard = boardRepository.save(board);

    // 3) 이제 그 User를 삭제해서, service 호출 시 ResponseStatusException 발생
    userRepository.deleteById(savedUser.getId());

    // when & then: ResponseStatusException을 던지는지 검증
    ResponseStatusException ex = assertThrows(
        ResponseStatusException.class,
        () -> boardService.boardDetail(savedBoard.getId())
    );

// 잘못된 getStatus() 대신 getStatusCode() 사용
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    assertEquals(
        "유저아이디(id=" + savedUser.getId() + ")가 존재하지 않습니다.",
        ex.getReason()
    );

    System.out.println("🌟 발생한 예외 메시지: " + ex.getReason());
  }
}