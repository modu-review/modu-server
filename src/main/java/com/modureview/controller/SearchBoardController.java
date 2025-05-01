package com.modureview.controller;

import com.modureview.dto.Response.SearchResponseDto;
import com.modureview.service.SearchBoardService;
import com.modureview.service.SearchBoardUtil.CustomPagination.PagedResponse;
import com.modureview.service.SearchBoardUtil.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchBoardController {
  private final SearchBoardService searchBoardService;
  private final PaginationMapper paginationMapper;

  @GetMapping("/search")
  public ResponseEntity<PagedResponse<SearchResponseDto>> search_board(
      @RequestParam(name = "page",defaultValue = "0")int page,
      @RequestParam(name = "sortBy",defaultValue = "id")String sortBy,
      @RequestParam(name = "direction", defaultValue = "DESC")Sort.Direction direction,
      @RequestParam(name = "keyword")String keyword
  ) {

    Page<SearchResponseDto> result =
        searchBoardService.search_keyword(page, sortBy, direction, keyword);

    PagedResponse<SearchResponseDto> response =
        paginationMapper.toPagedResponse(result);

    return ResponseEntity.ok(response);
  }
}
