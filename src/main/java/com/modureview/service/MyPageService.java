package com.modureview.service;

import com.modureview.entity.Board;
import com.modureview.enums.errors.UserErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.MyPageBookMarkRepository;
import com.modureview.repository.MyPageRepository;
import com.modureview.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
  private final MyPageBookMarkRepository myPageBookMarkRepository;

  public Page<Board> myPageBoard(String email, int page) {
    Sort sortCriteria = Sort.by(Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page - 1, 6, sortCriteria);

    if (userRepository.findByEmail(email).isPresent()) {
      return myPageRepository.findBoardByAuthorEmail(email, pageable);
    }
    throw new CustomException(UserErrorCode.USER_NOT_FOUND);
  }

  public Page<Board> myPageBookmark(String email, int page) {
    Sort sortCriteria = Sort.by(Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page - 1, 6, sortCriteria);
    if (userRepository.findByEmail(email).isEmpty()) {
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }

    Page<Long> boardIdPage = myPageBookMarkRepository.findBookMarksByEmail(email, pageable);
    List<Long> boardIds = boardIdPage.getContent();
    List<Board> boardList = myPageRepository.findAllById(boardIds);
    Map<Long, Board> boardMap = boardList.stream()
        .collect(Collectors.toMap(Board::getId, Function.identity()));
    List<Board> sortedBoards = boardIds.stream()
        .map(boardMap::get)
        .filter(Objects::nonNull)
        .toList();
    return new PageImpl<>(sortedBoards, pageable, boardIdPage.getTotalElements());


  }


}
