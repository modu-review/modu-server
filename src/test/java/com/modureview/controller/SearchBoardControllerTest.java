package com.modureview.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.entity.Board;
import com.modureview.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class SearchBoardControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    boardRepository.deleteAll();
    boardRepository.saveAll(List.of(
        // 1) 이 9개 중 8개는 "테스트"로 매치, 1개는 불일치
        Board.builder().title("Error제목1").content("<h1>테스트</h1>").category("커피").viewCount(100L).email("user1@test.com").build(),
        Board.builder().title("Error제목2").content("<h2>테스트2</h2>").category("커피").viewCount(200L).email("user2@test.com").build(),
        Board.builder().title("Error제목3").content("<h1>테스트3</h1>").category("커피").viewCount(150L).email("user3@test.com").build(),
        Board.builder().title("테스트 제목4").content("<h1>Error</h1>").category("커피").viewCount(120L).email("user4@test.com").build(),
        Board.builder().title("테스트 제목5").content("<h1>error</h1>").category("커피").viewCount(130L).email("user5@test.com").build(),
        Board.builder().title("error").content("<h1>error</h1>").category("커피").viewCount(140L).email("error6@test.com").build(), // 불일치
        Board.builder().title("error").content("<h1>error</h1>").category("커피").viewCount(110L).email("테스트7@test.com").build(),
        Board.builder().title("error").content("<h1>error</h1>").category("커피").viewCount(115L).email("테스트8@test.com").build(),
        Board.builder().title("error").content("<h1>error</h1>").category("커피").viewCount(118L).email("테스트9@test.com").build()
    ));
  }

  @Test
  @DisplayName("GET /search → JSON 응답을 ObjectMapper로 예쁘게 출력하며 200 OK 검증")
  void searchBoard_prettyPrintJsonAndVerify() throws Exception {
    // given
    String keyword   = "테스트";
    int    page      = 0;
    String sortBy    = "id";
    Sort.Direction direction = Sort.Direction.DESC;

    // when: /search 호출 (필터 비활성화 했으니 인증 없이 200 OK)
    MvcResult mvcResult = mockMvc.perform(get("/search")
            .param("keyword",  keyword)
            .param("page",     String.valueOf(page))
            .param("sortBy",   sortBy)
            .param("direction",direction.name())
            .accept(MediaType.APPLICATION_JSON)
        )
        // 쭉 서버 로그+스택트레이스 보고 싶으면 아래 .andDo(print()) 추가
        // .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andReturn();

    // then: JSON 응답을 String으로 꺼내서 예쁘게 출력
    String rawJson = mvcResult.getResponse().getContentAsString();
    System.out.println("===== RAW JSON RESPONSE =====");
    System.out.println(rawJson);
    System.out.println("===== PRETTY JSON RESPONSE =====");
    System.out.println(
        objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(
                objectMapper.readValue(rawJson, Object.class)
            )
    );
  }
}