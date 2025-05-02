package com.modureview.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

  @Value("${jwt.secret}")
  private String secretKey;
  private final Long accessTokenExpire = 60 * 60L;
  private final Long refreshTokenExpire = 30 * 24 * 60 * 60L;

  public List<ResponseCookie> loginTokenIssue(String userEmail) {
    return List.of(
        createAccessToken(userEmail),
        createRefreshToken(userEmail),
        createUserEmailCookie(userEmail)
    );
  }

  public ResponseCookie createAccessToken(String userEmail) {
    String accessToken = createJwtToken(userEmail, accessTokenExpire, secretKey);
    return createCookie("accessToken", accessToken, accessTokenExpire, true);
  }

  public ResponseCookie createRefreshToken(String userEmail) {
    String refreshToken = createJwtToken(userEmail, refreshTokenExpire, secretKey);
    return createCookie("refreshToken", refreshToken, refreshTokenExpire, true);
  }

  public ResponseCookie createUserEmailCookie(String userEmail) {
    return createCookie("userEmail", userEmail, refreshTokenExpire, true);
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private String createJwtToken(String subject, Long expireSeconds, String key) {
    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expireSeconds * 1000))
        .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
        .compact();
  }

  private ResponseCookie createCookie(String name, String value, long maxAge, boolean httpOnly) {
    return ResponseCookie.from(name, value)
        .httpOnly(httpOnly)
        .secure(true)
        .sameSite("None")
        .path("/")
        .maxAge(maxAge)
        .domain(".modu-review.com")
        .build();
  }
}
