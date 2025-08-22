package com.modureview.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.UserReviewsRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("h2")
class UserReviewsServiceTest {

	@Autowired
	private UserReviewsService userReviewsService;

	@Autowired
	private UserReviewsRepository userReviewsRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		List<Board> boards = new ArrayList<>();

		// user1의 게시글들
		for (int i = 0; i < 5; i++) {
			boards.add(
            Board.builder()
                .title("user1_게시글_" + i)
                .nickname("user1")
                .category(Category.book)
                .content("<p>user1 내용 " + i + "</p>")
                .commentsCount(i * 3)
                .bookmarksCount(i * 5)
                .build()
			);
		}

		// user2의 게시글들 (구분용)
		for (int i = 0; i < 3; i++) {
			boards.add(
            Board.builder()
                .title("user2_게시글_" + i)
                .nickname("user2")
                .category(Category.car)
                .content("<p>user2 내용 " + i + "</p>")
                .commentsCount(i * 2)
                .bookmarksCount(i * 4)
                .build()
			);
		}

		userReviewsRepository.saveAll(boards);
		log.info("테스트 데이터 {} 개 생성 완료", boards.size());
	}

	@AfterEach
	void cleanUp() {
		userReviewsRepository.deleteAll();
	}

	@Test
	@DisplayName("초기 로딩 - recent 정렬 성공")
	void userReviews_Success_Recent_Initial() throws Exception {
		String nickname = "user1";
		Long cursorId = null;
		String sort = "recent";

		long startTime = System.nanoTime();
		Slice<Board> result = userReviewsService.userReviews(nickname, cursorId, sort);
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);
		double durationMs = duration / 1_000_000.0;
		log.info("userReviews_Success_Recent_Initial() 실행 시간: {} ns ({} ms)", duration,
			String.format("%.3f", durationMs));

		String realJson = objectMapper
			.enable(SerializationFeature.INDENT_OUTPUT)
			.writeValueAsString(result);
		log.info("realJson == {}", realJson);

		assertThat(result.getContent()).hasSize(5);
		//assertThat(result.getContent()).allMatch(board -> board.getAuthorNickname().equals("user1"))
	}

	@Test
	@DisplayName("초기 로딩 - hotbookmarks 정렬 성공")
	void userReviews_Success_HotBookmarks_Initial() throws Exception {
		String nickname = "user1";
		Long cursorId = 0L;
		String sort = "hotbookmarks";

		long startTime = System.nanoTime();
		Slice<Board> result = userReviewsService.userReviews(nickname, cursorId, sort);
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);
		double durationMs = duration / 1_000_000.0;
		log.info("userReviews_Success_HotBookmarks_Initial() 실행 시간: {} ns ({} ms)", duration,
			String.format("%.3f", durationMs));

		String realJson = objectMapper
			.enable(SerializationFeature.INDENT_OUTPUT)
			.writeValueAsString(result);
		log.info("realJson == {}", realJson);

		assertThat(result.getContent()).hasSize(5);
		//assertThat(result.getContent()).allMatch(board -> board.getAuthorNickname().equals("user1"));
	}

	@Test
	@DisplayName("초기 로딩 - hotcomments 정렬 성공")
	void userReviews_Success_HotComments_Initial() throws Exception {
		String nickname = "user1";
		Long cursorId = null;
		String sort = "hotcomments";

		long startTime = System.nanoTime();
		Slice<Board> result = userReviewsService.userReviews(nickname, cursorId, sort);
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);
		double durationMs = duration / 1_000_000.0;
		log.info("userReviews_Success_HotComments_Initial() 실행 시간: {} ns ({} ms)", duration,
			String.format("%.3f", durationMs));

		String realJson = objectMapper
			.enable(SerializationFeature.INDENT_OUTPUT)
			.writeValueAsString(result);
		log.info("realJson == {}", realJson);

		assertThat(result.getContent()).hasSize(5);
		//assertThat(result.getContent()).allMatch(board -> board.getAuthorNickname().equals("user1"));
	}

	@Test
	@DisplayName("커서 기반 페이징 - recent 정렬 성공")
	void userReviews_Success_Recent_Cursor() throws Exception {
		// 먼저 초기 로딩으로 첫 번째 게시글 가져오기
		Slice<Board> firstPage = userReviewsService.userReviews("user1", null, "recent");
		Long cursorId = firstPage.getContent().get(0).getId();

		String nickname = "user1";
		String sort = "recent";

		long startTime = System.nanoTime();
		Slice<Board> result = userReviewsService.userReviews(nickname, cursorId, sort);
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);
		double durationMs = duration / 1_000_000.0;
		log.info("userReviews_Success_Recent_Cursor() 실행 시간: {} ns ({} ms)", duration,
			String.format("%.3f", durationMs));

		String realJson = objectMapper
			.enable(SerializationFeature.INDENT_OUTPUT)
			.writeValueAsString(result);
		log.info("realJson == {}", realJson);

		//assertThat(result.getContent()).allMatch(board -> board.getAuthorNickname().equals("user1"));
	}

	@Test
	@DisplayName("존재하지 않는 cursorId로 조회 시 예외 발생")
	void userReviews_Fail_InvalidCursorId() {
		String nickname = "user1";
		Long invalidCursorId = 999999L;
		String sort = "recent";

		assertThatThrownBy(() -> userReviewsService.userReviews(nickname, invalidCursorId, sort))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", BoardErrorCode.BOARD_ID_NOTFOUND);

		log.info("존재하지 않는 cursorId에 대한 예외 처리 확인 완료");
	}

	@Test
	@DisplayName("다른 사용자 게시글은 조회되지 않음")
	void UserReviews_Success_userFilter() throws Exception {
		String nickname = "user1";
		Long cursorId = null;
		String sort = "recent";

		Slice<Board> result = userReviewsService.userReviews(nickname, cursorId, sort);

		assertThat(result.getContent()).hasSize(5);
		//assertThat(result.getContent()).allMatch(board -> board.getAuthorNickname().equals("user1"));
		//assertThat(result.getContent()).noneMatch(board -> board.getAuthorNickname().equals("user2"));

		log.info("사용자별 게시글 필터링 확인 완료");
	}
}
