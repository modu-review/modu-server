package com.modureview.controller;

import java.util.List;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.modureview.dto.response.CustomPageResponse;
import com.modureview.dto.response.SliceBoardResponse;
import com.modureview.entity.Board;
import com.modureview.service.MyPageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MyPageController {

	private final MyPageService myPageService;

	@GetMapping("/users/me/reviews")
	public ResponseEntity<CustomPageResponse<SliceBoardResponse>> getUserBoards(
		@CookieValue(name = "userEmail") String email,
		@RequestParam(name = "page", defaultValue = "0") int page
	) {
		Page<Board> boardPage = myPageService.myPageBoard(email, page);
		List<SliceBoardResponse> listMyPage = boardPage.getContent().stream()
			.map(SliceBoardResponse::fromEntity)
			.toList();
		CustomPageResponse<SliceBoardResponse> myPage = new CustomPageResponse<>(
			listMyPage,
			boardPage.getNumber() + 1,
			boardPage.getTotalPages()
		);
		return ResponseEntity.ok(myPage);
	}

	@GetMapping("/users/me/bookmarks")
	public ResponseEntity<CustomPageResponse<SliceBoardResponse>> getBookMarkBoards(
		@CookieValue(name = "userEmail") String email,
		@RequestParam(name = "page", defaultValue = "0") int page
	) {
		Page<Board> boardMyPage = myPageService.myPageBookmark(email, page);
		List<SliceBoardResponse> listMyPage = boardMyPage.getContent().stream()
			.map(SliceBoardResponse::fromEntity)
			.toList();
		CustomPageResponse<SliceBoardResponse> myPage = new CustomPageResponse<>(
			listMyPage,
			boardMyPage.getNumber() + 1,
			boardMyPage.getTotalPages()
		);
		return ResponseEntity.ok(myPage);
	}

	@PostMapping("/users/me/profileImage")
	public ResponseEntity<?> uploadProfileImage(@CookieValue("userEmail") String email,@RequestParam("profileImage") MultipartFile file) {
		String newImageUrl = myPageService.updateProfileImage(email,file);

		return ResponseEntity.ok().body(Map.of("imageUrl", newImageUrl));
	}
}
