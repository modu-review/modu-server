package com.modureview.controller;

import com.modureview.dto.BookmarkRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class BoardController {

  @PostMapping("/bookmark")
  public ResponseEntity<?> setBookmark(@RequestBody BookmarkRequest request) {
    bookmarkService.addBookmark(request);
  }

}
