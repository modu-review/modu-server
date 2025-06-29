package com.modureview.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.entity.User;
import com.modureview.exception.CustomException;
import com.modureview.repository.MyPageBookMarkRepository;
import com.modureview.repository.MyPageRepository;
import com.modureview.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MyPageServiceUnitTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private MyPageBookMarkRepository myPageBookMarkRepository;

  @Mock
  private MyPageRepository myPageRepository;

  @InjectMocks
  private MyPageService service;

  @Test
  @DisplayName("존재하지 않는 사용자가 북마크 페이지 조회시 USER_NOT_FOUND 예외 발생")
  void testMyPageBookmark_UserNotFound() {
    String email = "nouser@example.com";
    int page = 1;

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    CustomException ex = assertThrows(CustomException.class,
        () -> service.myPageBookmark(email, page));

    log.info("ex.getErrorCode() == {}", ex.getErrorCode());
  }

  @Test
  @DisplayName("존재하는 사용자가 북마크 페이지 조회 시 정상 결과 반환")
  void testMyPageBookmark_Success() {
    String email = "user@example.com";
    int pageNumber = 1;
    int pageSize = 6;

    User newUser = User.builder()
        .email("user@example.com")
        .build();
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(newUser));

    List<Long> bookmarkIds = List.of(10L, 20L, 30L);
    Pageable pageable = PageRequest.of(pageNumber - 1, pageSize,
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Long> idPage = new PageImpl<>(bookmarkIds, pageable, bookmarkIds.size());
    when(myPageBookMarkRepository.findBookMarksByEmail(email, pageable))
        .thenReturn(idPage);

    Board b1 = Board.builder()
        .title("타이틀1")
        .authorEmail(email)
        .category(Category.book)
        .content("내용1")
        .commentsCount(0)
        .bookmarksCount(0)
        .build();
    ReflectionTestUtils.setField(b1, "id", 10L);

    Board b2 = Board.builder()
        .title("타이틀2")
        .authorEmail(email)
        .category(Category.book)
        .content("내용2")
        .commentsCount(0)
        .bookmarksCount(0)
        .build();
    ReflectionTestUtils.setField(b2, "id", 20L);

    Board b3 = Board.builder()
        .title("타이틀3")
        .authorEmail(email)
        .category(Category.book)
        .content("내용3")
        .commentsCount(0)
        .bookmarksCount(0)
        .build();
    ReflectionTestUtils.setField(b3, "id", 30L);

    when(myPageRepository.findAllById(bookmarkIds))
        .thenReturn(List.of(b2, b3, b1));

    Page<Board> result = service.myPageBookmark(email, pageNumber);

    List<Long> resultIds = result.getContent().stream()
        .map(Board::getId)
        .collect(Collectors.toList());
    System.out.println("조회된 Boards ID 순서: " + resultIds);

    assertEquals(3, result.getContent().size(), "결과 개수는 3개");
    assertEquals(bookmarkIds, resultIds, "ID 순서가 입력 순서와 일치");
    assertEquals(bookmarkIds.size(), result.getTotalElements(), "totalElements 일치");
    assertEquals(pageNumber - 1, result.getPageable().getPageNumber(), "페이지 번호 일치");
    assertEquals(pageSize, result.getPageable().getPageSize(), "페이지 사이즈 일치");
  }


}