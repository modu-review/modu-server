package com.modureview.Controller;

import com.modureview.Entity.User;
import com.modureview.Repository.UserRepository;
import com.modureview.Service.CustomOAuth2UserService;
import com.modureview.Service.UserService;
import com.modureview.Service.Utill.JwtTokenizer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuth2UserController {

  private final CustomOAuth2UserService OAuth2UserService;
  private final UserService userService;
  private final UserRepository userRepository;
  private final JwtTokenizer jwtTokenizer;

  @GetMapping("/user/oauth2/login")
  public ResponseEntity<?> oauth2UserLogin(@RequestParam("user_email") String user_email, HttpServletResponse response){
    try{
      User user = userService.getUserByEmail(user_email);

      jwtTokenizer.reissueTokenPair(response,user);

      return ResponseEntity.status(HttpStatus.OK).body(user_email);

    }catch(Exception e){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user email");
    }
  }

}
