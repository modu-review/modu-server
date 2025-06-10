package com.modureview.config;

import com.modureview.interceptor.JwtTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final JwtTokenInterceptor jwtTokenInterceptor;

  public WebMvcConfig(JwtTokenInterceptor jwtTokenInterceptor) {
    this.jwtTokenInterceptor = jwtTokenInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtTokenInterceptor)
        .excludePathPatterns(
            "/user/oauth2/**",
            "/token/refresh",
            "/error/**",
            "/login/result",
            "/reviews/best",
            "/reviews",
            "/reviews/**",
            "/search",
            "favicon.io"
        );
  }
}
