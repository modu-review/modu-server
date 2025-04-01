package com.modureview.controller;

import com.modureview.entity.User;

import com.modureview.service.UserService;
import com.modureview.service.utill.JwtTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuth2UserController {


  private final UserService userService;
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
  @PostMapping("/api/user/logout")
  public ResponseEntity<?> logout(HttpServletResponse response, HttpServletRequest request){
    log.info("--------------logout------------------");
    jwtTokenizer.removeToken(response,request);
    log.info("--------------logout------------------");
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }
}
