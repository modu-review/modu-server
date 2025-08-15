package com.modureview.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.response.NotificationPushResponse;
import com.modureview.entity.Board;
import com.modureview.entity.Notification;
import com.modureview.entity.User;
import com.modureview.entity.NotificationType;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.NotificationRepository;
import com.modureview.repository.UserRepository;
import com.modureview.utill.TestUtil;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("h2")
class NotificationControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private BoardRepository boardRepository;
  @Autowired private NotificationRepository notificationRepository;

  private TestUtil testUtil;
  private String userEmail;
  private Long userId;
  private String emptyUserEmail;
  private Long emptyUserId;
  private Board board;
  private Notification unreadNotification;
  private Notification readNotification;

  @BeforeEach
  void setUp() {
    testUtil = new TestUtil();
    userEmail = "notify@test.com";
    emptyUserEmail = "empty@test.com";

    // 사용자/게시글 준비
    User user = userRepository.save(testUtil.newUser(userEmail));
    userId = user.getId();

    // 빈 사용자(알림 없음)
    User emptyUser = userRepository.save(testUtil.newUser(emptyUserEmail));
    emptyUserId = emptyUser.getId();

    board = testUtil.newBoard(user);
    // 길이가 15자를 넘도록 제목 조정 (절단 확인)
    // 기본 TestUtil 제목은 "테스트"이므로 덮어쓰지 못해 여기서 저장 후 타이틀을 수정하여 다시 저장할 수 없으므로
    // 아예 새 Board를 생성하여 긴 제목으로 저장한다.
    board = Board.builder()
        .title("아주아주아주아주긴제목확인용타이틀")
        .user(user)
        .authorEmail(user.getEmail())
        .category(com.modureview.entity.Category.car)
        .content("<p>content</p>")
        .commentsCount(0)
        .bookmarksCount(0)
        .build();
    board = boardRepository.save(board);

    // 알림 2건 생성: 하나는 unread, 하나는 read
    unreadNotification = notificationRepository.save(
        Notification.builder()
            .receiverUserId(userId)
            .senderUserId(userId)
            .boardId(board.getId())
              .notificationType(NotificationType.comment)
            .build());

    readNotification = notificationRepository.save(
        Notification.builder()
            .receiverUserId(userId)
            .senderUserId(userId)
            .boardId(board.getId())
              .notificationType(NotificationType.bookmark)
            .build());
    // 읽음 처리
    readNotification.markAsRead();
    notificationRepository.save(readNotification);
  }

  @Test
  @DisplayName("GET /users/me/notifications - 빈 상태일 때 빈 배열 반환")
  void getNotifications_empty_list() throws Exception {
    log.info("[REQ] GET /users/me/notifications cookie=userEmail={}", emptyUserEmail);
    MvcResult res = mockMvc.perform(
            get("/users/me/notifications")
                .cookie(new Cookie("userEmail", emptyUserEmail))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
    // Pretty JSON logging
    Object jsonObjectEmpty = objectMapper.readValue(body, Object.class);
    String prettyJsonEmpty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObjectEmpty);
    log.info("[EMPTY LIST] Response Body (pretty):\n{}", prettyJsonEmpty);
    Map<String, Object> root = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> results = (List<Map<String, Object>>) root.get("results");
    assertThat(results).isEmpty();
  }

  @Test
  @DisplayName("GET /users/me/notifications - 목록 조회 및 제목 15자 절단 확인")
  void getNotifications_list_with_title_ellipsis() throws Exception {
    log.info("[REQ] GET /users/me/notifications cookie=userEmail={}", userEmail);
    MvcResult res = mockMvc.perform(
            get("/users/me/notifications")
                .cookie(new Cookie("userEmail", userEmail))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
    // Pretty JSON logging
    Object jsonObjectList = objectMapper.readValue(body, Object.class);
    String prettyJsonList = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObjectList);
    log.info("[LIST ELLIPSIS] Response Body (pretty):\n{}", prettyJsonList);
    Map<String, Object> root = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> results = (List<Map<String, Object>>) root.get("results");

    assertThat(results).isNotEmpty();
    // 제목 절단 규칙: 15자 + "..." 붙음 (한글 code point 기준 절단 가정)
    String title = (String) results.get(0).get("title");
    assertThat(title.length()).isGreaterThanOrEqualTo(3);
    assertThat(title).endsWith("...");
  }

  @Test
  @DisplayName("GET /users/me/notifications - 최소 7건 이상 목록 반환")
  void getNotifications_list_with_seven_items() throws Exception {
    // 추가 알림 7건 생성
    for (int i = 0; i < 7; i++) {
      notificationRepository.save(
          Notification.builder()
              .receiverUserId(userId)
              .senderUserId(userId)
              .boardId(board.getId())
              .notificationType(NotificationType.comment)
              .build());
    }

    log.info("[REQ] GET /users/me/notifications cookie=userEmail={}", userEmail);
    MvcResult res = mockMvc.perform(
            get("/users/me/notifications")
                .cookie(new Cookie("userEmail", userEmail))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
    // Pretty JSON logging
    Object jsonObjectSeven = objectMapper.readValue(body, Object.class);
    String prettyJsonSeven = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObjectSeven);
    log.info("[LIST >=7] Response Body (pretty):\n{}", prettyJsonSeven);
    Map<String, Object> root = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> results = (List<Map<String, Object>>) root.get("results");

    assertThat(results.size()).isGreaterThanOrEqualTo(7);
    // 일부 항목 필드 점검
    Map<String, Object> first = results.get(0);
    Long respBoardId = ((Number) first.get("board_id")).longValue();
    assertThat(respBoardId).isEqualTo(board.getId());
    assertThat((String) first.get("title")).endsWith("...");
  }

  @Test
  @DisplayName("GET /users/me/notifications/unread - unread 존재 여부 true")
  void unread_exists_true() throws Exception {
    log.info("[REQ] GET /users/me/notifications/unread cookie=userEmail={}", userEmail);
    MvcResult res = mockMvc.perform(
            get("/users/me/notifications/unread")
                .cookie(new Cookie("userEmail", userEmail))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    String body = res.getResponse().getContentAsString(StandardCharsets.UTF_8);
    // Pretty JSON logging (boolean)
    Object jsonObjectUnread = objectMapper.readValue(body, Object.class);
    String prettyJsonUnread = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObjectUnread);
    log.info("[UNREAD EXISTS] Response Body (pretty):\n{}", prettyJsonUnread);
    Boolean exists = objectMapper.readValue(body, Boolean.class);
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("PATCH /users/me/notifications/{id} - isRead=true 처리")
  void patch_mark_read() throws Exception {
    Map<String, Object> req = new HashMap<>();
    req.put("isRead", true);
    String reqJson = objectMapper.writeValueAsString(req);
    log.info("[REQ] PATCH /users/me/notifications/{} cookie=userEmail={}\nBody: {}",
        unreadNotification.getId(), userEmail, reqJson);

    mockMvc.perform(
            patch("/users/me/notifications/{id}", unreadNotification.getId())
                .cookie(new Cookie("userEmail", userEmail))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJson))
        .andExpect(status().isOk());

    // 확인: 해당 알림이 읽음 처리되었는지
    Notification n = notificationRepository.findById(unreadNotification.getId()).orElseThrow();
    assertThat(n.isRead()).isTrue();
  }

  @Test
  @DisplayName("PATCH /users/me/notifications/{id} - isDelete=true 처리")
  void patch_mark_delete() throws Exception {
    Map<String, Object> req = new HashMap<>();
    req.put("isDelete", true);
    String reqJson = objectMapper.writeValueAsString(req);
    log.info("[REQ] PATCH /users/me/notifications/{} cookie=userEmail={}\nBody: {}",
        unreadNotification.getId(), userEmail, reqJson);

    mockMvc.perform(
            patch("/users/me/notifications/{id}", unreadNotification.getId())
                .cookie(new Cookie("userEmail", userEmail))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJson))
        .andExpect(status().isOk());

    Notification n = notificationRepository.findById(unreadNotification.getId()).orElseThrow();
    assertThat(n.isDeleted()).isTrue();
  }

  @Test
  @DisplayName("PATCH /users/me/notifications/{id} - 잘못된 요청(둘 다 true) → 400")
  void patch_invalid_both_true() throws Exception {
    Map<String, Object> req = new HashMap<>();
    req.put("isRead", true);
    req.put("isDelete", true);
    String reqJson = objectMapper.writeValueAsString(req);
    log.info("[REQ] PATCH /users/me/notifications/{} cookie=userEmail={}\nBody: {}",
        unreadNotification.getId(), userEmail, reqJson);

    mockMvc.perform(
            patch("/users/me/notifications/{id}", unreadNotification.getId())
                .cookie(new Cookie("userEmail", userEmail))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJson))
        .andExpect(status().isBadRequest());
  }
}
