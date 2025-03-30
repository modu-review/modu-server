package com.modureview.controller;


import com.modureview.entity.User;
import com.modureview.repository.UserRepository;
import com.modureview.service.utill.JwtTokenizer;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/token")
public class RefreshTokenController {
  private final JwtTokenizer jwtTokenizer;
  private final UserRepository userRepository;

  @GetMapping("/refresh")
  public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response){
    if(jwtTokenizer.validateToken(refreshToken)){
      Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
      User user = userRepository.findByEmail(claims.getSubject()).orElseThrow(
          ()->new RuntimeException("User not found with email: "+claims.getSubject())
      );
      jwtTokenizer.reissueTokenPair(response,user);
      return ResponseEntity.status(HttpStatus.CREATED).body("access token refreshed successfully");
    }else{
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}
