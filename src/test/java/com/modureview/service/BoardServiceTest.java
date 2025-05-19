package com.modureview.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.modureview.dto.BoardDetailResponse;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.exception.BoardCustomException;
import com.modureview.repository.BoardRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BoardServiceTest {
  @Autowired
  private BoardService boardService;
  @Autowired
  private BoardRepository boardRepository;
  @Autowired
  ObjectMapper objectMapper;
  @AfterEach
  void cleanUp(){
    boardRepository.deleteAll();
  }

  @Test
  void getBoardDetail_Success() throws Exception {
    Board board = Board.builder()
        .title("Test제목")
        .authorEmail("Test작성자")
        .category(Category.car)
        .content("<h1 style=\\\"text-align: left\\\">이건 제목 1인데요..</h1><p style=\\\"text-align: left\\\">이건 본문 내용 1입니다.. 미리보기 화면 테스트용으로 아무 글이나 작성해보려고 합니다..</p><ul><li><p style=\\\"text-align: left\\\">그래서 이렇게 리스트도 하나 두고요..</p></li><li><p style=\\\"text-align: left\\\">아래에 하나 더 두고요..</p><ul><li><p style=\\\"text-align: left\\\">그리고 이렇게 하위 리스트도 하나 둘게요..</p></li></ul></li></ul><blockquote><p style=\\\"text-align: left\\\">그리고 인용구로 뭔가 있어보이게 적어볼게요..</p></blockquote><p style=\\\"text-align: left\\\">그리고 이렇게 본문 조금 더 쓰는데 이번엔 좀 더 길게 적어서 줄바꿈이 일어나도록 해볼게요.. 그래서 아무 말이나 적고 있습니다.. 줄 바꿈은 일어났는데 조금만 더 적어볼게요..</p><h2 style=\\\"text-align: left\\\">그리고 이렇게 제목 2를 둘게요..</h2><blockquote><p style=\\\"text-align: left\\\">여기는 바로 있어보이게 인용구로 시작할게요..</p></blockquote><p style=\\\"text-align: left\\\">그리고 여기서 잠깐 이 <strong>글자</strong>만 굵게 처리해볼게요.. 그리고 이 <em>글자</em>는 기울일게요.. 그리고 이 <s>글자</s>는 줄을 그어볼게요... 그리고 조금 더 작성하다가 이 <strong><em><s>글자</s></em></strong>는 모두 해볼게요...</p><p style=\\\"text-align: left\\\">그리고 여기는 위와 다르게 숫자 리스트를 둘까요..?</p><ol><li><p style=\\\"text-align: left\\\">여기는 1로 시작하는데요.. 이번엔 하위 리스트를 2개 둘게요..?</p><ol><li><p style=\\\"text-align: left\\\">이렇게.. 그럼 여기도 1로 시작하죠..?</p></li><li><p style=\\\"text-align: left\\\">여기는 2로 시작하네요..?!</p></li></ol></li><li><p style=\\\"text-align: left\\\">그럼 여기는 2로 다시 시작하구요..</p></li></ol><h2 style=\\\"text-align: center\\\">그럼 여기는 제목 3이겠네요..</h2><p style=\\\"text-align: center\\\">여기는 특별히 정렬을 주로 사용해볼게요..</p><p style=\\\"text-align: center\\\">이렇게 가운데에 좀 글을 써보다가..그냥<br>맘대로 줄바꿈도 해보구요...</p><p style=\\\"text-align: right\\\">오른쪽에도 써보고..</p><p style=\\\"text-align: left\\\">왼쪽에도 써볼게요...</p>")
        .commentsCount(13)
        .bookmarksCount(15)
        .build();
    boardRepository.save(board);

    Long boardId = board.getId();

    BoardDetailResponse findBoard = boardService.boardDetail(boardId);

    assertThat(findBoard).isNotNull();
    assertThat(findBoard.author()).isEqualTo(board.getAuthorEmail());
    assertThat(findBoard.category()).isEqualTo(board.getCategory());
    assertThat(findBoard.content()).isEqualTo(board.getContent());
    assertThat(findBoard.title()).isEqualTo(board.getTitle());
    assertThat(findBoard.author()).isEqualTo(board.getAuthorEmail());
    assertThat(findBoard.category()).isEqualTo(board.getCategory());




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
        .content("<h1 style=\\\"text-align: left\\\">이건 제목 1인데요..</h1><p style=\\\"text-align: left\\\">이건 본문 내용 1입니다.. 미리보기 화면 테스트용으로 아무 글이나 작성해보려고 합니다..</p><ul><li><p style=\\\"text-align: left\\\">그래서 이렇게 리스트도 하나 두고요..</p></li><li><p style=\\\"text-align: left\\\">아래에 하나 더 두고요..</p><ul><li><p style=\\\"text-align: left\\\">그리고 이렇게 하위 리스트도 하나 둘게요..</p></li></ul></li></ul><blockquote><p style=\\\"text-align: left\\\">그리고 인용구로 뭔가 있어보이게 적어볼게요..</p></blockquote><p style=\\\"text-align: left\\\">그리고 이렇게 본문 조금 더 쓰는데 이번엔 좀 더 길게 적어서 줄바꿈이 일어나도록 해볼게요.. 그래서 아무 말이나 적고 있습니다.. 줄 바꿈은 일어났는데 조금만 더 적어볼게요..</p><h2 style=\\\"text-align: left\\\">그리고 이렇게 제목 2를 둘게요..</h2><blockquote><p style=\\\"text-align: left\\\">여기는 바로 있어보이게 인용구로 시작할게요..</p></blockquote><p style=\\\"text-align: left\\\">그리고 여기서 잠깐 이 <strong>글자</strong>만 굵게 처리해볼게요.. 그리고 이 <em>글자</em>는 기울일게요.. 그리고 이 <s>글자</s>는 줄을 그어볼게요... 그리고 조금 더 작성하다가 이 <strong><em><s>글자</s></em></strong>는 모두 해볼게요...</p><p style=\\\"text-align: left\\\">그리고 여기는 위와 다르게 숫자 리스트를 둘까요..?</p><ol><li><p style=\\\"text-align: left\\\">여기는 1로 시작하는데요.. 이번엔 하위 리스트를 2개 둘게요..?</p><ol><li><p style=\\\"text-align: left\\\">이렇게.. 그럼 여기도 1로 시작하죠..?</p></li><li><p style=\\\"text-align: left\\\">여기는 2로 시작하네요..?!</p></li></ol></li><li><p style=\\\"text-align: left\\\">그럼 여기는 2로 다시 시작하구요..</p></li></ol><h2 style=\\\"text-align: center\\\">그럼 여기는 제목 3이겠네요..</h2><p style=\\\"text-align: center\\\">여기는 특별히 정렬을 주로 사용해볼게요..</p><p style=\\\"text-align: center\\\">이렇게 가운데에 좀 글을 써보다가..그냥<br>맘대로 줄바꿈도 해보구요...</p><p style=\\\"text-align: right\\\">오른쪽에도 써보고..</p><p style=\\\"text-align: left\\\">왼쪽에도 써볼게요...</p>")
        .commentsCount(13)
        .bookmarksCount(15)
        .build();
    boardRepository.save(board);

    Long boardId = board.getId();
    BoardCustomException ex= assertThrows(
        BoardCustomException.class,()->
            boardService.boardDetail(boardId+1L)
    );

    assertThat(ex.getMessage()).isEqualTo("게시글을 찾을 수 없습니다.");


    String errorJson = objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(Map.of("error",ex.getClass().getSimpleName(),
            "message",ex.getMessage()));
    log.info("errorJson == {}", errorJson);


  }

}