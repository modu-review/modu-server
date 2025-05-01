package com.modureview.service;

import com.modureview.dto.Response.SearchResponseDto;
import com.modureview.entity.Board;
import com.modureview.repository.SearchBoardRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class SearchBoardService {
  private final SearchBoardRepository searchBoardRepository;

  public Page<SearchResponseDto> search_keyword(int page,String sortBy, Direction direction,String keyword){
    List<String> allowed = List.of("id","view_count","createdAt");
    if(!allowed.contains(sortBy)){
      sortBy = "id";
    }
    Pageable pageable = PageRequest.of(page,10,Sort.by(direction,sortBy));

    Page<Board> boardPage = searchBoardRepository.searchBoardByKeyword(keyword,pageable);

    return boardPage.map(SearchResponseDto::fromEntity);
  }
}
