package com.modureview.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.modureview.Entity.User;
import com.modureview.Repository.UserRepository;
import com.modureview.Service.CustomOAuth2UserService;
import com.modureview.Service.UserService;

import com.modureview.Service.Utill.JwtTokenizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OAuth2UserController.class)
@Import(OAuth2UserControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 비활성화
public class OAuth2UserControllerTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
      return Mockito.mock(CustomOAuth2UserService.class);
    }

    @Bean
    public UserService userService() {
      return Mockito.mock(UserService.class);
    }

    @Bean
    public UserRepository userRepository() {
      return Mockito.mock(UserRepository.class);
    }

    @Bean
    public JwtTokenizer jwtTokenizer() {
      return Mockito.mock(JwtTokenizer.class);
    }
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  // ObjectMapper의 빈 DTO 직렬화 문제 방지 옵션
  @Autowired
  public void configureObjectMapper(ObjectMapper mapper) {
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  @Autowired
  private UserService userService;

  @Autowired
  private JwtTokenizer jwtTokenizer;

  @Test
  @DisplayName("GET /user/oauth2/login - 유효한 사용자 이메일")
  public void testOauth2UserLogin_ValidEmail() throws Exception {
    String userEmail = "valid@example.com";
    User dummyUser = User.builder()
        .email(userEmail)
        .build();

    // UserService.getUserByEmail()가 dummyUser 반환
    when(userService.getUserByEmail(userEmail)).thenReturn(dummyUser);

    // jwtTokenizer.reissueTokenPair()는 doNothing으로 처리
    doNothing().when(jwtTokenizer).reissueTokenPair(any(HttpServletResponse.class), eq(dummyUser));

    mockMvc.perform(get("/user/oauth2/login")
            .param("user_email", userEmail)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(userEmail));
  }

  @Test
  @DisplayName("GET /user/oauth2/login - 잘못된 사용자 이메일")
  public void testOauth2UserLogin_InvalidEmail() throws Exception {
    String userEmail = "invalid@example.com";
    // UserService.getUserByEmail()가 예외를 던지도록 처리
    when(userService.getUserByEmail(userEmail)).thenThrow(new RuntimeException("User not found"));

    mockMvc.perform(get("/user/oauth2/login")
            .param("user_email", userEmail)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid user email"));
  }
}