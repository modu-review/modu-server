package com.modureview.controller;

import com.modureview.dto.request.BoardCategoryRequest;
import com.modureview.dto.request.BoardSearchRequest;
import com.modureview.dto.response.BoardSearchResponse;
import com.modureview.dto.response.CustomPageResponse;
import com.modureview.dto.response.CustomSlicePageResponse;
import com.modureview.entity.Board;
import com.modureview.service.BoardSearchService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardSearchBoardController {

  private final BoardSearchService boardSearchService;

  //TODO : ResponseEntity 설정
  @GetMapping("/reviews")
  public CustomPageResponse<BoardSearchResponse> getBoardSearch(
      @RequestBody BoardSearchRequest request
  ) {
    Page<Board> boardPage = boardSearchService.boardSearch(request);
    List<BoardSearchResponse> listSearchBoard = boardPage.getContent().stream()
        .map(BoardSearchResponse::fromEntity)
        .toList();
    CustomPageResponse<BoardSearchResponse> page = new CustomPageResponse<>(
        listSearchBoard,
        boardPage.getNumber() + 1,
        boardPage.getTotalPages()
    );

    return ResponseEntity.ok().body(page);
  }

}
