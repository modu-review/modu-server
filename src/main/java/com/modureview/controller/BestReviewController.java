package com.modureview.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modureview.dto.BestReviewDto;
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
                  List<BestReviewDto> reviews = bestReviewsService.getBestReviews(category.name());
                  return Map.of(
                      "count", reviews.size(),
                      "reviews", reviews
                  );
                }
            ));

    try {
      String prettyJsonResponse = objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(finalResponse);
      log.info("최종 응답 데이터: \n{}", prettyJsonResponse);
    } catch (JsonProcessingException e) {
      log.error("최종 응답 객체 JSON 변환 실패", e);
    }
    return ResponseEntity.ok(finalResponse);
  }
}
