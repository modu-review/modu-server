package com.modureview.Service;

import com.modureview.Entity.User;
import com.modureview.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
  private final UserRepository userRepository;
  public User getUserByEmail(String email){
    return userRepository.findByEmail(email)
        .orElseThrow( () -> new UsernameNotFoundException("User not found"));
  }
}
