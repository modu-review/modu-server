package com.modureview.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class CustomSlicePageResponse<T> {

  private final List<T> content;
  private final Long nextCursor;
  private final boolean hasNext;


  public CustomSlicePageResponse(List<T> content, Long nextCursor, boolean hasNext,
      int numberOfElements, int size, boolean first) {
    this.content = content;
    this.nextCursor = nextCursor;
    this.hasNext = hasNext;
  }
}

