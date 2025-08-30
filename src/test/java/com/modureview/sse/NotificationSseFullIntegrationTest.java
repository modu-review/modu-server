package com.modureview.sse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.response.MetaDto;
import com.modureview.dto.response.NotificationPushResponse;
import com.modureview.entity.User;
import com.modureview.repository.UserRepository;
import com.modureview.service.JwtTokenService;
import com.modureview.service.NotificationSseService;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2")
class NotificationSseFullIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    NotificationSseService sseService;

    @Autowired
    ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        // 테스트 유저 저장
        testUser = User.builder()
            .email("test@example.com")
            .nickname("tester_nick")
            .build();
        userRepository.save(testUser);
    }

    @Test
    void sse_stream_end_to_end() throws JsonProcessingException {
        // given
        ResponseCookie at = jwtTokenService.createAccessToken(testUser.getEmail());
        String accessToken = at.getValue();
        String url = "http://localhost:" + port + "/notifications/stream";
        
        // 동시성 문제를 해결하기 위해 스레드 안전한 리스트 사용
        List<String> receivedData = new CopyOnWriteArrayList<>();
        List<String> receivedEvents = new CopyOnWriteArrayList<>();

        // when
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(10))
            .header("Cookie", "userNickname=" + testUser.getNickname() + "; accessToken=" + accessToken)
            .GET()
            .build();

        // 비동기적으로 SSE 스트림을 읽을 스레드 실행
        Thread readerThread = new Thread(() -> {
            try {
                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()))) {
                    String line;
                    while ((line = reader.readLine()) != null && !Thread.currentThread().isInterrupted()) {
                        if (line.startsWith("event:")) {
                            receivedEvents.add(line.substring(6).trim());
                        } else if (line.startsWith("data:")) {
                            receivedData.add(line.substring(5).trim());
                        }
                    }
                }
            } catch (Exception e) {
                if (!(e instanceof InterruptedException)) {
                    e.printStackTrace();
                }
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();

        // then
        // 1. SSE 연결이 성공하고 첫 'meta' 이벤트가 수신될 때까지 대기
        await().atMost(5, TimeUnit.SECONDS).until(() -> receivedEvents.size() >= 1);

        assertThat(receivedEvents.get(0)).isEqualTo("meta");

        // meta 이벤트의 데이터를 MetaDto로 파싱하여 검증
        String metaJson = receivedData.get(0);
        MetaDto metaDto = objectMapper.readValue(metaJson, MetaDto.class);
        assertThat(metaDto.hasNotification()).isFalse();

        // 2. 서버에서 알림을 보내고 클라이언트가 두 번째 이벤트를 수신하는지 검증
        NotificationPushResponse payload = new NotificationPushResponse(
            1000L, 10L, "COMMENT", "Test Title", false, false, LocalDateTime.now()
        );
        sseService.sendNotification(testUser.getId(), payload);

        // 'notification' 이벤트가 수신될 때까지 대기 (총 이벤트 수가 2개가 될 때까지)
        await().atMost(5, TimeUnit.SECONDS).until(() -> receivedEvents.size() >= 2);

        assertThat(receivedEvents.get(1)).isEqualTo("notification");
        
        // 3. 수신된 알림 데이터 검증
        try {
            String notificationJson = receivedData.get(1);

            // LocalDateTime 파싱 오류를 피하기 위해, JSON 문자열 자체를 검증합니다.
            assertThat(notificationJson).contains("\"board_id\":" + payload.board_id());
            assertThat(notificationJson).contains("\"type\":\"" + payload.type() + "\"");
            assertThat(notificationJson).contains("\"title\":\"" + payload.title() + "\"");

        } catch (Exception e) {
            fail("Failed to parse notification event data", e);
        } finally {
            // 테스트 종료 후 스레드 정리
            readerThread.interrupt();
        }
    }
}