package com.modureview.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.modureview.entity.Board;
import com.modureview.entity.User;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Test
  void getBoardDetail_success() throws Exception {
    
    User user = userRepository.save(
        User.builder()
            .email("test@example.com")
            .build()
    );

    Board board = boardRepository.save(
        Board.builder()
            .category("General")
            .userId(user.getId())
            .title("테스트 제목")
            .content("테스트 내용")
            .viewCount(100L)
            .stars(5)
            .build()
    );
    Long targetId = board.getId();


    mockMvc.perform(get("/board/" + targetId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("테스트 제목"))
        .andExpect(jsonPath("$.content").value("테스트 내용"))
        .andExpect(jsonPath("$.user").value("test@example.com"))
        .andExpect(jsonPath("$.category").value("General"))
        .andExpect(jsonPath("$.viewCount").value(101))
        .andExpect(jsonPath("$.stars").value(5))
        .andExpect(jsonPath("$.createdAt").exists())
        .andDo(result ->
            System.out.println("성공 응답 바디: " +
                result.getResponse().getContentAsString())
        );
  }

  @Test
  void getBoardDetail_notFound() throws Exception {

    long nonExistingId = 9999L;


    mockMvc.perform(get("/board/" + nonExistingId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(result ->
            System.out.println("실패 응답 바디: "
                + result.getResponse().getContentAsString())
        );
  }
}