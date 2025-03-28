package com.modureview;

import com.modureview.Entity.User;
import com.modureview.Repository.UserRepository;
import com.modureview.Service.Utill.JwtTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")  // 테스트용 프로파일 사용 (application-test.yml)
public class FullFlowIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private JwtTokenizer jwtTokenizer;

  @Autowired
  private UserRepository userRepository;

  private String baseUrl;

  @BeforeEach
  public void setup() {
    baseUrl = "http://localhost:" + port;
    // 테스트 시작 전에 기존 사용자를 제거하고, 테스트용 사용자를 등록합니다.
    userRepository.deleteAll();
    User user = User.builder().email("integration@example.com").build();
    userRepository.save(user);
  }

  @Test
  public void testFullFlow_LoginRefreshAndWriteBoard() {
    // 1. 로그인 요청 → /user/oauth2/login?user_email=integration@example.com
    ResponseEntity<String> loginResponse = restTemplate.getForEntity(
        baseUrl + "/user/oauth2/login?user_email=integration@example.com", String.class);
    assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    assertEquals("integration@example.com", loginResponse.getBody());

    // 2. 로그인 응답에서 refreshToken 쿠키 추출
    List<String> loginCookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
    assertNotNull(loginCookies);
    assertFalse(loginCookies.isEmpty());
    String refreshTokenCookie = loginCookies.stream()
        .filter(cookie -> cookie.startsWith("refreshToken="))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("refreshToken cookie not found"));
    String refreshTokenValue = extractCookieValue(refreshTokenCookie, "refreshToken");
    // 검증: 토큰 유효성
    assertTrue(jwtTokenizer.validateToken(refreshTokenValue));

    // 3. (실제 상황에서는 accessToken이 만료된 상태라 가정) Refresh 요청 → /api/v0/token/refresh
    HttpHeaders refreshHeaders = new HttpHeaders();
    refreshHeaders.add("Cookie", "refreshToken=" + refreshTokenValue);
    HttpEntity<?> refreshRequestEntity = new HttpEntity<>(refreshHeaders);
    ResponseEntity<String> refreshResponse = restTemplate.exchange(
        baseUrl + "/api/v0/token/refresh", HttpMethod.GET, refreshRequestEntity, String.class);
    // 재발급 성공 시 201 CREATED와 메시지가 반환되어야 합니다.
    assertEquals(HttpStatus.CREATED, refreshResponse.getStatusCode());
    assertEquals("access token refreshed successfully", refreshResponse.getBody());

    // 4. 새로 발급된 accessToken 쿠키 추출
    List<String> refreshCookies = refreshResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
    assertNotNull(refreshCookies);
    assertFalse(refreshCookies.isEmpty());
    String accessTokenCookie = refreshCookies.stream()
        .filter(cookie -> cookie.startsWith("accessToken="))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("accessToken cookie not found"));
    String accessTokenValue = extractCookieValue(accessTokenCookie, "accessToken");
    assertNotNull(accessTokenValue);
    // (추가 검증: accessToken 유효성 검증 필요 시 jwtTokenizer.validateToken(accessTokenValue))

    // 5. Board 작성 요청 → /api/v0/Board/write
    // Board 작성에 필요한 DTO를 JSON으로 전송합니다.
    // **category 값 추가**
    String boardPayload = "{\"title\": \"Integration Test Title\", \"content\": \"Integration Test Content\", \"category\": \"A\"}";
    HttpHeaders boardHeaders = new HttpHeaders();
    boardHeaders.setContentType(MediaType.APPLICATION_JSON);
    // accessToken 쿠키 포함
    boardHeaders.add("Cookie", "accessToken=" + accessTokenValue);
    HttpEntity<String> boardRequestEntity = new HttpEntity<>(boardPayload, boardHeaders);

    ResponseEntity<String> boardResponse = restTemplate.postForEntity(
        baseUrl + "/api/v0/Board/write", boardRequestEntity, String.class);
    // 게시글 작성 성공 시 201 CREATED를 기대합니다.
    assertEquals(HttpStatus.CREATED, boardResponse.getStatusCode());
    // 응답 본문에 작성한 제목이 포함되어 있으면 성공으로 판단 (예: JSON에 "Integration Test Title")
    assertThat(boardResponse.getBody()).contains("Integration Test Title");
  }

  // 헬퍼: 쿠키 문자열에서 실제 쿠키 값을 추출 (예: "refreshToken=abc123; Path=/; Secure; HttpOnly")
  private String extractCookieValue(String cookieHeader, String cookieName) {
    String[] parts = cookieHeader.split(";");
    for (String part : parts) {
      part = part.trim();
      if (part.startsWith(cookieName + "=")) {
        return part.substring((cookieName + "=").length());
      }
    }
    return null;
  }
}