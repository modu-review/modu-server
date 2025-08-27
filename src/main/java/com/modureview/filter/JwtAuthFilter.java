package com.modureview.filter;

import com.modureview.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtTokenService jwtTokenService;

  private static final List<String> EXEMPT_URIS = Arrays.asList(
      "/user/oauth2/**",
      "/token/refresh",
      "/reviews/best",
      "/reviews",
      "/reviews/*/comments",
      "/reviews/*/bookmarks",
      "/search",
      "/users/login",
      "/users/*/reviews",
      "/favicon.io"
  );

  @Override
  protected boolean shouldNotFilterAsyncDispatch(){
    return false;
  }

  @Override
  protected boolean shouldNotFilterErrorDispatch(){
    return false;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException, IOException {
    log.info("Filter request url: {}", request.getRequestURL());

    String requestURI = request.getRequestURI();
    boolean isExempt = EXEMPT_URIS.stream().anyMatch(uri -> requestURI.startsWith(uri));

    if (isExempt) {
      filterChain.doFilter(request, response);
      return;
    }

    Optional<String> accessTokenOpt = jwtTokenService.extractCookie(request, "accessToken");

    if (accessTokenOpt.isPresent()) {
      String accessToken = accessTokenOpt.get();
      jwtTokenService.validateToken(accessToken);

      Cookie[] cookies = request.getCookies();
      String userEmail = "";
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if ("userEmail".equals(cookie.getName())) {
            userEmail = cookie.getValue();
            break;
          }
        }
      }

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userEmail , null, Collections.emptyList());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);

  }
}