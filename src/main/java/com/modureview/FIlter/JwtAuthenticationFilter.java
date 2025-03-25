package com.modureview.FIlter;

import com.modureview.Service.Utill.CustomUserDetails;
import com.modureview.Service.Utill.JwtAuthenticationToken;
import com.modureview.Service.Utill.JwtTokenizer;
import com.modureview.Service.Utill.CustomAuthenticationEntryPoint;
import com.modureview.Exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
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
    return path.startsWith("/user/oauth2") || path.startsWith("/api/v0/token/refresh") || path.startsWith("/api/v0/user/logout");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = getJwtFromRequest(request);
    log.info("===============doFilterInternal=============");
    log.info("UserRequest url ::{}", request.getRequestURI());

    try {
      if (StringUtils.hasText(token)) {
        Claims claims = jwtTokenizer.parseAccessToken(token);
        setAuthenticationToContext(claims);
        log.info("claims :: {}", claims);
      } else {
        throw new JwtAuthenticationException("Access token is missing");
      }
    } catch (AuthenticationException ex) {
      SecurityContextHolder.clearContext();
      authenticationEntryPoint.commence(request, response, ex);
      return; // 필터 체인 중단
    }

    filterChain.doFilter(request, response);
    log.info("===============doFilterInternal=============");
  }

  //TODO:
  private void setAuthenticationToContext(Claims claims) {
    //UUID uuid = UUID.fromString(claims.getSubject());
    String userEmail = claims.getSubject();

    //Role role = Role.valueOf(claims.get("roles", String.class));
    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(userEmail));

    CustomUserDetails userDetails = new CustomUserDetails("","" , userEmail, authorities);
    Authentication authentication = new JwtAuthenticationToken(authorities, userDetails, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
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
