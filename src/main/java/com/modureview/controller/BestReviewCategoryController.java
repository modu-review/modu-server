package com.modureview.controller;

import com.modureview.dto.BestReviewDto;
import com.modureview.entity.Category;
import com.modureview.service.BestReviewsService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class BestReviewCategoryController {

  private final BestReviewsService bestReviewsService;

  @GetMapping("/best/reviews")
  public ResponseEntity<?> getBestReviews() {
    Map<String, List<BestReviewDto>> allBestReviews = Stream.of(Category.values())
        .collect(Collectors.toMap(
            Category::name,
            category -> bestReviewsService.getBestReviews(category.name())
        ));

    return ResponseEntity.ok(allBestReviews);
  }
}
