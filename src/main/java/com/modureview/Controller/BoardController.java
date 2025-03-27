package com.modureview.Controller;

import com.modureview.Dto.Board.Request.DeleteBoardRequestDto;
import com.modureview.Dto.Board.Request.WriteBoardRequestDto;
import com.modureview.Dto.Board.Response.DeleteBoardResponseDto;
import com.modureview.Dto.Board.Response.WriteBoardResponseDto;
import com.modureview.Service.BoardService;
import com.modureview.Service.Utill.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/Board")
public class BoardController{
  private final BoardService boardService;

  //페이징 목록
  //TODO : FIle이랑 같이 해결
  /*@GetMapping("/ALL_list")
  public ResponseEntity<Page<ListBoardResponseDto>> list_Board(
      @PageableDefault(size = 10,sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
    Page<ListBoardResponseDto> listDto = boardService.getAll_Board(pageable);
    return ResponseEntity.status(HttpStatus.OK).body(listDto);
  }*/
  //삭제되지 않은 넘들만 페이징 목록
  /*@GetMapping("/list")
  public ResponseEntity<Page<ListBoardResponseDto>> list_ALIVE_Board(
      @PageableDefault(size = 10,sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
    Page<ListBoardResponseDto> listDto_ALIVE = boardService.get_ALIVE_Board(pageable);
    return ResponseEntity.status(HttpStatus.OK).body(listDto_ALIVE);
  }*/

  //글쓰기
  @PostMapping("/write")
  public ResponseEntity<WriteBoardResponseDto> write_Board(
      @RequestBody WriteBoardRequestDto dto, @AuthenticationPrincipal CustomUserDetails user){
    Thread currentTread = Thread.currentThread();
    log.info("현재 진행중인 쓰레드 : " + currentTread.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(boardService.write_Board(dto, user));
  }
  //TODO : FIle이랑 같이 해결
  /*//페이징 검색,Get요청 @RequestBody 사용할 수 없나? ==> null판단 하나하나씩 하는 엄청나고 신기한 노가다로 안되나?
  @GetMapping("/search")
  public ResponseEntity<Page<ListBoardResponseDto>> search_Board(
      @PageableDefault(size = 5,sort = "id" , direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam(name = "title") String title,
      @RequestParam(name = "content") String content,
      @RequestParam(name = "writerName") String writerName){
    SearchBoardDataRequestDto SearchBoardReq = SearchBoardDataRequestDto.createSearchData(title, content, writerName);
    return ResponseEntity.status(HttpStatus.OK).body(
        boardService.search_Board(SearchBoardReq, pageable));
  }*/
/*
  //상세보기
  // 11-12 DetailGalleryBoardResponseDto 안에 file을 받아오는 로직 생겼습니다.
  //TODO : FIle이랑 같이 해결
  @GetMapping("/{BoardId}")
  public ResponseEntity<DetailBoardResponseDto> detail_Board(
      @PathVariable("BoardId") Long boardId){
    return ResponseEntity.status(HttpStatus.OK).body(boardService.detail_Board(boardId));
  }*/

  // 상세보기 -> 수정
  //TODO : FIle이랑 같이 해결
  /*@PutMapping("/{BoardId}/update")
  public ResponseEntity<DetailBoardResponseDto> update_Board(
      @PathVariable("BoardId") Long BoardId, @RequestBody UpdateBoardRequestDto dto, @AuthenticationPrincipal CustomUserDetails user){

    DetailBoardResponseDto responseDto = boardService.update_Board(BoardId, dto, user);

    System.out.println("Controller 응답 객체: " + responseDto);  // 응답 객체 확인 로그
    return ResponseEntity.ok(responseDto);
  }*/
  @DeleteMapping("{BoardId}/delete")
  public ResponseEntity<DeleteBoardResponseDto> delete_Board(
      @PathVariable("BoardId") Long BoardId, DeleteBoardRequestDto dto, @AuthenticationPrincipal CustomUserDetails user){
    return ResponseEntity.status(HttpStatus.OK).body(
        boardService.delete_Board(BoardId, dto,user));
  }
}
