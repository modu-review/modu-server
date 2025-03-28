package com.modureview.Service.Utill;


import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

  private final String username;
  private final String password;
  private final String userEmail;
  private final Collection<? extends GrantedAuthority> authorities;

  // 기존 생성자 (네 개의 인자를 받음)
  public CustomUserDetails(String username, String password, String userEmail, Collection<? extends GrantedAuthority> authorities) {
    this.username = username;
    this.password = password;
    this.userEmail = userEmail;
    this.authorities = authorities;
  }

  // 오버로딩 생성자: 이메일만 받아서 나머지는 기본값으로 설정
  public CustomUserDetails(String userEmail) {
    this(userEmail, "", userEmail, Collections.emptyList());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}