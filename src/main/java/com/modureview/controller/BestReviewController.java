package com.modureview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.response.BestReviewResponse;
import com.modureview.entity.Category;
import com.modureview.service.BestReviewsService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
@Slf4j
public class BestReviewController {

  private final BestReviewsService bestReviewsService;
  private final ObjectMapper objectMapper;

  @GetMapping("/reviews/best")
  public ResponseEntity<Map<String, Map<String, Object>>> getBestReviews() {
    Map<String, Map<String, Object>> finalResponse =
        Arrays.stream(Category.values())
            .collect(Collectors.toMap(
                Category::name,
                category -> {
                  List<BestReviewResponse> reviews = bestReviewsService.getBestReviewsForCategory(category.name());
                  return Map.of(
                      "count", reviews.size(),
                      "reviews", reviews
                  );
                }
            ));
    return ResponseEntity.ok(finalResponse);
  }
}
