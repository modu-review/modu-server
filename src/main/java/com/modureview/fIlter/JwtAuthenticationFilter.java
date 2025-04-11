package com.modureview.fIlter;

import com.modureview.service.utill.CustomUserDetails;
import com.modureview.service.utill.JwtAuthenticationToken;
import com.modureview.service.utill.JwtTokenizer;
import com.modureview.service.utill.CustomAuthenticationEntryPoint;
import com.modureview.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenizer jwtTokenizer;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    return path.startsWith("/user/oauth2") || path.startsWith("/token/refresh")
        || path.startsWith("/api/user/logout") || path.startsWith("/reviews/best")
        || path.startsWith("/reviews");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    String accessToken = getAccessToken(request);
    log.info("===============doFilterInternal=============");
    log.info("UserRequest url ::{}", request.getRequestURI());

    try {
      if (StringUtils.hasText(accessToken)) {
        log.info("I have a AccessToken : {}", accessToken);
        Claims claims = jwtTokenizer.parseAccessToken(accessToken);
        log.info("after claims : {}", claims);
        setAuthenticationToContext(claims);
        log.info("claims :: {}", claims);
      } else {
        throw new JwtAuthenticationException("Access token is missing from access token");
      }
    } catch (ExpiredJwtException ex) {
      log.info("expired Token Exception");
      SecurityContextHolder.clearContext();
      authenticationEntryPoint.commence(request, response,
          new AuthenticationException(ex.getMessage(), ex) {
          });
      return;
    } catch (AuthenticationException ex) {
      log.info("Authentication Exception");
      SecurityContextHolder.clearContext();
      authenticationEntryPoint.commence(request, response, ex);
      return;
    }

    filterChain.doFilter(request, response);
    log.info("===============doFilterInternal=============");
  }

  //TODO:
  private void setAuthenticationToContext(Claims claims) {
    String userEmail = claims.getSubject();

    //Role role = Role.valueOf(claims.get("roles", String.class));
    List<GrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority(userEmail));

    CustomUserDetails userDetails = new CustomUserDetails(userEmail);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, authorities
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private String getAccessToken(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("accessToken".equals(cookie.getName())) {
          log.info("Cookie :: {}", cookie.getValue());
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
