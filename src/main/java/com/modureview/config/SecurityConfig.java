package com.modureview.config;

import com.modureview.fIlter.JwtAuthenticationFilter;
import com.modureview.handler.OAuth2LoginFailureHandler;
import com.modureview.handler.OAuth2LoginSuccessHandler;
import com.modureview.service.CustomOAuth2UserService;
import com.modureview.service.utill.CustomAuthenticationEntryPoint;
import com.modureview.service.utill.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenizer jwtTokenizer;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  // OAuth2 설정
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

  // 허용 URL 등...
  String [] allAllowPage = new String[]{};
  String[] userAllowPage = new String[]{};

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v0/user/checkEmail", "/user/oauth2/**", "/api/token/refresh","/api/user/logout").permitAll()
            .anyRequest().authenticated()
        )
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .httpBasic(auth -> auth.disable())
        .formLogin(auth -> auth.disable())
        .logout(auth -> auth.disable())
        .sessionManagement(auth -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // JwtAuthenticationFilter 생성 시 CustomAuthenticationEntryPoint도 주입
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer,customAuthenticationEntryPoint),
            UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
        .oauth2Login(oauth2 -> oauth2
            .authorizationEndpoint(endpoint -> endpoint.baseUri("/oauth2/authorize"))
            .redirectionEndpoint(endpoint -> endpoint.baseUri("/login/oauth2/code/**"))
            .userInfoEndpoint(endpoint -> endpoint.userService(customOAuth2UserService))
            .successHandler(oAuth2LoginSuccessHandler)
            .failureHandler(oAuth2LoginFailureHandler)
        );
    return http.build();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder(){
    return new BCryptPasswordEncoder();
  }
}