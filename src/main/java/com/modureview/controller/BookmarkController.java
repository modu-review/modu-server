package com.modureview.controller;

import com.modureview.dto.request.BookmarkRequest;
import com.modureview.dto.response.BookmarkDetailResponse;
import com.modureview.service.BoardService;
import com.modureview.service.BookmarkService;
import com.modureview.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
@Slf4j
public class BookmarkController {

  private final BoardService boardService;
  private final BookmarkService bookmarkService;
  private final UserService userService;

  @GetMapping("/reviews/{reviewId}/bookmarks")
  public ResponseEntity<BookmarkDetailResponse> getBookMarkDetail(
      @PathVariable Long reviewId,
      @CookieValue(name = "userEmail", required = false, defaultValue = "null") String email) {
    BookmarkDetailResponse bookMarkDetailResponse = bookmarkService.bookmarkDetail(reviewId, email);
    return ResponseEntity.ok(bookMarkDetailResponse);

  }

  @PostMapping("reviews/{reviewId}/bookmarks")
  public ResponseEntity<?> addBookmark(@PathVariable Long reviewId,
     @RequestBody BookmarkRequest bookmarkRequest) {
    log.info("reviewId == {}", reviewId);
    log.info("bookmarkRequest == {}", bookmarkRequest);
    String userEmail = bookmarkRequest.userEmail();
    boardService.findBoard(reviewId);
    Long userId = userService.findUserId(userEmail);
    bookmarkService.saveBookmark(reviewId, userId, userEmail);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("reviews/{reviewId}/bookmarks")
  public ResponseEntity<?> deleteBookmark(@PathVariable Long reviewId,
     @RequestBody BookmarkRequest bookmarkRequest) {
    log.info("reviewId == {}", reviewId);
    log.info("bookmarkRequest == {}", bookmarkRequest);
    String userEmail = bookmarkRequest.userEmail();
    bookmarkService.deleteBookmark(reviewId, userEmail);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
