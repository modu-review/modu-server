package com.modureview.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final com.modureview.interceptor.JwtTokenInterceptor jwtTokenInterceptor;

  public WebMvcConfig(com.modureview.interceptor.JwtTokenInterceptor jwtTokenInterceptor) {
    this.jwtTokenInterceptor = jwtTokenInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtTokenInterceptor)
        .excludePathPatterns(
            "/user/oauth2/**",
            "/token/refresh",
            "/reviews/best"
        );
  }
}
