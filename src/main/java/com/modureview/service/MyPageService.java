package com.modureview.service;

import com.modureview.entity.Board;
import com.modureview.enums.errors.UserErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.MyPageRepository;
import com.modureview.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

  private final UserRepository userRepository;
  private final MyPageRepository myPageRepository;

  public Page<Board> myPageBoard(String email, int page) {
    Sort sortCriteria = Sort.by(Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page - 1, 6, sortCriteria);

    if (userRepository.findByEmail(email).isPresent()) {
      return myPageRepository.findBoardByAuthorEmail(email, pageable);
    }
    throw new CustomException(UserErrorCode.USER_NOT_FOUND);
  }
}
