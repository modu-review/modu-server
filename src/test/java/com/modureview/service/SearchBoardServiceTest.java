package com.modureview.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.Response.SearchResponseDto;
import com.modureview.entity.Board;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.SearchBoardRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SearchBoardServiceTest {

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private SearchBoardRepository searchBoardRepository;

  @Autowired
  private SearchBoardService searchBoardService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {

    boardRepository.deleteAll();
  }

  @Test
  @DisplayName("검색 키워드로 필터링된 Page<SearchResponseDto>를 반환한다")
  void search_keyword_filtersByKeyword() throws Exception {

    int page = 0;
    String sortBy = "id";
    Sort.Direction direction = Sort.Direction.DESC;
    String keyword = "테스트";


    List<Board> toSave = List.of(
        Board.builder().title("Error제목1").content("<h1>테스트</h1>").category("커피").viewCount(100L).email("user1@test.com").build(),
        Board.builder().title("Error제목2").content("<h2>테스트2</h2>").category("커피").viewCount(200L).email("user2@test.com").build(),
        Board.builder().title("Error제목3").content("<h1>테스트3</h1>").category("커피").viewCount(150L).email("user3@test.com").build(),
        Board.builder().title("테스트 제목4").content("<h1>Error</h1>").category("커피").viewCount(120L).email("user4@test.com").build(),
        Board.builder().title("테스트 제목5").content("<h1>error</h1>").category("커피").viewCount(130L).email("user5@test.com").build(),
        Board.builder().title("error").content("<h1>error</h1>").category("커피").viewCount(140L).email("error6@test.com").build(),  // 불일치
        Board.builder().title("error").content("<h1>error</h1>").category("커피").viewCount(110L).email("테스트7@test.com").build(),
        Board.builder().title("error").content("<h1>error</h1>").category("커피").viewCount(115L).email("테스트8@test.com").build(),
        Board.builder().title("error").content("<h1>error</h1>").category("커피").viewCount(118L).email("테스트9@test.com").build()
    );

    toSave.forEach(boardRepository::save);


    Page<SearchResponseDto> result = searchBoardService
        .search_keyword(page, sortBy, direction, keyword);


    assertThat(result.getTotalElements()).isEqualTo(8);




    // 결과 출력 (선택)
    System.out.println("============== 테스트 결과 ==============");
    System.out.println(objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(result.getContent()));
    System.out.println("============== 테스트 결과 ==============");
  }
}