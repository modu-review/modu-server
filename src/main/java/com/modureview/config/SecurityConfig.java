package com.modureview.config;

import com.modureview.hanlder.SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private SuccessHandler successHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  public SecurityConfig(CustomAuthenticationEntryPoint authenticationEntryPoint,
      SuccessHandler successHandler) {
    this.successHandler = successHandler;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/user/oauth2/**", "/token/refresh", "/reviews/best", "/reviews",
                "/search", "favicon.io")
            .permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
        )
        .oauth2Login(oauth2 -> oauth2
            .successHandler(successHandler)
        );
    return http.build();
  }
}

