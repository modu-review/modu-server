package com.modureview.config;

import com.modureview.filter.JwtAuthFilter;
import com.modureview.hanlder.SuccessHandler;
import com.modureview.service.JwtTokenService;
import com.modureview.service.utill.CookieOAuth2AuthorizationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final SuccessHandler successHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final CookieOAuth2AuthorizationRequestRepository cookieAuthRequestRepository;
  private final JwtTokenService jwtTokenService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/user/oauth2/**",
                "/token/refresh",
                "/reviews/best",
                "/reviews",
                "/reviews/*/comments",
                "/reviews/*/bookmarks",
                "/search",
                "/users/login",
                "/users/*/reviews",
                "favicon.io"
            )
            .permitAll()
            .requestMatchers("/reviews/**")
            .permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
        )
        .oauth2Login(oauth2 -> oauth2
            .authorizationEndpoint(endpoint -> endpoint
                .authorizationRequestRepository(cookieAuthRequestRepository)
            )
            .successHandler(successHandler)
        )
        .addFilterBefore(new JwtAuthFilter(jwtTokenService),
            UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }
}

