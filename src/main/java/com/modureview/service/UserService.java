package com.modureview.service;

import com.modureview.entity.User;
import com.modureview.repository.UserRepository;
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
