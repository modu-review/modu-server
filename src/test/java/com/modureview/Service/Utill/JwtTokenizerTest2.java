package com.modureview.Service.Utill;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.modureview.Entity.RefreshToken;
import com.modureview.Entity.User;
import com.modureview.Service.RefreshTokenService;
import com.modureview.Service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class JwtTokenizerTest2 {

  private JwtTokenizer jwtTokenizer;
  private RefreshTokenService refreshTokenService;
  private HttpServletResponse response;
  private HttpServletRequest request;
  private User testUser;
  private UserService userService;

  // 64자 이상의 테스트용 키 (예시)
  private final String accessSecretKey = "0123456789012345678901234567890123456789012345678901234567890123";
  private final String refreshSecretKey = "abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdef";

  @BeforeEach
  public void setUp() {
    refreshTokenService = mock(RefreshTokenService.class);
    jwtTokenizer = new JwtTokenizer(accessSecretKey, refreshSecretKey, refreshTokenService,userService);
    response = mock(HttpServletResponse.class);
    request = mock(HttpServletRequest.class);
    testUser = User.builder().email("test@example.com").build();
  }

  @Test
  public void testCreateAndParseAccessToken() {
    // AccessToken 생성: subject는 testUser.getEmail()이어야 함.
    String accessToken = jwtTokenizer.createAccessToken(testUser);
    assertNotNull(accessToken);

    Claims claims = jwtTokenizer.parseAccessToken(accessToken);
    assertEquals("test@example.com", claims.getSubject());

    // validateToken() 메서드는 refreshSecretKey를 사용하므로 accessToken에 대해 false가 나와야 합니다.
    assertFalse(jwtTokenizer.validateToken(accessToken), "validateToken should return false for accessToken");
  }

  @Test
  public void testCreateAndParseRefreshToken() {
    String refreshToken = jwtTokenizer.createRefreshToken(testUser);
    assertNotNull(refreshToken);

    Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
    assertEquals("test@example.com", claims.getSubject());

    // refreshToken은 refreshSecretKey로 검증하므로 true여야 합니다.
    assertTrue(jwtTokenizer.validateToken(refreshToken), "validateToken should return true for refreshToken");
  }

  @Test
  public void testReissueTokenPair() {
    // refreshTokenService 동작을 stub 처리
    when(refreshTokenService.getRefreshTokenByUserId(anyLong()))
        .thenReturn(Optional.empty());
    doAnswer(invocation -> invocation.getArgument(0))
        .when(refreshTokenService).saveRefreshToken(any(RefreshToken.class));

    jwtTokenizer.reissueTokenPair(response, testUser);

    // response.addCookie()가 두 번 이상 호출되어 accessToken, refreshToken 쿠키가 세팅되었는지 확인
    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
    verify(response, atLeast(2)).addCookie(cookieCaptor.capture());
    boolean accessTokenSet = cookieCaptor.getAllValues().stream()
        .anyMatch(cookie -> "accessToken".equals(cookie.getName()));
    boolean refreshTokenSet = cookieCaptor.getAllValues().stream()
        .anyMatch(cookie -> "refreshToken".equals(cookie.getName()));

    assertTrue(accessTokenSet, "accessToken cookie should be set");
    assertTrue(refreshTokenSet, "refreshToken cookie should be set");
  }

  @Test
  public void testRemoveToken() {
    // 더미 쿠키들을 가진 request 생성
    Cookie accessTokenCookie = new Cookie("accessToken", "dummy");
    Cookie refreshTokenCookie = new Cookie("refreshToken", "dummy");
    Cookie userCookie = new Cookie("userCookie", "test@example.com");
    Cookie[] cookies = new Cookie[] { accessTokenCookie, refreshTokenCookie, userCookie };
    when(request.getCookies()).thenReturn(cookies);

    jwtTokenizer.removeToken(response, request);

    // 각 쿠키가 maxAge 0으로 설정되도록 response.addCookie()가 호출되었는지 검증
    ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
    verify(response, times(3)).addCookie(captor.capture());
    captor.getAllValues().forEach(cookie ->
        assertEquals(0, cookie.getMaxAge(), "Cookie " + cookie.getName() + " should have maxAge 0")
    );
  }
}