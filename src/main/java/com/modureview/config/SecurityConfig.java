package com.modureview.config;

import com.modureview.filter.JwtAuthFilter;
import com.modureview.hanlder.SuccessHandler;
import com.modureview.service.JwtTokenService;
import com.modureview.service.utill.CookieOAuth2AuthorizationRequestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final SuccessHandler successHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final CookieOAuth2AuthorizationRequestRepository cookieAuthRequestRepository;
  private final JwtTokenService jwtTokenService;

  @Value("${frontend.url}")
  private String frontendUrl;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      CorsConfigurationSource corsConfigurationSource) throws Exception {
      http
          .cors(cors -> cors.configurationSource(corsConfigurationSource))
          .csrf(csrf -> csrf.disable())
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
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
                  "/users/me/reviews",
                  "favicon.io",
                  "/users/*/profileImage"
              )
              .permitAll()
              .requestMatchers("/reviews/**")
              .permitAll()
              .requestMatchers(HttpMethod.OPTIONS, "/notifications/stream").permitAll()
              .requestMatchers(HttpMethod.GET, "/notifications/stream").authenticated()
              .requestMatchers(HttpMethod.GET,"/users/me/reviews").authenticated()
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
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(frontendUrl,"https://dev.modu-review.com:3000","http://dev.modu-review.com:3000"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

