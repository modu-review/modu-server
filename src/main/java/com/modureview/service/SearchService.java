package com.modureview.service;


import com.modureview.entity.Board;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.SearchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

  private final SearchRepository searchRepository;

  public Page<Board> boardSearch(String keyword, int page, String sort) {
    Sort sortCriteria;
    switch (sort.toLowerCase()) {
      case "hotbookmarks" -> sortCriteria = Sort.by(Direction.DESC, "bookmarksCount");
      case "hotcomments" -> sortCriteria = Sort.by(Direction.DESC, "commentsCount");
      case "recent" -> sortCriteria = Sort.by(Direction.DESC, "createdAt");
      default -> sortCriteria = Sort.by(Direction.DESC, "createdAt");
    }

    Pageable pageable = PageRequest.of(page - 1, 6, sortCriteria);
    if (StringUtils.hasText(keyword)) {
      return searchRepository.findByKeyword(keyword, pageable);
    } else {
      throw new CustomException(BoardErrorCode.BOARD_SEARCH_KEYWORD_NOTFOUND);
    }
  }
}