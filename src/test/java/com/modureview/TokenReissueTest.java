package com.modureview;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.modureview.entity.User;
import com.modureview.repository.UserRepository;
import com.modureview.service.utill.JwtTokenizer;
import com.nimbusds.jwt.JWT;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TokenReissueTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtTokenizer jwtTokenizer;

  @Autowired
  private UserRepository userRepository;

  private String refreshToken;
  private String expiredAccessToken;

  @BeforeEach
  void setup(){
    User user = User.builder()
        .email("test@example.com")
        .build();
    userRepository.save(user);

    expiredAccessToken = jwtTokenizer.createAccessToken(user);

    refreshToken = jwtTokenizer.createRefreshToken(user);
  }

  @Test
  void shouldReturn401_whenAccessTokenIsExpired() throws Exception {
    mockMvc.perform(get("/hello")
            .cookie(new Cookie("accessToken", expiredAccessToken)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void tokenRefreshAPI() throws Exception {
    mockMvc.perform(get("/token/refresh")
            .cookie(new Cookie("refreshToken", refreshToken)))
        .andExpect(status().isCreated())
        .andExpect(content().string("access token refreshed successfully"));
  }

}
