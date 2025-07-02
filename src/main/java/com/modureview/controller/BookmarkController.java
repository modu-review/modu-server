package com.modureview.controller;

import com.modureview.dto.request.BookmarkRequest;
import com.modureview.dto.response.BookMarkDetailResponse;
import com.modureview.service.BoardService;
import com.modureview.service.BookMarkService;
import com.modureview.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class BookMarkController {

  private final BoardService boardService;
  private final BookMarkService bookmarkService;
  private final UserService userService;

  @GetMapping("/reviews/{reviewId}/bookmarks")
  public ResponseEntity<BookMarkDetailResponse> getBookMarkDetail(
      @PathVariable Long reviewId,
      @CookieValue(name = "email", required = false, defaultValue = "null") String email) {
    BookMarkDetailResponse bookMarkDetailResponse = bookmarkService.bookMarkDetail(reviewId, email);
    return ResponseEntity.ok(bookMarkDetailResponse);

  }

  @PostMapping("reviews/{reviewId}/bookmark")
  public ResponseEntity<?> updateBookmark(@PathVariable Long reviewId,
      BookmarkRequest bookmarkRequest) {
    String userEmail = bookmarkRequest.userEmail();
    boardService.findBoard(reviewId);
    Long userId = userService.findUserId(userEmail);
    bookmarkService.saveBookmark(reviewId, userId, userEmail);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("reviews/{reviewId}/bookmark")
  public ResponseEntity<?> deleteBookmark(@PathVariable Long reviewId,
      BookmarkRequest bookmarkRequest) {
    String userEmail = bookmarkRequest.userEmail();
    bookmarkService.deleteBookmark(reviewId, userEmail);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
