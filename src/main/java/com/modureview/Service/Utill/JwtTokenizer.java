package com.modureview.Service.Utill;

import com.modureview.Entity.RefreshToken;
import com.modureview.Entity.User;
import com.modureview.Service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenizer {
  private final RefreshTokenService refreshTokenService;
  private final byte[] accessSecretKey;
  private final byte[] refreshSecretKey;

  public static Long ACCESS_TOKEN_EXPIRATION_TIME = (Long)(2*30*10*1000L);
  public static Long REFRESH_TOKEN_EXPIRATION_TIME = (Long)(2*60*60*1000L);


  public JwtTokenizer(@Value("${jwt.secretKey}")String accessSecretKey,@Value("${jwt.refreshKey}")String refreshSecretKey,RefreshTokenService refreshTokenService){
    this.refreshTokenService = refreshTokenService;
    this.accessSecretKey = accessSecretKey.getBytes(StandardCharsets.UTF_8);
    this.refreshSecretKey = refreshSecretKey.getBytes(StandardCharsets.UTF_8);
  }

  

  public String createToken(Long Id,Long expire,byte[] secretKey){
    Claims claims = Jwts.claims().setSubject(Id.toString());
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime()+expire))
        .signWith(getSigningKey(secretKey))
        .compact();
  }

  public String createAccessToken(User user){
    return createToken(user.getId(),ACCESS_TOKEN_EXPIRATION_TIME,accessSecretKey);
  }

  public String createRefreshToken(User user){
    return createToken(user.getId(),REFRESH_TOKEN_EXPIRATION_TIME,refreshSecretKey);
  }

  public Claims parseToken(String token,byte[] secretKey){
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey(secretKey))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public Claims parseAccessToken(String accessToken){
    return parseToken(accessToken,accessSecretKey);
  }

  public Claims parseRefreshToken(String refreshToken){
    return parseToken(refreshToken,refreshSecretKey);
  }


  private static Key getSigningKey(byte[] secretKey) {
    return Keys.hmacShaKeyFor(secretKey);
  }

  public void String reissueTokenPair(HttpServletResponse response , User user){
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

    addRefreshTokenCookie(response,refreshToken,REFRESH_TOKEN_EXPIRATION_TIME);
    addAccessTokenCookie(response,accessToken,ACCESS_TOKEN_EXPIRATION_TIME);
  }
  
  private void addRefreshTokenCookie(HttpServletResponse response,String tokenValue, Long expirationTime){
    Cookie refreshToken = new Cookie("refreshToken", tokenValue);
    refreshToken.setHttpOnly(true);
    refreshToken.setPath("/");
    refreshToken.setMaxAge(Math.toIntExact(expirationTime));
    refreshToken.setSecure(true);
    refreshToken.setAttribute("SameSite","Lax");
    log.info("Setting Cookie - Name : {} , Value : {}",refreshToken.getName(),refreshToken);
    response.addCookie(refreshToken);
  }

  private void addAccessTokenCookie(HttpServletResponse response,String tokenValue, Long expirationTime){
    Cookie accessToken = new Cookie("accessToken", tokenValue);
    accessToken.setHttpOnly(true);
    accessToken.setPath("/");
    accessToken.setMaxAge(Math.toIntExact(expirationTime));
    accessToken.setSecure(true);
    accessToken.setAttribute("SameSite","Lax");
    log.info("Setting Cookie - Name : {} , Value : {}",accessToken.getName(),accessToken);
    response.addCookie(accessToken);
  }

  private void addUserCookie(HttpServletResponse response,User user){
    Cookie userCookie = new Cookie("userCookie",user.getEmail());
    userCookie.setHttpOnly(false);
    userCookie.setPath("/");
    userCookie.setMaxAge(60*60*1000);
    userCookie.setSecure(false);
    userCookie.setAttribute("SameSite","Lax");
    log.info("Setting Cookie - Name : {}m Value : {} ",userCookie.getName(),userCookie);
    response.addCookie(userCookie);
  }
  public boolean validateToken(String refreshToken){
    try{
      Jwts.parserBuilder().setSigningKey(getSigningKey(refreshSecretKey)).build().parseClaimsJws(refreshToken);
      return true;
    }catch (Exception e){
      return false;
    }
  }

  public void removeToken(HttpServletResponse response, HttpServletRequest request){
    Cookie[] cookies = request.getCookies();
    if(cookies != null){
      for (Cookie cookie : cookies) {
        if("refreshToken".equals(cookie.getName()) || "accessToken".equals(cookie.getName()) || "userCookie".equals(cookie.getName())){
          cookie.setValue("");
          cookie.setPath("/");
          cookie.setMaxAge(0);
          response.addCookie(cookie);
        }
      }
    }
  }

}
