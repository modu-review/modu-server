package com.modureview.service.loginUtil;

import com.nimbusds.jose.shaded.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setCharacterEncoding("UTF-8");

    Map<String, Object> errorResponse = new HashMap<>();

    if (authException.getCause() instanceof ExpiredJwtException expiredJwt) {
      errorResponse.put("message", "Token expired: " + expiredJwt.getMessage());
    } else {
      errorResponse.put("message", authException.getMessage());
    }

    errorResponse.put("error", "Unauthorized");
    errorResponse.put("path", request.getRequestURI());
    errorResponse.put("timestamp", LocalDateTime.now().toString());

    String json = new Gson().toJson(errorResponse);
    response.getWriter().write(json);
  }

}