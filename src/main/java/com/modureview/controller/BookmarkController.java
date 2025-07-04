package com.modureview.controller;

import com.modureview.dto.response.BookmarkDetailResponse;
import com.modureview.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService bookmarkService;

  @GetMapping("/reviews/{reviewId}/bookmarks")
  public ResponseEntity<BookmarkDetailResponse> getBookMarkDetail(
      @PathVariable Long reviewId,
      @CookieValue(name = "email", required = false, defaultValue = "null") String email) {
    BookmarkDetailResponse bookMarkDetailResponse = bookmarkService.bookmarkDetail(reviewId, email);
    return ResponseEntity.ok(bookMarkDetailResponse);

  }

}
