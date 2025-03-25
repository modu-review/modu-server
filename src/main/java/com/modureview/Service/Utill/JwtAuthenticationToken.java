package com.modureview.Service.Utill;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
  private String token;
  private Object principal;
  private Object credentials;

  public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities,Object principal,Object credentials){
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    this.setAuthenticated(true);
  }
  @Override
  public Object getCredentials(){return null;}
}
