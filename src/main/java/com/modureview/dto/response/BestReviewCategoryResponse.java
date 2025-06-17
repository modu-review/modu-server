package com.modureview.dto.response;

import com.modureview.dto.BestReviewDto;
import java.util.List;

public record BestReviewCategoryResponse(
    int count,
    List<BestReviewDto> reviews
) {

  public static BestReviewCategoryResponse of(List<BestReviewDto> list) {
    return new BestReviewCategoryResponse(list.size(), list);
  }
}
