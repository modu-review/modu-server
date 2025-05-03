package com.modureview.controller;

import com.modureview.enums.JwtErrorCode;
import com.modureview.exception.jwtError.InvalidTokenException;
import com.modureview.service.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

  private final JwtTokenService jwtTokenService;

  @GetMapping("/login/result")
  public ResponseEntity<String> tokenIssue(@RequestParam String email,
      HttpServletResponse response) {
    List<ResponseCookie> responseCookies = jwtTokenService.loginTokenIssue(email);

    responseCookies.stream()
        .forEach(cookie -> response.addHeader("Set-Cookie", cookie.toString()));

    return ResponseEntity.ok("토큰 발행 완료 : " + email);

  }

  @GetMapping("/token/refresh")
  public ResponseEntity<?> refresh(@RequestParam String token, HttpServletRequest request,
      HttpServletResponse response) {
    String refreshToken = jwtTokenService.extractCookie(request, "refreshToken")
        .orElseThrow(() -> new InvalidTokenException(JwtErrorCode.UNAUTHORIZED));

    ResponseCookie newAccessToken = jwtTokenService.reIssueAccessToken(token);

    response.addHeader("Set-Cookie", newAccessToken.toString());

    return ResponseEntity.ok().build();
  }
}
