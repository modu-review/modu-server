package com.modureview.controller;

import com.modureview.dto.request.BoardSearchRequest;
import com.modureview.dto.response.BoardSearchResponse;
import com.modureview.dto.response.CustomPageResponse;
import com.modureview.service.BoardSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class  BoardSearchBoardController {
  private final BoardSearchService boardSearchService;

  @GetMapping("/reviews")
  public ResponseEntity<CustomPageResponse<BoardSearchResponse>> getBoardSearch(
      @RequestBody BoardSearchRequest request
  ){
    return ResponseEntity.ok().body(boardSearchService.boardSearch(request));
  }

}
