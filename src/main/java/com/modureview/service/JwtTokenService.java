package com.modureview.service;

import com.modureview.enums.JwtErrorCode;
import com.modureview.exception.jwtError.InvalidTokenException;
import com.modureview.exception.jwtError.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtTokenService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }
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
    String accessToken = createJwtToken(userEmail, accessTokenExpire);
    return createCookie("accessToken", accessToken, accessTokenExpire, true);
  }

  public ResponseCookie createRefreshToken(String userEmail) {
    String refreshToken = createJwtToken(userEmail, refreshTokenExpire);
    return createCookie("refreshToken", refreshToken, refreshTokenExpire, true);
  }

  public ResponseCookie createUserEmailCookie(String userEmail) {
    return createCookie("userEmail", userEmail, refreshTokenExpire, true);
  }

  public void validateToken(String token) {
    parseAndThrow(token);
  }

  public ResponseCookie reIssueAccessToken(String token){
    return createAccessToken(token);
  }

  private void parseAndThrow(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSecretKey())
          .build()
          .parseClaimsJws(token);
      log.info("parseEnd");
    } catch (ExpiredJwtException e) {
      throw new TokenExpiredException(JwtErrorCode.UNAUTHORIZED);
    } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
      throw new InvalidTokenException(JwtErrorCode.FORBIDDEN);
    } catch (IllegalArgumentException e) {
      throw new InvalidTokenException(JwtErrorCode.UNAUTHORIZED);
    }
  }

  private String createJwtToken(String subject, Long expireSeconds) {
    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expireSeconds * 1000))
        .signWith(getSecretKey())
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

  public Optional<String> extractCookie(HttpServletRequest request, String cookieName) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }
    return Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(cookieName))
        .map(Cookie::getValue)
        .findFirst();
  }

  public String extractSubject(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }
}

