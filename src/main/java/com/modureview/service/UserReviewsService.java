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

	public Slice<Board> userReviews(String email, Long cursorId, String sort) {
		Pageable pageable = PageRequest.of(0, 6);
		log.info("nickname == {}", email);
		log.info("cursorId == {}", cursorId);
		log.info("sort == {}", sort);

		if (cursorId == null || cursorId == 0L) {
			switch (sort) {
				case "recent":
					return userReviewsRepository.findFirstSliceByAuthorEmailOrderByCreatedAt(email, pageable);
				case "hotbookmarks":
					return userReviewsRepository.findFirstSliceByAuthorEmailOrderByBookmarksCount(email, pageable);
				case "hotcomments":
					return userReviewsRepository.findFirstSliceByAuthorEmailOrderByCommentsCount(email, pageable);
				default:
					return userReviewsRepository.findFirstSliceByAuthorEmailOrderByCreatedAt(email, pageable);
			}
		}

		Board targetBoard = boardRepository.findById(cursorId)
			.orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_ID_NOTFOUND));

		switch (sort) {
			case "recent":
				return userReviewsRepository.findByAuthorEmailByCreatedAt(
					email, targetBoard.getCreatedAt(), targetBoard.getId(), pageable);
			case "hotbookmarks":
				return userReviewsRepository.findByAuthorEmailByBookmarksCount(
					email, targetBoard.getBookmarksCount(), targetBoard.getId(), pageable);
			case "hotcomments":
				return userReviewsRepository.findByAuthorEmailByCommentsCount(
					email, targetBoard.getCommentsCount(), targetBoard.getId(), pageable);
			default:
				return userReviewsRepository.findByAuthorEmailByCreatedAt(
					email, targetBoard.getCreatedAt(), targetBoard.getId(), pageable);
		}
	}

	public long countByAuthorEmail(String authorEmail) {
		return userReviewsRepository.countByAuthorEmail(authorEmail);
	}

}
