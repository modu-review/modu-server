package com.modureview.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class CustomSlicePageResponse<T> {

  private final List<T> results;
  private final Long next_cursor;
  private final boolean has_next;


  public CustomSlicePageResponse(List<T> results, Long next_cursor, boolean has_next,
      int numberOfElements, int size, boolean first) {
    this.results = results;
    this.next_cursor = next_cursor;
    this.has_next = has_next;
  }
}

