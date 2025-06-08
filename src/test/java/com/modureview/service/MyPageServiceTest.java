package com.modureview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.modureview.entity.Board;
import com.modureview.entity.User;
import com.modureview.repository.MyPageRepository;
import com.modureview.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("h2")
class MyPageServiceTest {

  @Autowired
  private MyPageService myPageService;

  @Autowired
  private UserRepository userRepository;


  @Autowired
  private MyPageRepository myPageRepository;

  @Autowired
  private ObjectMapper objectMapper;


  @BeforeEach
  void setUp() {
    User newUser = User.builder()
        .email("TargetEmail")
        .build();

    User user = userRepository.save(newUser);

    User notTargetUser = User.builder()
        .email("NotTargetEmail")
        .build();

    User NotTargetUser = userRepository.save(notTargetUser);

    List<Board> boards = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      if (i % 2 != 0) {
        boards.add(
            Board.builder().title("TestTitle" + i).user(NotTargetUser).authorEmail("NotTargetEmail")
                .content("<p>내용 예시 " + i + "</p>")
                .commentsCount(ThreadLocalRandom.current().nextInt(1, 101))
                .bookmarksCount(ThreadLocalRandom.current().nextInt(1, 101)).build());
      } else {
        boards.add(
            Board.builder().user(user).authorEmail(newUser.getEmail())
                .content("<p>내용 예시 " + i + "</p>")
                .commentsCount(ThreadLocalRandom.current().nextInt(1, 101))
                .bookmarksCount(ThreadLocalRandom.current().nextInt(1, 101)).build());
      }
      myPageRepository.saveAll(boards);
    }
  }

  @AfterEach
  void tearDown() {
    myPageRepository.deleteAll();
  }

  @Test
  @DisplayName("작성자가 올바른 사람인지 확인")
  void MyPage_success() throws Exception {
    String email = "TargetEmail";
    int page = 1;
    long startTime = System.nanoTime();
    Page<Board> rep = myPageService.myPageBoard(email, page);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double durationMs = duration / 1_000_000.0;
    log.info("MyPageServiceTest.MyPage_success(작성자가1 올바른 사람인지 확인) : {} ns ({} ms)", duration,
        String.format("%.3f", durationMs));
    String realJson = objectMapper.
        enable(SerializationFeature.INDENT_OUTPUT)
        .writeValueAsString(rep);
    log.info("realJson == {}", realJson);
  }
}

