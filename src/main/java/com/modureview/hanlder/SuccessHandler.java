package com.modureview.hanlder;


import com.modureview.entity.User;
import com.modureview.repository.UserRepository;
import com.modureview.service.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenService jwtTokenService;
  private final UserRepository userRepository;
  @Value("${frontend.url}")
  private String frontURL;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    User oAuth2User = (User) authentication.getPrincipal();

    String email = oAuth2User.getEmail();

    User user = userRepository.findByEmail(email)
        .orElseGet(() -> userRepository.save(User.builder().email(email).build()));

    jwtTokenService.loginTokenIssue(user.getEmail())
        .forEach(cookie -> response.addHeader("Set-Cookie", cookie.toString()));

    String redirectUrl = frontURL + "/oauth2/redirect?user_email=" + oAuth2User.getEmail();
  }
}