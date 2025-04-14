package com.modureview.handler;


import com.modureview.service.loginUtil.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


  @Value("${app.cors.allowed-origins}")
  private String frontUrl;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    log.info("OAuth2 Login 성공");

    try {
      CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
      String redirectUrl = frontUrl + "/oauth2/redirect?user_email=" + oAuth2User.getEmail();
      log.info("리다이렉트 URL: {}", redirectUrl);
      getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    } catch (Exception e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "internal Server Error");
    }
  }
}

