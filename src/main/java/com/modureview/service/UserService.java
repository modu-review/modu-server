package com.modureview.service;

import com.modureview.entity.User;
import com.modureview.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public Long findUserId(String email){
    User user = userRepository.findByEmail(email).get();
    return user.getId();
  }

  public Long findUserIdByNickname(String nickname) {
    User user = userRepository.findByNickname(nickname).get();
    return user.getId();
  }

  public User findUserByNickname(String nickname) {
  }
}
