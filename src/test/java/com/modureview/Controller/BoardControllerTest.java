package com.modureview.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.modureview.Dto.Board.Request.SearchBoardDataRequestDto;
import com.modureview.Dto.Board.Request.WriteBoardRequestDto;

import com.modureview.Dto.Board.Response.ListBoardResponseDto;
import com.modureview.Dto.Board.Response.WriteBoardResponseDto;
import com.modureview.Service.BoardService;
import com.modureview.Service.Utill.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@Import(BoardControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 비활성화
public class BoardControllerTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public BoardService boardService() {
      return Mockito.mock(BoardService.class);
    }
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  // ObjectMapper의 빈 DTO 직렬화 문제를 방지하기 위해 FAIL_ON_EMPTY_BEANS 옵션 비활성화
  @Autowired
  public void configureObjectMapper(ObjectMapper mapper) {
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  @Autowired
  private BoardService boardService;

  // 인증 정보를 위한 CustomUserDetails (이메일만 사용)
  private CustomUserDetails getCustomUserDetails() {
    return new CustomUserDetails("test@example.com");
  }

  @Test
  @DisplayName("GET /api/v0/Board/list - 전체 게시글 목록 조회")
  public void testListAllBoard() throws Exception {
    ListBoardResponseDto listDto = ListBoardResponseDto.builder()
        .Id(1L)
        .title("Test Title")
        .content("Test Content")
        .writerName("test@example.com")
        .build();
    PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
    var page = new PageImpl<>(Arrays.asList(listDto), pageable, 1);

    Mockito.when(boardService.get_AllBoard(any(PageRequest.class))).thenReturn(page);

    mockMvc.perform(get("/api/v0/Board/list")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").value("Test Title"));
  }

  @Test
  @DisplayName("GET /api/v0/Board/list/{catgory} - 카테고리별 게시글 목록 조회")
  public void testListCategoryBoard() throws Exception {
    ListBoardResponseDto listDto = ListBoardResponseDto.builder()
        .Id(1L)
        .title("Category Title")
        .content("Category Content")
        .writerName("test@example.com")
        .build();
    PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
    var page = new PageImpl<>(Arrays.asList(listDto), pageable, 1);

    Mockito.when(boardService.get_All_Board_Category(any(PageRequest.class), any(String.class)))
        .thenReturn(page);

    mockMvc.perform(get("/api/v0/Board/list/A")
            .param("Category", "A")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").value("Category Title"));
  }

  @Test
  @DisplayName("POST /api/v0/Board/write - 게시글 작성")
  public void testWriteBoard() throws Exception {
    WriteBoardRequestDto requestDto = WriteBoardRequestDto.builder()
        .title("New Title")
        .content("New Content")
        .build();
    WriteBoardResponseDto responseDto = WriteBoardResponseDto.builder()
        .Id(1L)
        .title("New Title")
        .content("New Content")
        .build();

    Mockito.when(boardService.write_Board(any(WriteBoardRequestDto.class), any(CustomUserDetails.class)))
        .thenReturn(responseDto);

    mockMvc.perform(post("/api/v0/Board/write")
            .with(SecurityMockMvcRequestPostProcessors.user(getCustomUserDetails()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("New Title"));
  }

  @Test
  @DisplayName("GET /api/v0/Board/search - 게시글 검색")
  public void testSearchBoard() throws Exception {
    SearchBoardDataRequestDto searchDto = SearchBoardDataRequestDto.createSearchData("Test", "", "");
    ListBoardResponseDto listDto = ListBoardResponseDto.builder()
        .Id(1L)
        .title("Test Title")
        .content("Test Content")
        .writerName("test@example.com")
        .build();
    PageRequest pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
    var page = new PageImpl<>(Arrays.asList(listDto), pageable, 1);

    Mockito.when(boardService.search_Board(any(SearchBoardDataRequestDto.class), any(PageRequest.class)))
        .thenReturn(page);

    mockMvc.perform(get("/api/v0/Board/search")
            .param("title", "Test")
            .param("content", "")
            .param("writerName", "")
            .param("page", "0")
            .param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").value("Test Title"));
  }

  @Test
  @DisplayName("DELETE /api/v0/Board/{BoardId}/delete - 게시글 삭제")
  public void testDeleteBoard() throws Exception {
    // anyLong() 매처를 사용하여 BoardId가 1L일 때 1L이 반환되도록 설정
    Mockito.when(boardService.delete_Board(anyLong(), any(CustomUserDetails.class)))
        .thenReturn(1L);

    mockMvc.perform(delete("/api/v0/Board/1/delete")
            .with(SecurityMockMvcRequestPostProcessors.user(getCustomUserDetails()))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(1));
  }
}