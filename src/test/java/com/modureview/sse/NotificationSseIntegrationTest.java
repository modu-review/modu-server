package com.modureview.sse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.modureview.dto.response.NotificationPushResponse;
import com.modureview.service.NotificationService;
import com.modureview.service.NotificationSseService;
import com.modureview.service.UserService;
import com.modureview.service.JwtTokenService;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.show-sql=false"
})
class NotificationSseIntegrationTest {

  @LocalServerPort
  int port;

  @Autowired
  NotificationSseService sseService;

  @MockBean
  JwtTokenService jwtTokenService;

  @MockBean
  UserService userService;

  @MockBean
  NotificationService notificationService;

  @BeforeEach
  void setUpMocks() {
    // 모든 요청에서 accessToken 쿠키가 있다고 가정하고 유효성 통과
    when(jwtTokenService.extractCookie(any(), eq("accessToken")))
        .thenReturn(Optional.of("dummy"));
    Mockito.doNothing().when(jwtTokenService).validateToken("dummy");

    // userNickname 쿠키 → 사용자 ID 매핑 (컨트롤러 요구사항과 일치)
    when(userService.findUserIdByNickname(any(String.class))).thenReturn(1L);

    // 초기 메타: 안읽은 알림 없음
    when(notificationService.hasUnread(1L)).thenReturn(false);
  }

  @Test
  void sse_stream_sends_meta_and_notification_event() throws Exception {
    String url = "http://localhost:" + port + "/notifications/stream";

    HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(Duration.ofSeconds(10))
        .header("Cookie", "userNickname=tester_nick; accessToken=dummy")
        .GET()
        .build();

    HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
    assertThat(response.statusCode()).isEqualTo(200);
    // Content-Type 은 text/event-stream 이어야 함 (부가 파라미터 무시)
    assertThat(response.headers().firstValue("Content-Type").orElse(""))
        .contains("text/event-stream");

    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()));

    // 1) 연결 직후 meta 이벤트 대기
    CountDownLatch metaLatch = new CountDownLatch(1);
    Thread metaThread = new Thread(() -> {
      try {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.startsWith("event: meta")) {
            metaLatch.countDown();
            break;
          }
        }
      } catch (Exception ignored) { }
    });
    metaThread.setDaemon(true);
    metaThread.start();

    boolean metaOk = metaLatch.await(3, TimeUnit.SECONDS);
    assertThat(metaOk).as("should receive initial meta event").isTrue();

    // 2) 서버에서 notification 이벤트 발행 후 수신 대기
    CountDownLatch notiLatch = new CountDownLatch(1);
    Thread notiThread = new Thread(() -> {
      try {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.startsWith("event: notification")) {
            notiLatch.countDown();
            break;
          }
        }
      } catch (Exception ignored) { }
    });
    notiThread.setDaemon(true);
    notiThread.start();

    // 약간의 지연 후 서버 푸시 트리거
    Thread.sleep(200);
    NotificationPushResponse payload = new NotificationPushResponse(
        999L, 123L, "COMMENT", "Test Title", false, false, LocalDateTime.now()
    );
    sseService.sendNotification(1L, payload);

    boolean notiOk = notiLatch.await(3, TimeUnit.SECONDS);
    assertThat(notiOk).as("should receive notification event after server publish").isTrue();

    // 정리
    try { reader.close(); } catch (Exception ignored) {}
  }
}
