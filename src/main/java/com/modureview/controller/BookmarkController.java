package com.modureview.controller;

import com.modureview.dto.response.BookMarkDetailResponse;
import com.modureview.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookMarkController {

  private final BookMarkService bookMarkService;

  @GetMapping("/reviews/{reviewId}/bookmarks")
  public ResponseEntity<BookMarkDetailResponse> getBookMarkDetail(
      @PathVariable Long reviewId,
      @CookieValue(name = "email", required = false, defaultValue = "null") String email) {
    BookMarkDetailResponse bookMarkDetailResponse = bookMarkService.bookMarkDetail(reviewId, email);
    return ResponseEntity.ok(bookMarkDetailResponse);

  }

}
