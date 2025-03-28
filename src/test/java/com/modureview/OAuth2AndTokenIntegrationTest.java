package com.modureview;

import com.modureview.Entity.User;
import com.modureview.Repository.UserRepository;
import com.modureview.Service.Utill.JwtTokenizer;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // 테스트용 프로파일, application-test.yml 등으로 설정
public class OAuth2AndTokenIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtTokenizer jwtTokenizer;

  private String baseUrl;

  @BeforeEach
  public void setup() {
    baseUrl = "http://localhost:" + port;
    // 테스트 시작 전, 기존 사용자를 제거하고 테스트용 사용자를 등록
    userRepository.deleteAll();
    User user = User.builder()
        .email("integration@example.com")
        .build();
    userRepository.save(user);
  }

  @Test
  public void testCompleteLoginAndRefreshFlow() {
    // 1. 로그인 요청 - /user/oauth2/login?user_email=integration@example.com
    ResponseEntity<String> loginResponse = restTemplate.getForEntity(
        baseUrl + "/user/oauth2/login?user_email=integration@example.com", String.class);
    assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    assertEquals("integration@example.com", loginResponse.getBody());

    // 2. 로그인 응답에서 refreshToken 쿠키 추출
    List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
    assertThat(cookies).isNotEmpty();
    String refreshTokenCookie = cookies.stream()
        .filter(cookie -> cookie.startsWith("refreshToken="))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("refreshToken cookie not found"));

    String refreshTokenValue = extractCookieValue(refreshTokenCookie, "refreshToken");
    // jwtTokenizer.validateToken()로 refreshToken 유효성 검증
    assertTrue(jwtTokenizer.validateToken(refreshTokenValue));

    // 3. /api/v0/token/refresh 엔드포인트 호출 - refreshToken 쿠키 포함
    HttpHeaders headers = new HttpHeaders();
    headers.add("Cookie", "refreshToken=" + refreshTokenValue);
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<String> refreshResponse = restTemplate.exchange(
        baseUrl + "/api/v0/token/refresh", HttpMethod.GET, requestEntity, String.class);
    assertEquals(HttpStatus.CREATED, refreshResponse.getStatusCode());
    assertEquals("access token refreshed successfully", refreshResponse.getBody());

    // 4. refresh 응답에서 accessToken 쿠키가 설정되었는지 확인
    List<String> refreshCookies = refreshResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
    assertThat(refreshCookies).isNotEmpty();
    boolean hasAccessToken = refreshCookies.stream().anyMatch(cookie -> cookie.startsWith("accessToken="));
    assertTrue(hasAccessToken);
  }

  // 쿠키 문자열에서 실제 값을 추출하는 헬퍼 메서드
  private String extractCookieValue(String cookieHeader, String cookieName) {
    // 예: "refreshToken=theTokenValue; Path=/; Secure; HttpOnly"
    String[] parts = cookieHeader.split(";");
    for (String part : parts) {
      part = part.trim();
      if (part.startsWith(cookieName + "=")) {
        return part.substring((cookieName + "=").length());
      }
    }
    return null;
  }

  @Test
  public void testRefreshToken_InvalidToken() {
    String invalidToken = "thisIsNotAValidToken";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Cookie", "refreshToken=" + invalidToken);
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
        baseUrl + "/api/v0/token/refresh", HttpMethod.GET, requestEntity, String.class);

    // jwtTokenizer.validateToken()가 false를 반환하여 BAD_REQUEST가 응답되어야 함.
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
