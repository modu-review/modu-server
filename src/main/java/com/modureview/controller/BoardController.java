package com.modureview.controller;

import com.modureview.dto.BoardDetailResponse;
import com.modureview.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;

  @GetMapping("/board/{boardId}")
  public ResponseEntity<BoardDetailResponse> getBoardDetail(
      @PathVariable("boardId") Long boardId
  ){
    return ResponseEntity.ok(boardService.boardDetail(boardId));
  }

}
