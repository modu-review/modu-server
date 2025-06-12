package com.modureview.controller;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@Slf4j
class BestReviewControllerTest {

  @Autowired
  BestReviewController controller;
  @Test
  @DisplayName("best/reviews통합 테스트 수행")
  public void testBestReviews() throws Exception {

    ResponseEntity<?> bestReviews = controller.getBestReviews();

  }
}