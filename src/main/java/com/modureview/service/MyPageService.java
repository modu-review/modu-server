package com.modureview.service;

import com.modureview.entity.Board;
import com.modureview.enums.errors.MypageErrorCode;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

  private final UserRepository userRepository;
  private final MyPageRepository myPageRepository;
  private final MyPageBookMarkRepository myPageBookMarkRepository;

  private static final List<String> ALLOWED_EXTENSIONS = List.of("jpeg", "jpg", "png");
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

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

  public String updateProfileImage(MultipartFile file) {
    validateImage(file);
    validateFileSize(file);

    return "url";
  }
  private void validateImage(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    if (file == null || file.isEmpty() || !StringUtils.hasText(fileName)) {

      log.warn("파일이 비어있습니다.");
      throw new CustomException(MypageErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    String extension = StringUtils.getFilenameExtension(fileName);

    if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
      log.warn("지원하지 않는 이미지 형식입니다: {}", extension);
      throw new CustomException(MypageErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }
  }

  private void validateFileSize(MultipartFile imageFile) {
    if (imageFile.getSize() > MAX_FILE_SIZE) {
      log.warn("파일 크기 초과: {} bytes (최대: {} bytes)", imageFile.getSize(), MAX_FILE_SIZE);
      throw new CustomException(MypageErrorCode.FILE_SIZE_EXCEEDED);
    }
  }
}

