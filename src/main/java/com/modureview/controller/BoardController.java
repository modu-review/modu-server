package com.modureview.controller;

import com.modureview.dto.BoardDetailResponse;
import com.modureview.dto.request.BoardSaveRequest;
import com.modureview.dto.request.PresignRequest;
import com.modureview.service.BoardService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {
  private final BoardService boardService;

  @GetMapping("/post")
  public ResponseEntity<BoardDetailResponse> getBoardDetail(
      @RequestParam Long board_id
  ){
    return ResponseEntity.ok().body(boardService.boardDetail(board_id));
  }

  @PostMapping("/presign")
  public ResponseEntity<?> createImageInfo(@RequestBody PresignRequest fileType) {
    String type = fileType.fileType();
    String imageUuid = boardService.createImageID();
    String presignedURL = boardService.createPresignedURL(imageUuid + "." + type);

    Map<String, String> result = new HashMap<>();
    result.put("uuid", imageUuid);
    result.put("presignedUrl", presignedURL);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/review")
  public ResponseEntity<Map<String,String>> saveBoard(BoardSaveRequest boardSaveRequest) {
    boardService.htmlSanitizer(boardSaveRequest);
    boardService.saveBoard(boardSaveRequest);
    boardService.extractImageInfo(boardSaveRequest);

    Map<String, String> response = Map.of("message", "게시글이 성공적으로 등록되었습니다.");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}
