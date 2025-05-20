package com.modureview.interceptor;

import com.modureview.enums.errors.JwtErrorCode;
import com.modureview.exception.jwtError.InvalidTokenException;
import com.modureview.service.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

  private final JwtTokenService jwtTokenService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    log.info("request url: {}", request.getRequestURL());

    Optional<String> accessTokenOpt = jwtTokenService.extractCookie(request, "accessToken");
    Optional<String> refreshTokenOpt = jwtTokenService.extractCookie(request, "refreshToken");

    if (accessTokenOpt.isPresent()) {
      String accessToken = accessTokenOpt.get();
      jwtTokenService.validateToken(accessToken);
      return true;
    }

    throw new InvalidTokenException(JwtErrorCode.UNAUTHORIZED);
  }

}
