package com.modureview.controller;

import com.modureview.service.JwtTokenService;
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

    return ResponseEntity.ok("토큰이 발행 완료 : " + email);

  }
}
