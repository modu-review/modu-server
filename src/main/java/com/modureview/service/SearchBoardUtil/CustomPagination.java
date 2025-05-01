package com.modureview.service.SearchBoardUtil;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CustomPagination {


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Pagination {
    private int current_page;
    private int total_Pages;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PagedResponse<T> {
    private List<T> data;
    private Pagination pagination;
  }
}

