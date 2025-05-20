package com.modureview.service;



import com.modureview.dto.request.BoardSearchRequest;
import com.modureview.dto.response.BoardSearchResponse;
import com.modureview.dto.response.CustomPageResponse;
import com.modureview.entity.Board;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.BoardSearchRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
@Transactional
public class BoardSearchService {
  private final BoardSearchRepository boardSearchRepository;

  public CustomPageResponse<BoardSearchResponse> boardSearch(BoardSearchRequest req) {
    Sort sortCriteria;

    switch (req.sort().toLowerCase()) {
      case "hotbookmarks" -> sortCriteria = Sort.by(Direction.DESC, "bookmarksCount");
      case "hotcomments" -> sortCriteria = Sort.by(Direction.DESC, "commentsCount");
      case "recent" -> sortCriteria = Sort.by(Direction.DESC, "createdAt");
      default -> sortCriteria = Sort.by(Direction.DESC, "createdAt");
    }

    Pageable pageable = PageRequest.of(req.page(), 9, sortCriteria);
    if (StringUtils.hasText(req.keyword())) {
      Page<Board> boardPage = boardSearchRepository.findByKeyword(req.keyword(), pageable);
      List<BoardSearchResponse> List_SearchBoard = boardPage.getContent().stream()
          .map(BoardSearchResponse::fromEntity)
          .toList();
      return new CustomPageResponse<>(
          List_SearchBoard,
          boardPage.getNumber() + 1,
          boardPage.getTotalPages()

      );
    } else {
      throw new CustomException(BoardErrorCode.BOARD_SEARCH_KEYWORD_NOTFOUND);
    }
  }
}