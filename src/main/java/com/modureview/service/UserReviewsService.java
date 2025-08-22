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

    public Slice<Board> userReviews(String nickname, Long cursorId, String sort)
    {

        Pageable pageable = PageRequest.of(0, 6);
        log.info("nickname == {}", nickname);
        log.info("cursorId == {}", cursorId);
        log.info("sort == {}", sort);

        boolean isFirstPage = (cursorId == null || cursorId == 0L);

        if (isFirstPage) {
            return switch (sort) {
                case "hotbookmarks" -> userReviewsRepository.findByNicknameOrderByBookmarksCountFirst(nickname, pageable);
                case "hotcomments" -> userReviewsRepository.findByNicknameOrderByCommentsCountFirst(nickname, pageable);
                case "recent" -> userReviewsRepository.findByNicknameOrderByCreatedAtFirst(nickname, pageable)
                ;
                default -> userReviewsRepository.findByNicknameOrderByCreatedAtFirst(nickname, pageable)
                ;
            };
        } else {
            Board anchor = boardRepository.findById(cursorId)
                .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_ID_NOTFOUND));

            return switch (sort) {
                case "hotbookmarks" -> userReviewsRepository.findByNicknameByBookmarksCount(
                    nickname, anchor.getBookmarksCount(), anchor.getId(), pageable);
                case "hotcomments" -> userReviewsRepository.findByNicknameByCommentsCount(
                    nickname, anchor.getCommentsCount(), anchor.getId(), pageable);
                case "recent" -> userReviewsRepository.findByNicknameByCreatedAt
                    (
                        nickname, anchor.getCreatedAt(), anchor.getId(), pageable);
                default -> userReviewsRepository.findByNicknameByCreatedAt(
                    nickname, anchor.getCreatedAt(), anchor.getId(), pageable);
            };
        }
    }

    public long countByNickname(String nickname) {
        return userReviewsRepository.countByNickname(nickname);
    }
}