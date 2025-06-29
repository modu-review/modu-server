package com.modureview.controller;

import com.modureview.dto.request.BookmarkRequest;
import com.modureview.service.BoardService;
import com.modureview.service.BookmarkService;
import com.modureview.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class BookmarkController {

  private final BoardService boardService;
  private final BookmarkService bookmarkService;
  private final UserService userService;

  @PostMapping("/{reviewId}/bookmark")
  public ResponseEntity<?> updateBookmark(@PathVariable Long reviewId, BookmarkRequest bookmarkRequest) {
    String userEmail = bookmarkRequest.userEmail();
    boardService.findBoard(reviewId);
    Long userId = userService.findUserId(userEmail);
    bookmarkService.saveBookmark(reviewId,userId, userEmail);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

}
