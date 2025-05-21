package com.modureview.service;


import com.modureview.config.AwsS3Config;
import com.modureview.dto.BoardDetailResponse;
import com.modureview.entity.Board;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.enums.errors.ImageSaveErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.exception.imageSaveError.CreatPresignedUrlError;
import com.modureview.exception.imageSaveError.CreateUuidError;
import com.modureview.repository.BoardRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
  private final BoardRepository boardRepository;
  private final AwsS3Config awsS3Config;

  public BoardDetailResponse boardDetail(Long boardId) {
    Board findBoard = boardRepository.findById(boardId).orElseThrow(
        () -> new CustomException(BoardErrorCode.BOARD_ID_NOTFOUND)
    );
    BoardDetailResponse response = BoardDetailResponse.builder()
        .board_id(findBoard.getId())
        .title(findBoard.getTitle())
        .category(findBoard.getCategory())
        .author(findBoard.getAuthorEmail())
        .create_At(findBoard.getCreatedAt())
        .content(findBoard.getContent())
        .comment_count(findBoard.getCommentsCount())
        .bookmarks(findBoard.getBookmarksCount())
        .build();

    return response;
    }

  public String createImageID() {
    try {
      return UUID.randomUUID().toString();
    } catch (Exception e) {
      throw new CreateUuidError(ImageSaveErrorCode.CAN_NOT_CREATE_UUID);
    }
  }

  public String createPresignedURL(String key) {
    try {
      Region region = Region.of(awsS3Config.getRegion());

      AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
          awsS3Config.getCredentials().getAccessKey(),
          awsS3Config.getCredentials().getSecretKey()
      );

      PutObjectRequest objectRequest = PutObjectRequest.builder()
          .bucket(awsS3Config.getBucket())
          .key(key)
          .contentType("image/png")
          .build();

      PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))
          .putObjectRequest(objectRequest)
          .build();

      try (S3Presigner presigner = S3Presigner.builder()
          .region(region)
          .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
          .build()) {

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
      }

    } catch (Exception e) {
      throw new CreatPresignedUrlError(ImageSaveErrorCode.CAN_NOT_CREATE_PRESIGNED_URL);
    }
  }

}
