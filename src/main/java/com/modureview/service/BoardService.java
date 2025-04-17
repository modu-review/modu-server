package com.modureview.service;

import com.modureview.dto.request.BookmarkRequest;
import com.modureview.entity.User;
import com.modureview.exception.user.UserNotFoundException;
import com.modureview.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BoardService {

  private final UserRepository userRepository;

  public void addBookmark(BookmarkRequest request) {
    User user = userRepository.findByEmail(request.userEmail())
        .orElseThrow(UserNotFoundException::new);

  }
}
