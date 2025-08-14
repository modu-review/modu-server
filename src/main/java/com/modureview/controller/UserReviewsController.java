package com.modureview.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.modureview.dto.response.CustomSlicePageResponse;
import com.modureview.dto.response.UserReviewsResponse;
import com.modureview.entity.Board;
import com.modureview.service.UserReviewsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserReviewsController {
	private final UserReviewsService userReviewsService;

	@GetMapping("/users/{memberEmail}")
    public ResponseEntity<CustomSlicePageResponse<UserReviewsResponse>> getBoardsByUserEmail(
        @PathVariable String memberEmail,
        @RequestParam(name = "cursor", defaultValue = "0") Long cursor,
        @RequestParam(name = "sort", defaultValue = "recent") String sort
    ) {
        Slice<Board> boardSlice = userReviewsService.userReviews(memberEmail, cursor, sort);
        // Count total results for this author once and apply to all DTOs
        long totalResults = userReviewsService.countByAuthorEmail(memberEmail);
        List<UserReviewsResponse> dtoList = boardSlice.getContent().stream()
            .map(board -> UserReviewsResponse.fromEntity(board, (int) totalResults))
            .collect(Collectors.toList());

		Long nextCursorValue = null;
		if (boardSlice.hasNext() && !boardSlice.getContent().isEmpty()) {
			Board lastBoardInSlice = boardSlice.getContent().get(boardSlice.getContent().size() - 1);
			nextCursorValue = lastBoardInSlice.getId();
		}

        CustomSlicePageResponse<UserReviewsResponse> customResponse = new CustomSlicePageResponse<>(
            dtoList,
            nextCursorValue,
            boardSlice.hasNext(),
            boardSlice.getNumberOfElements(),
            boardSlice.getSize(),
            boardSlice.isFirst()
        );

		return ResponseEntity.ok(customResponse);
	}
}
