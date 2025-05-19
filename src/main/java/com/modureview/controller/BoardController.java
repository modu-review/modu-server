package com.modureview.controller;

import com.modureview.dto.BoardDetailResponse;
import com.modureview.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {
  private final BoardService boardService;

  @GetMapping("/post")
  public ResponseEntity<BoardDetailResponse> getBoardDetail(
      @RequestParam Long board_id
  ){
    return ResponseEntity.ok().body(boardService.boardDetail(board_id));
  }

}
