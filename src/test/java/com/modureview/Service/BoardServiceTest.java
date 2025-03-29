package com.modureview.Service;

import com.modureview.Dto.Board.Request.SearchBoardDataRequestDto;
import com.modureview.Dto.Board.Response.ListBoardResponseDto;
import com.modureview.Dto.Board.Response.WriteBoardResponseDto;
import com.modureview.Dto.Board.Request.WriteBoardRequestDto;
import com.modureview.Entity.Auditable;
import com.modureview.Entity.Board;
import com.modureview.Entity.Status.Category;
import com.modureview.Entity.User;
import com.modureview.Repository.BoardRepository;
import com.modureview.Service.Utill.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BoardServiceTest {

  @Mock
  private BoardRepository boardRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private BoardService boardService;

  private CustomUserDetails customUserDetails;
  private User user;
  private Board board;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    // 테스트용 User 생성 (이메일만 존재)
    user = User.builder()
        .email("test@example.com")
        .build();

    // CustomUserDetails는 이메일만 사용
    customUserDetails = new CustomUserDetails(user.getEmail());

    board = Board.builder()
        .Id(1L)
        .title("Test Title")
        .content("Test Content")
        .view_count(0)
        .user(user)
        .category(Category.A)
        .build();

    // Auditable의 createdAt, modifiedAt 필드는 setter가 없으므로 reflection을 통해 설정
    Field createdAtField = Auditable.class.getDeclaredField("createdAt");
    createdAtField.setAccessible(true);
    createdAtField.set(board, LocalDateTime.now());

    Field modifiedAtField = Auditable.class.getDeclaredField("modifiedAt");
    modifiedAtField.setAccessible(true);
    modifiedAtField.set(board, LocalDateTime.now());
  }

  @Test
  public void testWriteBoard() {
    WriteBoardRequestDto requestDto = WriteBoardRequestDto.builder()
        .title("Test Title")
        .content("Test Content")
        .build();

    // userService를 통해 User를 가져옴
    when(userService.getUserByEmail("test@example.com")).thenReturn(user);
    when(boardRepository.save(any(Board.class))).thenReturn(board);

    WriteBoardResponseDto responseDto = boardService.write_Board(requestDto, customUserDetails);
    assertNotNull(responseDto);
    assertEquals(board.getId(), responseDto.getId());
    assertEquals(board.getTitle(), responseDto.getTitle());
  }

  @Test
  public void testSearchBoardByTitle() {
    // 제목으로 검색하는 경우
    SearchBoardDataRequestDto searchDto = SearchBoardDataRequestDto.createSearchData("Test", "", "",Category.A);
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Board> boardPage = new PageImpl<>(Arrays.asList(board), pageable, 1);

    when(boardRepository.findAllTitleContaining("Test", pageable)).thenReturn(boardPage);

    Page<ListBoardResponseDto> result = boardService.search_Board(searchDto, pageable);
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    List<ListBoardResponseDto> list = result.getContent();
    assertEquals(board.getTitle(), list.get(0).getTitle());
  }

  @Test
  public void testDeleteBoard_Success() {
    // delete_Board 메서드는 삭제 전 존재여부와 작성자 검증 후 해당 Board의 ID를 반환합니다.
    when(boardRepository.findByIdWithUserAndCommentsAndFiles(1L))
        .thenReturn(Optional.of(board));

    Long deletedId = boardService.delete_Board(1L, customUserDetails);
    assertNotNull(deletedId);
    assertEquals(1L, deletedId);
  }

  @Test
  public void testDeleteBoard_NotFound() {
    when(boardRepository.findByIdWithUserAndCommentsAndFiles(1L))
        .thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () ->
        boardService.delete_Board(1L, customUserDetails)
    );
    assertTrue(exception.getMessage().contains("Board not found"));
  }

  @Test
  public void testDeleteBoard_UserMismatch() {
    // 작성자가 다른 경우 예외 발생
    User differentUser = User.builder().email("other@example.com").build();
    board.setUser(differentUser);

    when(boardRepository.findByIdWithUserAndCommentsAndFiles(1L))
        .thenReturn(Optional.of(board));

    Exception exception = assertThrows(RuntimeException.class, () ->
        boardService.delete_Board(1L, customUserDetails)
    );
    assertTrue(exception.getMessage().contains("NOT_MATHCH_WRITER_USER_EMAIL"));
  }

  @Test
  public void testGetAllBoard() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Board> boardPage = new PageImpl<>(Arrays.asList(board), pageable, 1);

    when(boardRepository.findAllWithUserAndComments(pageable)).thenReturn(boardPage);

    Page<ListBoardResponseDto> result = boardService.get_AllBoard(pageable);
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    List<ListBoardResponseDto> list = result.getContent();
    assertEquals(board.getTitle(), list.get(0).getTitle());
  }

  @Test
  public void testGetAllBoardCategory() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Board> boardPage = new PageImpl<>(Arrays.asList(board), pageable, 1);

    when(boardRepository.findAllByCategory(pageable, Category.A)).thenReturn(boardPage);

    Page<ListBoardResponseDto> result = boardService.get_All_Board_Category(pageable, "A");
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    List<ListBoardResponseDto> list = result.getContent();
    assertEquals(board.getTitle(), list.get(0).getTitle());
  }

  @Test
  public void testCheckExistBoard_Success() {
    when(boardRepository.findByIdWithUserAndCommentsAndFiles(1L))
        .thenReturn(Optional.of(board));

    Board foundBoard = boardService.checkExistBoard(1L);
    assertNotNull(foundBoard);
    assertEquals(1L, foundBoard.getId());
  }

  @Test
  public void testCheckExistBoard_NotFound() {
    when(boardRepository.findByIdWithUserAndCommentsAndFiles(1L))
        .thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () ->
        boardService.checkExistBoard(1L)
    );
    assertTrue(exception.getMessage().contains("Board not found with id"));
  }
}