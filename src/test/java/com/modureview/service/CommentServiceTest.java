package com.modureview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.modureview.entity.Comment;
import com.modureview.repository.CommentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import org.springframework.data.domain.Sort.Direction;

/**
 * @ExtendWith(MockitoExtension.class): Mockito 프레임워크를 사용하여 단위 테스트를 진행합니다. 실제 스프링 컨텍스트를 로드하지 않아 통합
 * 테스트보다 훨씬 가볍고 빠릅니다.
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  // @InjectMocks: 테스트 대상인 CommentService 객체를 생성하고,
  // @Mock으로 생성된 가짜(Mock) 객체를 주입합니다.
  @InjectMocks
  private CommentService commentService;

  // @Mock: CommentService가 의존하는 CommentRepository의 가짜 객체를 생성합니다.
  @Mock
  private CommentRepository commentRepository;

  // CommentService가 BoardService도 의존하고 있으므로, 해당 의존성도 Mock으로 생성해줍니다.
  @Mock
  private BoardService boardService;

  @Test
  @DisplayName("댓글 목록 조회 단위 테스트")
  void commentListUnitTest() {
    Long boardId = 1L;
    int page = 1;

    List<Comment> comments = IntStream.range(0, 10)
        .mapToObj(i -> Comment.builder()
            .id((long) i)
            .boardId(boardId)
            .author("test@test.com")
            .content("test content " + i)
            .createdAt(LocalDateTime.now().minusMinutes(i))
            .build())
        .collect(Collectors.toList());

    Pageable pageable = PageRequest.of(page - 1, 15, Sort.by(Direction.DESC, "createdAt"));

    Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

    when(commentRepository.findByBoardId(eq(boardId), any(Pageable.class)))
        .thenReturn(commentPage);

    Page<Comment> resultPage = commentService.commentList(boardId, page);

    assertThat(resultPage).isNotNull();
    assertThat(resultPage.getContent()).hasSize(10);
    assertThat(resultPage.getContent().get(0).getContent()).isEqualTo("test content 0");

    verify(commentRepository).findByBoardId(eq(boardId), eq(pageable));

    System.out.println("✅ 단위 테스트 성공: CommentService가 Repository를 올바르게 호출하고 결과를 잘 반환했습니다.");
    resultPage.getContent().forEach(c ->
        System.out.println(
            "내용: " + c.getContent() + ", 생성 시각: " + c.getCreatedAt()
        )
    );
  }
}