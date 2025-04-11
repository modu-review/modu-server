package com.modureview.service;

import com.modureview.entity.User;
import com.modureview.repository.UserRepository;
import com.modureview.service.utill.CustomOAuth2User;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  protected final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("CustomOAuth2UserService 진입 - OAuth2 로그인 요청");
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    Map<String, Object> attributes = oAuth2User.getAttributes();

    // Kakao 정보 추출
    String providerId = String.valueOf(attributes.get("id"));
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    String email = null;
    String nickname = null;
    if (kakaoAccount != null) {
      email = (String) kakaoAccount.get("email");
      Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
      if (profile != null) {
        nickname = (String) profile.get("nickname");
      }
    }
    log.info("Kakao 사용자 정보 - providerId: {}, email: {}, nickname: {}", providerId, email, nickname);

    User user = getUser(email);

    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    return new CustomOAuth2User(
        authorities,
        attributes,
        userNameAttributeName,
        user.getEmail()
    );
  }

  private User getUser(String email) {
    return userRepository.findByEmail(email).orElseGet(() -> {
      User newUser = User.builder()
          .email(email)
          .build();
      return userRepository.save(newUser);
    });
  }
}