package com.modureview.controller;

import com.modureview.dto.response.BoardSearchResponse;
import com.modureview.dto.response.CustomPageResponse;
import com.modureview.entity.Board;
import com.modureview.service.BoardSearchService;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BoardSearchBoardController {

  private final BoardSearchService boardSearchService;


  @GetMapping("/search")
  public ResponseEntity<CustomPageResponse<BoardSearchResponse>> getBoardSearch(
      @RequestParam(name = "keyword") String keyword,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "sort", defaultValue = "recent") String sort
  ) throws UnsupportedEncodingException {

    log.info("keyword == {}", keyword);
    String decodeKeyword = URLDecoder.decode(keyword, "UTF-8");
    log.info("decodeKeyword == {}", decodeKeyword);
    log.info("sort == {}", sort);
    Page<Board> boardPage = boardSearchService.boardSearch(decodeKeyword, page, sort);
    List<BoardSearchResponse> listSearchBoard = boardPage.getContent().stream()
        .map(BoardSearchResponse::fromEntity)
        .toList();
    CustomPageResponse<BoardSearchResponse> SearchPage = new CustomPageResponse<>(
        listSearchBoard,
        boardPage.getNumber() + 1,
        boardPage.getTotalPages()
    );

    return ResponseEntity.ok().body(SearchPage);
  }

}
