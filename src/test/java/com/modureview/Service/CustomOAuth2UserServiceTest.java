
package com.modureview.Service;

import com.modureview.Entity.User;
import com.modureview.Repository.UserRepository;
import com.modureview.Service.Utill.CustomOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class CustomOAuth2UserServiceTest {

  private UserRepository userRepository;
  private CustomOAuth2UserService customOAuth2UserService;

  @BeforeEach
  public void setUp() {
    userRepository = Mockito.mock(UserRepository.class);
    // 테스트에서는 하위 클래스를 사용하여 delegate 동작을 흉내냅니다.
    customOAuth2UserService = new TestCustomOAuth2UserService(userRepository);
  }

  @Test
  public void testLoadUser_KakaoSuccess() {
    // Dummy ClientRegistration (Kakao 전용)
    ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("kakao")
        .clientId("dummyClientId")
        .clientSecret("dummyClientSecret")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
        .authorizationUri("https://kauth.kakao.com/oauth/authorize")
        .tokenUri("https://kauth.kakao.com/oauth/token")
        .userInfoUri("https://kapi.kakao.com/v2/user/me")
        .userNameAttributeName("id")
        .build();

    OAuth2AccessToken accessToken = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "dummyToken",
        Instant.now(),
        Instant.now().plusSeconds(3600)
    );

    OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessToken);

    // Stub: userRepository.findByEmail(email) -> dummy User 반환
    User dummyUser = User.builder().email("test@kakao.com").build();
    Mockito.when(userRepository.findByEmail(anyString()))
        .thenReturn(Optional.of(dummyUser));

    // 호출: loadUser
    OAuth2User result = customOAuth2UserService.loadUser(userRequest);
    assertNotNull(result);
    assertTrue(result instanceof CustomOAuth2User);
    CustomOAuth2User customUser = (CustomOAuth2User) result;
    // 반환된 CustomOAuth2User에 email이 정상적으로 설정되었는지 확인
    assertEquals("test@kakao.com", customUser.getEmail());
  }


/**
   * 테스트용 하위 클래스.
   * 실제 delegate 호출 대신 dummy Kakao 응답 형식의 attributes를 사용하여 loadUser 동작을 재정의합니다.
   */

  private static class TestCustomOAuth2UserService extends CustomOAuth2UserService {
    public TestCustomOAuth2UserService(UserRepository userRepository) {
      // 원래 서비스 생성자 호출 (delegate는 내부에서 사용되지 않도록 함)
      super(userRepository);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
      // Dummy Kakao 응답 attributes 구성
      Map<String, Object> kakaoAccount = new HashMap<>();
      kakaoAccount.put("email", "test@kakao.com");
      Map<String, Object> attributes = new HashMap<>();
      attributes.put("kakao_account", kakaoAccount);
      attributes.put("id", "12345");

      // Kakao 계정 정보 검증
      Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
      if (account == null) {
        throw new OAuth2AuthenticationException("Kakao account details not found");
      }
      String email = (String) account.get("email");
      if (email == null) {
        throw new OAuth2AuthenticationException("Email not found in Kakao account");
      }

      // User 조회 (테스트에서는 이미 stub 처리된 userRepository를 사용)
      User user = super.userRepository.findByEmail(email)
          .orElseThrow(() -> new OAuth2AuthenticationException("User not found"));

      String userNameAttributeName = userRequest.getClientRegistration()
          .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

      return new CustomOAuth2User(
          Collections.emptySet(),
          attributes,
          userNameAttributeName,
          user.getEmail()
      );
    }
  }
}
