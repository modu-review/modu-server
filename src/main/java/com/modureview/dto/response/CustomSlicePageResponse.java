package com.modureview.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder

public class CustomSlicePageResponse<T>  {


  private final List<T> results;
  private final Long next_cursor;
  private final boolean has_next;
  private final Long total_results;

  public static <T> CustomSlicePageResponse<T> of(List<T> results, Long next_cursor, boolean has_next) {
    return CustomSlicePageResponse.<T>builder()
        .results(results)
        .next_cursor(next_cursor)
        .has_next(has_next)
        .build();
  }

  public static <T> CustomSlicePageResponse<T> of(
      List<T> results , Long next_cursor , boolean has_next , Long total_results
  ){
    return CustomSlicePageResponse.<T>builder()
        .results(results)
        .next_cursor(next_cursor)
        .has_next(has_next)
        .total_results(total_results)
        .build();
  }

}

