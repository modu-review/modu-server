package com.modureview.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

class JwtTokenServiceTest {

  private final JwtTokenService jwtTokenService = new JwtTokenService();

  @Test
  @DisplayName("accressTokenCookie 정보 확인 테스트")
  void isValidAccessToken() {
    ResponseCookie accessCookie = jwtTokenService.createAccessToken("test@example.com");

    assertThat(accessCookie.getName()).isEqualTo("accessToken");
    assertThat(accessCookie.isHttpOnly()).isTrue();
    assertThat(accessCookie.isSecure()).isTrue();
    assertThat(accessCookie.getMaxAge().getSeconds()).isEqualTo(60 * 60L);
    assertThat(accessCookie.getDomain()).isEqualTo(".modu-review.com");
  }

  @Test
  @DisplayName("refreshTokenCookie 정보 확인 리스트")
  void isValidRefreshToken() {
    ResponseCookie refreshCookie = jwtTokenService.createRefreshToken("test@example.com");

    assertThat(refreshCookie.getName()).isEqualTo("refreshToken");
    assertThat(refreshCookie.isHttpOnly()).isTrue();
    assertThat(refreshCookie.isSecure()).isTrue();
    assertThat(refreshCookie.getMaxAge().getSeconds()).isEqualTo(30 * 24 * 60 * 60L);
  }

  @Test
  @DisplayName("userEmailCookie 이름, 이메일 확인 테스트 ")
  void userEmailCookie_shouldContainPlainEmail() {
    ResponseCookie emailCookie = jwtTokenService.createUserEmailCookie("test@example.com");

    assertThat(emailCookie.getName()).isEqualTo("userEmail");
    assertThat(emailCookie.getValue()).isEqualTo("test@example.com");
    assertThat(emailCookie.isHttpOnly()).isTrue();
  }

  @Test
  @DisplayName("지정한 토큰이 잘 지정되는지 확인 ")
  void loginTokenIssueCheck() {
    List<ResponseCookie> cookies = jwtTokenService.loginTokenIssue("user@domain.com");

    assertThat(cookies).hasSize(3);
    assertThat(cookies.stream().map(ResponseCookie::getName).toList())
        .containsExactlyInAnyOrder("accessToken", "refreshToken", "userEmail");
  }
}