package com.modureview.service;

import com.modureview.entity.Board;
import com.modureview.entity.User;
import com.modureview.enums.errors.MypageErrorCode;
import com.modureview.enums.errors.UserErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.repository.MyPageBookMarkRepository;
import com.modureview.repository.MyPageRepository;
import com.modureview.repository.UserRepository;
import com.modureview.service.utill.S3UploadService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
  private static final String PROFILE_IMAGE_DIR = "profile-images";

  private final S3UploadService s3UploadService;

  private static final List<String> ALLOWED_EXTENSIONS = List.of("jpeg", "jpg", "png");
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

  public Page<Board> myPageBoard(String nickname, int page) {
    Sort sortCriteria = Sort.by(Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page - 1, 6, sortCriteria);

    if (userRepository.findByNickname(nickname).isPresent()) {
      return myPageRepository.findBoardByNickname(nickname, pageable);
    }
    throw new CustomException(UserErrorCode.USER_NOT_FOUND);
  }

  public Page<Board> myPageBookmark(String nickname, int page) {
    Sort sortCriteria = Sort.by(Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page - 1, 6, sortCriteria);
    if (userRepository.findByNickname(nickname).isEmpty()) {
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }

    Page<Long> boardIdPage = myPageBookMarkRepository.findBookMarksByNickname(nickname, pageable);
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

  @Transactional
  public String updateProfileImage(String email, MultipartFile file) {
    validateImage(file);
    validateFileSize(file);
    User user = validateUser(email);

    String originalFilename = file.getOriginalFilename();
    String extension = StringUtils.getFilenameExtension(originalFilename);
    String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
    log.info("생성된 고유 파일명: {}", uniqueFileName);
    String imageUrl = null;

    try {
      imageUrl = s3UploadService.upload(file, PROFILE_IMAGE_DIR, uniqueFileName);
      log.info("S3 업로드 성공. URL: {}", imageUrl);

      user.updateProfileImageUrl(imageUrl);

    } catch (Exception e) {
      log.error("프로필 이미지 업데이트 중 예외 발생. S3 롤백을 시작합니다.", e);

      if (imageUrl != null) {
        log.warn("DB 오류로 인해 S3에 업로드된 파일을 롤백(삭제)합니다. URL: {}", imageUrl);
        s3UploadService.deleteImage(imageUrl);
      }

      throw new CustomException(MypageErrorCode.FILE_UPLOAD_FAILED);
    }

    return imageUrl;
  }

  private User validateUser(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    log.info("사용자 확인 완료 = {}", email);

    return user;
  }

  private void validateImage(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    if (file.isEmpty() || !StringUtils.hasText(fileName)) {

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


  public String getProfileImage(String email) {
    userRepository.findByEmail(email).ifPresent(user -> {
     user.getProfile();
   });
    return "no-thumbnail.png";
  }

  public void deleteProfileImage(String email) {
    userRepository.findByEmail(email).ifPresent(user -> {
      user.updateProfileImageUrl("no-thumbnail.png");
    });
  }
}