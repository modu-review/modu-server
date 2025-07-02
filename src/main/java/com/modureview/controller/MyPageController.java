package com.modureview.controller;

import com.modureview.dto.response.BoardSearchResponse;
import com.modureview.dto.response.CustomPageResponse;
import com.modureview.entity.Board;
import com.modureview.service.MyPageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MyPageController {

  private final MyPageService myPageService;

  @GetMapping("/users/me/reviews")
  public ResponseEntity<CustomPageResponse<BoardSearchResponse>> getUserBoards(
      @CookieValue(name = "userEmail") String email,
      @RequestParam(name = "page", defaultValue = "0") int page
  ) {
    Page<Board> boardPage = myPageService.myPageBoard(email, page);
    List<BoardSearchResponse> listMyPage = boardPage.getContent().stream()
        .map(BoardSearchResponse::fromEntity)
        .toList();
    CustomPageResponse<BoardSearchResponse> myPage = new CustomPageResponse<>(
        listMyPage,
        boardPage.getNumber() + 1,
        boardPage.getTotalPages()
    );
    return ResponseEntity.ok(myPage);
  }

  @GetMapping("/users/me/bookmarks")
  public ResponseEntity<CustomPageResponse<BoardSearchResponse>> getBookMarkBoards(
      @CookieValue(name = "userEmail") String email,
      @RequestParam(name = "page", defaultValue = "0") int page
  ) {
    Page<Board> boardMyPage = myPageService.myPageBookmark(email, page);
    List<BoardSearchResponse> listMyPage = boardMyPage.getContent().stream()
        .map(BoardSearchResponse::fromEntity)
        .toList();
    CustomPageResponse<BoardSearchResponse> myPage = new CustomPageResponse<>(
        listMyPage,
        boardMyPage.getNumber() + 1,
        boardMyPage.getTotalPages()
    );
    return ResponseEntity.ok(myPage);
  }
}
