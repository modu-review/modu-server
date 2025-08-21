package com.modureview.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.jwtError.InvalidTokenException;
import com.modureview.service.JwtTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

	private final JwtTokenService jwtTokenService;

	@GetMapping("/users/login")
	public ResponseEntity<String> tokenIssue(@RequestParam String email,
		HttpServletResponse response) {
		List<ResponseCookie> responseCookies = jwtTokenService.loginTokenIssue(email);

		responseCookies.stream()
			.forEach(cookie -> response.addHeader("Set-Cookie", cookie.toString()));
		log.info("토큰 발행 완료");
		return ResponseEntity.ok("토큰 발행 완료 : " + email);
	}

	@GetMapping("/token/refresh")
	public ResponseEntity<?> refresh(HttpServletRequest request,
		HttpServletResponse response) {
		String refreshToken = jwtTokenService.extractCookie(request, "refreshToken")
			.orElseThrow(() -> new InvalidTokenException(JwtErrorCode.UNAUTHORIZED));

		ResponseCookie newAccessToken = jwtTokenService.reIssueAccessToken(
			jwtTokenService.extractSubject(refreshToken));

		response.addHeader("Set-Cookie", newAccessToken.toString());

		return ResponseEntity.ok().build();
	}

	@GetMapping("/users/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			Arrays.stream(cookies).forEach(cookie -> {
				if (cookie.getName().equals("accessToken") ||
					cookie.getName().equals("refreshToken") ||
					cookie.getName().equals("userEmail") ||
					cookie.getName().equals("userNickname")) {
					ResponseCookie expiredCookie = jwtTokenService.expireCookie(
						ResponseCookie.from(cookie.getName(), "").build()
					);
					headers.add(HttpHeaders.SET_COOKIE, expiredCookie.toString());
					log.info("Expired cookie: {}", cookie.getName());
				}
			});
		}
		return ResponseEntity.ok().headers(headers).body("Logged out successfully.");

	}
}
