package com.modureview.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.modureview.Entity.User;
import com.modureview.Repository.UserRepository;
import com.modureview.Service.CustomOAuth2UserService;
import com.modureview.Service.Utill.CustomOAuth2User;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.Builder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;

public class CustomOAuth2UserServiceTest2 {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomOAuth2UserService customOAuth2UserService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // 헬퍼: 더미 ClientRegistration 생성 (Kakao)
  private ClientRegistration createDummyKakaoRegistration() {
    return ClientRegistration.withRegistrationId("kakao")
        .clientId("dummy-client-id")
        .clientSecret("dummy-secret")
        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .scope("account_email")
        .authorizationUri("https://kauth.kakao.com/oauth/authorize")
        .tokenUri("https://kauth.kakao.com/oauth/token")
        .userInfoUri("https://kapi.kakao.com/v2/user/me")
        .userNameAttributeName("id")
        .clientName("Kakao")
        .build();
  }

  // 헬퍼: 더미 OAuth2AccessToken 생성
  private OAuth2AccessToken createDummyAccessToken() {
    return new OAuth2AccessToken(
        TokenType.BEARER,
        "dummy-access-token",
        Instant.now(),
        Instant.now().plusSeconds(3600)
    );
  }

  // 헬퍼: 더미 OAuth2UserRequest 생성 (accessToken이 null이 아니도록 함)
  private OAuth2UserRequest createDummyOAuth2UserRequest() {
    ClientRegistration registration = createDummyKakaoRegistration();
    OAuth2AccessToken accessToken = createDummyAccessToken();
    return new OAuth2UserRequest(registration, accessToken);
  }

  // 헬퍼: 더미 CustomOAuth2User 생성 (Kakao API 응답 모방)
  private CustomOAuth2User createDummyCustomOAuth2User() {
    Map<String, Object> attributes = new HashMap<>();
    // Kakao 응답의 "id"
    attributes.put("id", 123456);
    // kakao_account 정보 설정
    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", "test@example.com");
    Map<String, Object> profile = new HashMap<>();
    profile.put("nickname", "TestUser");
    kakaoAccount.put("profile", profile);
    attributes.put("kakao_account", kakaoAccount);
    // CustomOAuth2User 생성: authorities, attributes, userNameAttributeName, email
    return new CustomOAuth2User(
        Set.of(new SimpleGrantedAuthority("ROLE_USER")),
        attributes,
        "id",
        "test@example.com"
    );
  }

  @Test
  public void testLoadUser_WhenUserExists() {
    OAuth2UserRequest userRequest = createDummyOAuth2UserRequest();
    CustomOAuth2User dummyCustomUser = createDummyCustomOAuth2User();

    // 기존 사용자가 이미 저장된 경우
    User existingUser = User.builder().email("test@example.com").build();
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

    // spy 처리: loadUser() 메서드를 오버라이드하여 dummyCustomUser를 반환
    CustomOAuth2UserService spyService = spy(customOAuth2UserService);
    doReturn(dummyCustomUser).when(spyService).loadUser(userRequest);

    // when
    var oAuth2User = spyService.loadUser(userRequest);

    // then
    assertNotNull(oAuth2User);
    assertTrue(oAuth2User instanceof CustomOAuth2User);
    CustomOAuth2User customUser = (CustomOAuth2User) oAuth2User;
    assertEquals("test@example.com", customUser.getEmail());
    // 기존 사용자이므로 save()는 호출되지 않아야 합니다.
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  public void testLoadUser_WhenUserDoesNotExist() {
    OAuth2UserRequest userRequest = createDummyOAuth2UserRequest();
    CustomOAuth2User dummyCustomUser = createDummyCustomOAuth2User();

    // 기존 사용자가 없는 경우: 신규 사용자 생성
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
    User newUser = User.builder().email("test@example.com").build();
    when(userRepository.save(any(User.class))).thenReturn(newUser);

    CustomOAuth2UserService spyService = spy(customOAuth2UserService);
    doReturn(dummyCustomUser).when(spyService).loadUser(userRequest);

    // when
    var oAuth2User = spyService.loadUser(userRequest);

    // then
    assertNotNull(oAuth2User);
    assertTrue(oAuth2User instanceof CustomOAuth2User);
    CustomOAuth2User customUser = (CustomOAuth2User) oAuth2User;
    assertEquals("test@example.com", customUser.getEmail());
    // 신규 사용자 저장이 호출되어야 합니다.
    verify(userRepository, times(1)).save(any(User.class));
  }
}