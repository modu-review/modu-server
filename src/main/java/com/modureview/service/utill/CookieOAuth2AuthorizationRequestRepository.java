package com.modureview.service.utill;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class CookieOAuth2AuthorizationRequestRepository implements
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  public static final String OAUTH2_AUTH_REQUEST_COOKIE_NAME = "oauth2_auth_request";
  private static final int COOKIE_EXPIRE_SECONDS = 180;

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    return CookieUtil.getCookie(request, OAUTH2_AUTH_REQUEST_COOKIE_NAME)
        .map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
        .orElse(null);
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request, HttpServletResponse response) {
    if (authorizationRequest == null) {
      CookieUtil.deleteCookie(request, response, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
      return;
    }
    CookieUtil.addCookie(response, OAUTH2_AUTH_REQUEST_COOKIE_NAME,
        CookieUtil.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
      HttpServletResponse response) {
    OAuth2AuthorizationRequest authorizationRequest = this.loadAuthorizationRequest(request);
    CookieUtil.deleteCookie(request, response, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
    return authorizationRequest;
  }
}