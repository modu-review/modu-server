package com.modureview.Service.Utill;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.modureview.Entity.User;
import com.modureview.Service.RefreshTokenService;
import com.modureview.Service.Utill.JwtTokenizer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class JwtTokenizerTest {

  private JwtTokenizer jwtTokenizer;
  private RefreshTokenService refreshTokenService; // 모의 객체로 생성

  @BeforeEach
  public void setup() {
    // refreshTokenService는 테스트 목적상 단순 모의 객체로 생성합니다.
    refreshTokenService = Mockito.mock(RefreshTokenService.class);
    // 필요한 키 값은 임의의 문자열로 설정 (실제 테스트에선 테스트용 프로퍼티를 활용)
    String accessSecretKey = "accessSecretKeyForTesting1234567890123456";
    String refreshSecretKey = "refreshSecretKeyForTesting1234567890123456";
    jwtTokenizer = new JwtTokenizer(accessSecretKey, refreshSecretKey, refreshTokenService);
  }

  @Test
  public void testReissueTokenPairSetsCookies() {
    // given: 테스트용 사용자 생성 (subject는 이메일이어야 함)
    User user = User.builder().email("test@example.com").build();

    // refreshTokenService 관련 동작은 필요에 따라 stub 처리합니다.
    when(refreshTokenService.getRefreshTokenByUserId(anyLong()))
        .thenReturn(java.util.Optional.empty());
    // 저장 호출은 그냥 리턴하도록 설정
    doAnswer(invocation -> invocation.getArgument(0))
        .when(refreshTokenService).saveRefreshToken(any());

    // 모의 HttpServletResponse 생성
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    // when: 토큰 재발급 메서드 호출
    jwtTokenizer.reissueTokenPair(response, user);

    // then: response.addCookie()가 호출되어 refreshToken과 accessToken 쿠키가 추가되었는지 확인합니다.
    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
    verify(response, atLeast(2)).addCookie(cookieCaptor.capture());
    List<Cookie> cookies = cookieCaptor.getAllValues();

    boolean refreshCookieFound = cookies.stream().anyMatch(cookie -> "refreshToken".equals(cookie.getName()));
    boolean accessCookieFound = cookies.stream().anyMatch(cookie -> "accessToken".equals(cookie.getName()));

    assertTrue(refreshCookieFound, "refreshToken cookie should be set");
    assertTrue(accessCookieFound, "accessToken cookie should be set");

    // 추가 검증: 각 쿠키가 HttpOnly, Secure, SameSite 설정을 갖고 있는지 (쿠키 클래스에서는 getAttribute()는 지원하지 않을 수 있으므로, 설정값은 로깅 등을 통해 수동 확인)
    // 예시로, HttpOnly와 Path만 검증합니다.
    cookies.stream()
        .filter(cookie -> "refreshToken".equals(cookie.getName()))
        .forEach(cookie -> {
          assertTrue(cookie.isHttpOnly(), "refreshToken cookie should be HttpOnly");
          assertEquals("/", cookie.getPath(), "refreshToken cookie path should be '/'");
        });
    cookies.stream()
        .filter(cookie -> "accessToken".equals(cookie.getName()))
        .forEach(cookie -> {
          assertTrue(cookie.isHttpOnly(), "accessToken cookie should be HttpOnly");
          assertEquals("/", cookie.getPath(), "accessToken cookie path should be '/'");
        });
  }
}