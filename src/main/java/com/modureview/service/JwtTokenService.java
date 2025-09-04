package com.modureview.service;

import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.jwtError.InvalidTokenException;
import com.modureview.exception.jwtError.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
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
@lombok.RequiredArgsConstructor
public class JwtTokenService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }
  private final Long accessTokenExpire = 60 * 60L;
  private final Long refreshTokenExpire = 30 * 24 * 60 * 60L;
  private final com.modureview.repository.UserRepository userRepository;

  public List<ResponseCookie> loginTokenIssue(String userEmail) {
    String nickname = userRepository.findByEmail(userEmail)
        .map(com.modureview.entity.User::getNickname)
        .orElse("익명");

    return List.of(
        createAccessToken(userEmail),
        createRefreshToken(userEmail),
        createUserEmailCookie(userEmail),
        createNicknameCookie(nickname)
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

  public ResponseCookie createNicknameCookie(String nickname) {
    return createCookie("userNickname", nickname, refreshTokenExpire, true);
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
    String safeValue = value;
    // RFC6265 allows only US-ASCII in cookie value; encode when non-ASCII is present
    if (!isAscii(value)) {
      try {
        safeValue = java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
      } catch (Exception e) {
        // Fallback to empty if encoding somehow fails
        safeValue = "";
      }
    }

    return ResponseCookie.from(name, safeValue)
        .httpOnly(httpOnly)
        .secure(true)
        .sameSite("LAX")
        .path("/")
        .maxAge(maxAge)
        .domain(".modu-review.com")
        .build();
  }

  private boolean isAscii(String s) {
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) > 0x7F) return false;
    }
    return true;
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

  public ResponseCookie expireCookie(ResponseCookie responseCookie) {
    return ResponseCookie.from(responseCookie.getName(), responseCookie.getValue())
        .httpOnly(true)
        .secure(true)
        .sameSite("LAX")
        .path("/")
        .maxAge(0)
        .domain(".modu-review.com")
        .build();
  }
}
