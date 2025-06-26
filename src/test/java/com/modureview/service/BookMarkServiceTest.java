package com.modureview.service;

import static com.modureview.enums.errors.BoardErrorCode.BOARD_ID_NOTFOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.modureview.dto.response.BookMarkDetailResponse;
import com.modureview.entity.Board;
import com.modureview.entity.User;
import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.BookMarkRepository;
import com.modureview.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookMarkServiceTest {

  @InjectMocks
  private BookMarkService bookMarkService;

  @Mock
  private BookMarkRepository bookMarkRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BoardRepository boardRepository;

  @Test
  @DisplayName("북마크 상세 조회 성공 - 로그인 유저 , 북마크 함")
  void bookMarkDetail_Success_LoggedIn_Bookmarked() {
    //given
    Long reviewId = 1L;
    String email = "test@example.com";
    Board mockBoard = Board.builder().id(reviewId).bookmarksCount(10).build();
    User mockUser = User.builder().email(email).build();

    //Mock 객체의 행동 정의
    when(boardRepository.findById(reviewId)).thenReturn(java.util.Optional.of(mockBoard));
    when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(mockUser));
    when(bookMarkRepository.existsByBoardIdAndEmail(reviewId, email)).thenReturn(
        Optional.of(true));

    //when
    BookMarkDetailResponse response = bookMarkService.bookMarkDetail(reviewId, email);

    //then
    assertThat(response.hasBookmarked()).isTrue();
    assertThat(response.bookmarks()).isEqualTo(10);

    //Mock 객체의 메소드가 의도대로 호출되었는지 검증
    verify(boardRepository, times(1)).findById(reviewId);
    verify(userRepository, times(1)).findByEmail(email);
    verify(bookMarkRepository, times(1)).existsByBoardIdAndEmail(reviewId, email);
  }

  @Test
  @DisplayName("북마크 상세 조회 성공 - 비로그인 유저")
  void bookmarkDetail_Success_NotLoggedIn() {
    //given
    Long reviewId = 1L;
    String email = "null";
    Board mockBoard = Board.builder().id(reviewId).bookmarksCount(5).build();

    when(boardRepository.findById(reviewId)).thenReturn(java.util.Optional.of(mockBoard));

    //when
    BookMarkDetailResponse response = bookMarkService.bookMarkDetail(reviewId, email);

    //then
    assertThat(response.hasBookmarked()).isFalse();
    assertThat(response.bookmarks()).isEqualTo(5);

    //비 로그인시 user,bookmark repository는 호출되지 말아야함.
    verify(userRepository, never()).findByEmail(anyString());
    verify(bookMarkRepository, never()).existsByBoardIdAndEmail(anyLong(), anyString());
  }

  @Test
  @DisplayName("북마크 상세 조회 실패 - 존재하지 않은 게시글 ID")
  void bookMarkDetail_Fail_BoardNotFound() {
    //given
    Long reviewId = 999L;
    String email = "test@example.com";

    when(boardRepository.findById(reviewId)).thenReturn(Optional.empty());

    //when&then
    CustomException exception = assertThrows(CustomException.class, () -> {
      bookMarkService.bookMarkDetail(reviewId, email);
    });

    assertThat(exception.getErrorCode()).isEqualTo(BOARD_ID_NOTFOUND);
  }

  @Test
  @DisplayName("북마크 상세 조회 실패 - 존재하지 않는 유저 (잘못된 토큰)")
  void bookMarkDetail_Fail_UserNotFound() {
    // given
    Long reviewId = 1L;
    String email = "unknown@example.com";
    Board mockBoard = Board.builder().id(reviewId).bookmarksCount(10).build();

    when(boardRepository.findById(reviewId)).thenReturn(Optional.of(mockBoard));
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class, () -> {
      bookMarkService.bookMarkDetail(reviewId, email);
    });

    assertThat(exception.getErrorCode()).isEqualTo(JwtErrorCode.FORBIDDEN);
  }
}