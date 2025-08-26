package com.modureview.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
		@CookieValue(name = "userNickname") String nickname,
		@RequestParam(name = "page", defaultValue = "0") int page
	) {

		String decoded = URLDecoder.decode(nickname, StandardCharsets.UTF_8);
		Page<Board> boardPage = myPageService.myPageBoard(decoded, page);
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
		@CookieValue(name = "userNickname") String nickname,
		@RequestParam(name = "page", defaultValue = "0") int page
	) {
		String decoded = URLDecoder.decode(nickname, StandardCharsets.UTF_8);
		Page<Board> boardMyPage = myPageService.myPageBookmark(decoded, page);
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

		return ResponseEntity.ok().body("");
	}
	@GetMapping("/users/{nickname}/profileImage")
	public ResponseEntity<?> getProfileImage( @PathVariable String nickname) {
		String profileImage = myPageService.getProfileImage(nickname);
		return ResponseEntity.ok().body(Map.of("profileImage", profileImage));

	}

	@DeleteMapping("/users/me/profileImage")
	public ResponseEntity<?> deleteProfileImage(@CookieValue(name = "userEmail") String email) {
		myPageService.deleteProfileImage(email);
		return ResponseEntity.ok().body("이미지가 기본으로 변경되었습니다.");
	}
}
