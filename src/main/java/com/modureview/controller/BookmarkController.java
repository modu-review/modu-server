package com.modureview.controller;

import com.modureview.dto.request.BookmarkRequest;
import com.modureview.service.BoardService;
import com.modureview.service.BookmarkService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class BookmarkController {

  private final BoardService boardService;
  private final BookmarkService bookmarkService;

  @PostMapping("/bookmark")
  public void updateBookmark(BookmarkRequest bookmarkRequest) {
      boardService.findBoard(bookmarkRequest.boardId());

      bookmarkService.redisUpdate(bookmarkRequest.boardId());
      bookmarkService.saveBookmark(bookmarkRequest);
  }

}
