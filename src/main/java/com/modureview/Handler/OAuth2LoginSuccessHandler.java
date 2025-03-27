package com.modureview.Handler;


import com.modureview.Entity.User;
import com.modureview.Service.Utill.CustomOAuth2User;
import com.modureview.Service.Utill.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
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
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    log.info("OAuth2 Login 성공");
    //TODO : 해야할것

    try{
      CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
      //UUID user_uuid = userEntity.getUuid();
      //

      String redirectUrl = frontUrl + "/oauth2/redirect?user_email=" + oAuth2User.getEmail();
      log.info("====================================OAuth2LoginSuccessHandler====================================");
      log.info("oAuth2User: {} ", oAuth2User);
      //log.info("userInfoJwt = {}",userInfoJwt);
      log.info("리다이렉트 URL: {}", redirectUrl);
      //log.info("JWT: {}", userInfoJwt);
      //log.info("ROLE: {}", role);
      getRedirectStrategy().sendRedirect(request,response,redirectUrl);
      log.info("====================================OAuth2LoginSuccessHandler====================================");
    }catch(Exception e){
      log.error("OAuth2 로그인 성공 처리 중 오류 발생",e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"internal Server Error");
    }
  }
}

