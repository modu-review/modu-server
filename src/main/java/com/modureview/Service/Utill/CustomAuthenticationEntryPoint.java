package com.modureview.Service.Utill;

import com.nimbusds.jose.shaded.gson.Gson;
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
    log.error("Unauthorized error: {}", authException.getMessage());

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
    response.setCharacterEncoding("UTF-8");

    // JSON 형식의 응답 직접 구성
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", "Unauthorized");
    errorResponse.put("message", authException.getMessage());
    errorResponse.put("path", request.getRequestURI());
    errorResponse.put("timestamp", LocalDateTime.now().toString());

    String json = new Gson().toJson(errorResponse);
    response.getWriter().write(json);
  }
}