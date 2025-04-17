package com.modureview.controller;

import com.modureview.dto.request.BookmarkRequest;
import com.modureview.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class BoardController {
  private final BoardService boardService;
  @PostMapping("/bookmark")
  public ResponseEntity<?> setBookmark(@RequestBody BookmarkRequest request) {
    boardService.addBookmark(request);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
