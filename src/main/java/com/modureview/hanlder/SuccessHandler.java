package com.modureview.hanlder;


import com.modureview.entity.User;
import com.modureview.repository.UserRepository;
import com.modureview.service.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenService jwtTokenService;
  private final UserRepository userRepository;
  @Value("${frontend.url}")
  private String frontURL;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
    String email = (String) kakaoAccount.get("email");

    User user = userRepository.findByEmail(email)
        .orElseGet(() -> userRepository.save(User.builder().email(email).build()));

    String redirectUrl = frontURL + "/oauth2/redirect?user_email=" + email;
    log.info("login info {}",email);

    response.sendRedirect(redirectUrl);
  }
}