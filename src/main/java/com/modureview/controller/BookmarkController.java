package com.modureview.controller;

import com.modureview.dto.request.BookmarkRequest;
import com.modureview.dto.response.BookmarkDetailResponse;
import com.modureview.service.BoardService;
import com.modureview.service.BookmarkService;
import com.modureview.service.UserService;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
      @CookieValue(name = "userNickname" , required = false , defaultValue = "null") String nickname) {
    String decoded = "null".equals(nickname) ? nickname : URLDecoder.decode(nickname, StandardCharsets.UTF_8);
    BookmarkDetailResponse bookMarkDetailResponse = bookmarkService.bookmarkDetail(reviewId, decoded);
    return ResponseEntity.ok(bookMarkDetailResponse);

  }

  @PostMapping("reviews/{reviewId}/bookmarks")
  public ResponseEntity<?> addBookmark(@PathVariable Long reviewId,
      @CookieValue (name = "userNickname" , required = true , defaultValue = "null")String nickname) {
    log.info("reviewId == {}", reviewId);

    String decoded = URLDecoder.decode(nickname, StandardCharsets.UTF_8);
    log.info("nickname == {}", decoded);
    boardService.findBoard(reviewId);
    Long userId = userService.findUserIdByNickname(decoded);
    bookmarkService.saveBookmark(reviewId, userId, decoded);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("reviews/{reviewId}/bookmarks")
  public ResponseEntity<?> deleteBookmark(@PathVariable Long reviewId,
      @CookieValue(name = "userNickname" , required = false ,defaultValue = "null") String nickname) {
    log.info("reviewId == {}", reviewId);
    String decoded = "null".equals(nickname) ? nickname : URLDecoder.decode(nickname, StandardCharsets.UTF_8);
    log.info("nickname == {}", decoded);
    bookmarkService.deleteBookmark(reviewId, decoded);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
