package com.modureview.controller;

import com.modureview.dto.response.BestReviewResponse;
import com.modureview.entity.Board;
import com.modureview.service.MainService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {

  private final MainService mainService;

  @GetMapping("/latestReviews")
  public ResponseEntity<?> getLatestReviews() {
    List<Board> recentBoards = mainService.latest6Reviews();
    List<BestReviewResponse> collect = recentBoards.stream()
        .map(BestReviewResponse::from)
        .collect(Collectors.toList());

    Map<String, Object> response = new HashMap<>();
    response.put("latest_reviews", collect);

    return ResponseEntity.ok().body(response);
  }
}
