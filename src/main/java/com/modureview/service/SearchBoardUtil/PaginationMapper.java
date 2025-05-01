package com.modureview.service.SearchBoardUtil;

import com.modureview.service.SearchBoardUtil.CustomPagination.PagedResponse;
import com.modureview.service.SearchBoardUtil.CustomPagination.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
@Component
public class PaginationMapper {
  public <T> PagedResponse<T> toPagedResponse(Page<T> page) {
    int currentPage = page.getNumber();
    int totalPages  = page.getTotalPages();

    Pagination pg = new Pagination(currentPage, totalPages);
    return new PagedResponse<>(page.getContent(), pg);
  }
}
