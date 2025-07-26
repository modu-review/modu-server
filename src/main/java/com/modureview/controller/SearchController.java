package com.modureview.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.modureview.dto.response.CustomPageResponse;
import com.modureview.dto.response.CustomSlicePageResponse;
import com.modureview.dto.response.SliceBoardResponse;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.service.SearchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@GetMapping("/search")
	public ResponseEntity<CustomPageResponse<SliceBoardResponse>> getBoardSearch(
		@RequestParam(name = "keyword") String keyword,
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "sort", defaultValue = "recent") String sort
	) throws UnsupportedEncodingException {
		String decodeKeyword = URLDecoder.decode(keyword, "UTF-8");
		log.info("Searching for " + decodeKeyword);
		Page<Board> boardPage = searchService.boardSearch(decodeKeyword, page, sort);
		List<SliceBoardResponse> listSearchBoard = boardPage.getContent().stream()
			.map(SliceBoardResponse::fromEntity)
			.toList();
		CustomPageResponse<SliceBoardResponse> SearchPage = new CustomPageResponse<>(
			listSearchBoard,
			boardPage.getNumber() + 1,
			boardPage.getTotalPages()
		);

		return ResponseEntity.ok().body(SearchPage);
	}

	@GetMapping("/reviews")
	public ResponseEntity<CustomSlicePageResponse<SliceBoardResponse>> getBoardsByCategory(
		@RequestParam(name = "categoryId") Category category,
		@RequestParam(name = "cursor", defaultValue = "0") Long cursorId,
		@RequestParam(name = "sort", defaultValue = "recent") String sort) {
		Slice<Board> boardSlice = searchService.getCategoryBoard(category, cursorId, sort);
		List<SliceBoardResponse> dtoList = boardSlice.getContent().stream()
			.map(SliceBoardResponse::fromEntity)
			.collect(Collectors.toList());

		Long nextCursorValue = null;
		if (boardSlice.hasNext() && !boardSlice.getContent().isEmpty()) {
			Board lastBoardInSlice = boardSlice.getContent().get(boardSlice.getContent().size() - 1);
			nextCursorValue = lastBoardInSlice.getId();
		}

		CustomSlicePageResponse<SliceBoardResponse> customResponse = new CustomSlicePageResponse<>(
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
