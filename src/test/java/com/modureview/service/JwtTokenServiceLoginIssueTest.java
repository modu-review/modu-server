package com.modureview.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

class JwtTokenServiceLoginIssueTest {

  static class StubJwtTokenService extends JwtTokenService {
    @Override
    public ResponseCookie createAccessToken(String userEmail) {
      return ResponseCookie.from("accessToken", "stub-access")
          .httpOnly(true)
          .secure(true)
          .sameSite("LAX")
          .path("/")
          .maxAge(60 * 60L)
          .domain(".modu-review.com")
          .build();
    }

    @Override
    public ResponseCookie createRefreshToken(String userEmail) {
      return ResponseCookie.from("refreshToken", "stub-refresh")
          .httpOnly(true)
          .secure(true)
          .sameSite("LAX")
          .path("/")
          .maxAge(30 * 24 * 60 * 60L)
          .domain(".modu-review.com")
          .build();
    }
  }

  @Test
  @DisplayName("loginTokenIssue는 4개 쿠키를 발급한다")
  void loginTokenIssue_returns_four_cookies() {
    String email = "user@domain.com";
    String nickname = "nickname123";
    JwtTokenService service = new StubJwtTokenService();

    List<ResponseCookie> cookies = service.loginTokenIssue(email, nickname);

    assertThat(cookies).hasSize(4);
    assertThat(cookies.stream().map(ResponseCookie::getName).toList())
        .containsExactlyInAnyOrder("accessToken", "refreshToken", "userEmail", "userNickName");

    Map<String, ResponseCookie> byName = cookies.stream()
        .collect(Collectors.toMap(ResponseCookie::getName, c -> c));

    assertThat(byName.get("userEmail").getValue()).isEqualTo(email);
    assertThat(byName.get("userEmail").isHttpOnly()).isTrue();

    assertThat(byName.get("userNickName").getValue()).isEqualTo(nickname);
    assertThat(byName.get("userNickName").isHttpOnly()).isTrue();
  }
}
