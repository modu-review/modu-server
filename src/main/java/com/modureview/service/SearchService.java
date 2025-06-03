package com.modureview.service;


import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.SearchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
  private final BoardRepository boardRepository;

  public Page<Board> boardSearch(String keyword, int page, String sort) {
    Sort sortCriteria;

    switch (sort.toLowerCase()) {
      case "hotbookmarks" -> sortCriteria = Sort.by(Direction.DESC, "bookmarksCount");
      case "hotcomments" -> sortCriteria = Sort.by(Direction.DESC, "commentsCount");
      case "recent" -> sortCriteria = Sort.by(Direction.DESC, "createdAt");
      default -> sortCriteria = Sort.by(Direction.DESC, "createdAt");
    }

    Pageable pageable = PageRequest.of(page, 6, sortCriteria);
    if (StringUtils.hasText(keyword)) {
      return searchRepository.findByKeyword(keyword, pageable);
    } else {
      throw new CustomException(BoardErrorCode.BOARD_SEARCH_KEYWORD_NOTFOUND);
    }
  }

  public Slice<Board> getCategoryBoard(Category category, Long cursor, String sort) {
    Pageable pageable = PageRequest.of(0, 6);
    log.info("category == {}", category);
    log.info("sort == {}", sort);
    switch (sort) {
      case "recent":
        if (cursor == 0) {
          if (category == Category.all) {
            return searchRepository.findAllOrderByCreatedAtFirst(pageable);
          } else {
            return searchRepository.findByCategoryOrderByCreatedAtFirst(category, pageable);
          }
        } else {
          Board board = foundBoard(cursor);
          if (category == Category.all) {
            return searchRepository.findAllOrderByCreatedAt(board.getCreatedAt(),
                board.getId(), pageable);
          } else {
            return searchRepository.findByCategoryOrderByCreatedAt(category,
                board.getCreatedAt(), board.getId(), pageable);
          }

        }
      case "hotbookmarks":
        if (cursor == 0) {
          if (category == Category.all) {
            return searchRepository.findAllOrderByBookmarksCountFirst(pageable);
          } else {
            return searchRepository.findByCategoryOrderByBookmarksCountFirst(category,
                pageable);
          }
        } else {
          Board board = foundBoard(cursor);
          if (category == Category.all) {
            return searchRepository.findAllOrderByBookmarksCount(board.getBookmarksCount(),
                board.getId(), pageable);
          } else {
            return searchRepository.findByCategoryOrderByBookmarksCount(category,
                board.getBookmarksCount(), board.getId(), pageable);
          }
        }
      case "hotcomments":
        if (cursor == 0) {
          if (category == Category.all) {
            return searchRepository.findAllOrderByCommentsCountFirst(pageable);
          } else {
            return searchRepository.findByCategoryOrderByCommentsCountFirst(category,
                pageable);
          }

        } else {
          Board board = foundBoard(cursor);
          if (category == Category.all) {
            searchRepository.findAllOrderByCommentsCount(board.getCommentsCount(),
                board.getId(), pageable);
          } else {
            return searchRepository.findByCategoryOrderByCommentsCount(category,
                board.getCommentsCount(), board.getId(), pageable);
          }

        }
      default:
        return searchRepository.findByCategoryOrderByCreatedAtFirst(category, pageable);
    }

  }

  public Board foundBoard(Long id) {
    return boardRepository.findById(id)
        .orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_ID_NOTFOUND));
  }
}