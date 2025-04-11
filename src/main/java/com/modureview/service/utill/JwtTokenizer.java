package com.modureview.service.utill;

import com.modureview.entity.RefreshToken;
import com.modureview.entity.User;
import com.modureview.service.RefreshTokenService;
import com.modureview.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenizer {

  private final RefreshTokenService refreshTokenService;
  private final UserService userService;
  private final byte[] accessSecretKey;
  private final byte[] refreshSecretKey;
  public static final long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 60L;
  public static final long REFRESH_TOKEN_EXPIRATION_TIME = 30 * 24 * 60 * 60L;
  private final String domain = "localhost";

  public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecretKey,
      @Value("${jwt.refreshKey}") String refreshSecretKey, RefreshTokenService refreshTokenService,
      UserService userService) {
    this.refreshTokenService = refreshTokenService;
    this.accessSecretKey = accessSecretKey.getBytes(StandardCharsets.UTF_8);
    this.refreshSecretKey = refreshSecretKey.getBytes(StandardCharsets.UTF_8);
    this.userService = userService;
  }

  public String createToken(String email, Long expire, byte[] secretKey) {
    Claims claims = Jwts.claims().setSubject(email);
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + expire * 1000))
        .signWith(getSigningKey(secretKey))
        .compact();
  }

  public String createAccessToken(User user) {
    return createToken(user.getEmail(), ACCESS_TOKEN_EXPIRATION_TIME, accessSecretKey);
  }

  public String createRefreshToken(User user) {
    return createToken(user.getEmail(), REFRESH_TOKEN_EXPIRATION_TIME, refreshSecretKey);
  }

  public Claims parseToken(String token, byte[] secretKey) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey(secretKey))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public Claims parseAccessToken(String accessToken) {
    return parseToken(accessToken, accessSecretKey);
  }

  public Claims parseRefreshToken(String refreshToken) {
    return parseToken(refreshToken, refreshSecretKey);
  }


  private static Key getSigningKey(byte[] secretKey) {
    return Keys.hmacShaKeyFor(secretKey);
  }

  public void reissueTokenPair(HttpServletResponse response, User user) {
    String accessToken = createAccessToken(user);
    String refreshToken = createRefreshToken(user);
    RefreshToken refreshTokenObj = refreshTokenService.getRefreshTokenByUserId(user.getId())
        .orElseGet(() -> {
          RefreshToken newRefreshToken = new RefreshToken();
          newRefreshToken.setUser(user);
          return newRefreshToken;
        });
    refreshTokenObj.setValue(refreshToken);
    refreshTokenService.saveRefreshToken(refreshTokenObj);

    addAccessToken(response, accessToken, ACCESS_TOKEN_EXPIRATION_TIME);
    addRefreshToken(response, refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
    addUserCookie(response, user, ACCESS_TOKEN_EXPIRATION_TIME);

  }

  private void addRefreshToken(HttpServletResponse response, String tokenValue,
      Long expirationTime) {
    ResponseCookie refreshToken = ResponseCookie.from("refreshToken", tokenValue)
        .httpOnly(true)
        .secure(true)
        .sameSite("Lax")
        .path("/")
        .maxAge(expirationTime)
        .domain(".modu-review.com")
        .build();

    log.info("Setting Cookie - Name : {} , Value : {}", refreshToken.getName(),
        refreshToken.getValue());

    response.addHeader("Set-Cookie", refreshToken.toString());

  }

  public void addAccessToken(HttpServletResponse response, String tokenValue,
      Long expirationTime) {
    ResponseCookie accessToken = ResponseCookie.from("accessToken", tokenValue)
        .httpOnly(true)
        .secure(true)
        .sameSite("Lax")
        .path("/")
        .maxAge(expirationTime)
        .domain(".modu-review.com")
        .build();

    log.info("Setting Cookie - Name : {} , Value : {}", accessToken.getName(),
        accessToken);

    response.addHeader("Set-Cookie", accessToken.toString());
  }

  public void addUserCookie(HttpServletResponse response, User user, Long expirationTime) {
    ResponseCookie userCookie = ResponseCookie.from("UserEmail", user.getEmail())
        .httpOnly(true)
        .secure(true)
        .sameSite("Lax")
        .path("/")
        .maxAge(expirationTime)
        .domain(".modu-review.com")
        .build();

    log.info("Setting Cookie - Name : {}, Value : {}", userCookie.getName(), userCookie.getValue());

    response.addHeader("Set-Cookie", userCookie.toString());

  }

  public boolean validateToken(String refreshToken) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey(refreshSecretKey)).build()
          .parseClaimsJws(refreshToken);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void removeToken(HttpServletResponse response, HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName()) || "accessToken".equals(cookie.getName())
            || "userCookie".equals(cookie.getName())) {
          cookie.setValue("");
          cookie.setPath("/");
          cookie.setMaxAge(0);
          response.addCookie(cookie);
        }
      }
    }
  }

  public void removeTokenFromDB(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("accessToken".equals(cookie.getName())) {
          Claims claims = parseAccessToken(cookie.getValue());
          String userEmail = claims.getSubject();
          refreshTokenService.removeRefreshTokenDB(userService.getUserByEmail(userEmail));
        }
      }
    }
  }
}






