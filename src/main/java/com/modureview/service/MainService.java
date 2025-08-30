package com.modureview.service;

import com.modureview.dto.response.BestReviewResponse;
import com.modureview.entity.Board;
import com.modureview.repository.BoardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {

  private final BoardRepository boardRepository;

  public List<Board> latest6Reviews() {
    return boardRepository.findTop6ByOrderByCreatedAtAsc();

  }
}
