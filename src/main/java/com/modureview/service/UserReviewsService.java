package com.modureview.service;

import java.util.ArrayList;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.modureview.entity.Board;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.UserReviewsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserReviewsService {
	private final UserReviewsRepository userReviewsRepository;
	private final BoardRepository boardRepository;

	public Slice<Board> userReviews(String nickname, Long cursorId, String sort) {
		Pageable pageable = PageRequest.of(0, 6);
		log.info("nickname == {}", nickname);
		log.info("cursorId == {}", cursorId);
		log.info("sort == {}", sort);

		Board targetBoard;

		if (cursorId == null || cursorId == 0L) {
			switch (sort) {
				case "recent":
					targetBoard = userReviewsRepository.findTopByAuthorEmailOrderByCreatedAtDesc(nickname);
					break;
				case "hotbookmarks":
					targetBoard = userReviewsRepository.findTopByAuthorEmailOrderByBookmarksCountDesc(nickname);
					break;
				case "hotcomments":
					targetBoard = userReviewsRepository.findTopByAuthorEmailOrderByCommentsCountDesc(nickname);
					break;
				default:
					targetBoard = userReviewsRepository.findTopByAuthorEmailOrderByCreatedAtDesc(nickname);
			}
			
			// 사용자의 게시글이 없는 경우 빈 Slice 반환
			if (targetBoard == null) {
				return new SliceImpl<>(new ArrayList<>(), pageable, false);
			}
			
		} else {
			targetBoard = boardRepository.findById(cursorId)
				.orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_ID_NOTFOUND));
		}
		switch (sort) {
			case "recent":
				return userReviewsRepository.findByAuthorEmailByCreatedAt(nickname, targetBoard.getCreatedAt(),
					targetBoard.getId(), pageable);
			case "hotbookmarks":
				return userReviewsRepository.findByAuthorEmailByBookmarksCount(nickname,
					targetBoard.getBookmarksCount(), targetBoard.getId(), pageable);
			case "hotcomments":
				return userReviewsRepository.findByAuthorEmailByCommentsCount(nickname,
					targetBoard.getCommentsCount(), targetBoard.getId(), pageable);

		}
		return userReviewsRepository.findByAuthorEmailByCreatedAt(nickname, targetBoard.getCreatedAt(),
			targetBoard.getId(), pageable);
	}

}
