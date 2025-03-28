package com.modureview.Controller;

import static org.junit.jupiter.api.Assertions.*;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.modureview.Entity.User;
import com.modureview.Repository.UserRepository;
import com.modureview.Service.Utill.JwtTokenizer;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;
import java.util.Collections;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RefreshTokenController.class)
@Import(RefreshTokenControllerTest.TestConfig.class)
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 비활성화
public class RefreshTokenControllerTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public JwtTokenizer jwtTokenizer() {
      return Mockito.mock(JwtTokenizer.class);
    }
    @Bean
    public UserRepository userRepository() {
      return Mockito.mock(UserRepository.class);
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
  private JwtTokenizer jwtTokenizer;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("GET /api/v0/token/refresh - 유효한 refreshToken인 경우")
  public void testRefreshToken_Valid() throws Exception {
    String refreshToken = "dummyRefreshToken";

    // jwtTokenizer.validateToken(refreshToken)가 true 반환
    when(jwtTokenizer.validateToken(refreshToken)).thenReturn(true);

    // dummy Claims 생성 및 subject("valid@example.com") 반환 설정
    Claims dummyClaims = Mockito.mock(Claims.class);
    when(dummyClaims.getSubject()).thenReturn("valid@example.com");
    when(jwtTokenizer.parseRefreshToken(refreshToken)).thenReturn(dummyClaims);

    // dummy User 생성
    User dummyUser = User.builder().email("valid@example.com").build();
    when(userRepository.findByEmail("valid@example.com")).thenReturn(Optional.of(dummyUser));

    // reissueTokenPair는 아무 동작 없이 호출
    doNothing().when(jwtTokenizer).reissueTokenPair(any(HttpServletResponse.class), eq(dummyUser));

    mockMvc.perform(get("/api/v0/token/refresh")
            .cookie(new Cookie("refreshToken", refreshToken))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().string("access token refreshed successfully"));
  }

  @Test
  @DisplayName("GET /api/v0/token/refresh - 유효하지 않은 refreshToken인 경우")
  public void testRefreshToken_Invalid() throws Exception {
    String refreshToken = "dummyInvalidRefreshToken";

    // jwtTokenizer.validateToken(refreshToken)가 false 반환
    when(jwtTokenizer.validateToken(refreshToken)).thenReturn(false);

    mockMvc.perform(get("/api/v0/token/refresh")
            .cookie(new Cookie("refreshToken", refreshToken))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}