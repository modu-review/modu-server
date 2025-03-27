package com.modureview.Service.Utill;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

  private final String username;
  private final String userEmail;
  private final String password;
  private final List<GrantedAuthority> authorities;

  public CustomUserDetails(String username, String password, String userEmail,
      List<GrantedAuthority> authorities) {
    this.username = username;
    this.userEmail = userEmail;
    this.password = password;
    this.authorities = Collections.singletonList(new SimpleGrantedAuthority(userEmail));
  }
}