package com.modureview.controller;

import com.modureview.dto.response.BoardSearchResponse;
import com.modureview.dto.response.CustomPageResponse;
import com.modureview.dto.response.CustomSlicePageResponse;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.service.SearchService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;


  @GetMapping("/search")
  public ResponseEntity<CustomPageResponse<BoardSearchResponse>> getBoardSearch(
      @RequestParam(name = "keyword") String keyword,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "sort", defaultValue = "recent") String sort
  ) {
    Page<Board> boardPage = searchService.boardSearch(keyword, page, sort);
    List<BoardSearchResponse> listSearchBoard = boardPage.getContent().stream()
        .map(BoardSearchResponse::fromEntity)
        .toList();
    CustomPageResponse<BoardSearchResponse> SearchPage = new CustomPageResponse<>(
        listSearchBoard,
        boardPage.getNumber() + 1,
        boardPage.getTotalPages()
    );

    return ResponseEntity.ok().body(SearchPage);
  }


  @GetMapping("/reviews")
  public ResponseEntity<CustomSlicePageResponse<BoardSearchResponse>> getBoardsByCategory(
      @RequestParam(name = "category") Category category,
      @RequestParam(name = "cursorId", defaultValue = "0") Long cursorId,
      @RequestParam(name = "recent", defaultValue = "recent") String sort) {
    Slice<Board> boardSlice = searchService.getCategoryBoard(category, cursorId, sort);
    List<BoardSearchResponse> dtoList = boardSlice.getContent().stream()
        .map(BoardSearchResponse::fromEntity)
        .collect(Collectors.toList());

    Long nextCursorValue = null;
    if (boardSlice.hasNext() && !boardSlice.getContent().isEmpty()) {
      Board lastBoardInSlice = boardSlice.getContent().get(boardSlice.getContent().size() - 1);
      nextCursorValue = lastBoardInSlice.getId();
    }

    CustomSlicePageResponse<BoardSearchResponse> customResponse = new CustomSlicePageResponse<>(
        dtoList,
        nextCursorValue,
        boardSlice.hasNext(),
        boardSlice.getNumberOfElements(),
        boardSlice.getSize(),
        boardSlice.isFirst()
    );

    return ResponseEntity.ok(customResponse);
  }


}
