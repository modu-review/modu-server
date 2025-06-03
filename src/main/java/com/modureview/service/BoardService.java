package com.modureview.service;


import com.modureview.config.AwsS3Config;
import com.modureview.dto.BoardDetailResponse;
import com.modureview.dto.request.BoardSaveRequest;
import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.enums.errors.ImageSaveErrorCode;
import com.modureview.exception.BoardError.BoardSaveError;
import com.modureview.exception.BoardError.ImageSrcExtractError;
import com.modureview.exception.BoardError.NotAllowedHtmlError;
import com.modureview.exception.CustomException;
import com.modureview.exception.imageSaveError.CreatPresignedUrlError;
import com.modureview.exception.imageSaveError.CreateUuidError;
import com.modureview.repository.BoardRepository;
import com.modureview.service.utils.HtmlSanitizerPolicy;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
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
@Slf4j
public class BoardService {
  private final BoardRepository boardRepository;
  private final AwsS3Config awsS3Config;
  private final PolicyFactory sanitizer = HtmlSanitizerPolicy.POLICY;

  @Value("${custom.message.image.extract-fail}")
  private String imageExtractFailMessage;

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
      log.info("presigned생성중 에러 : {}", e);
      throw new CreatPresignedUrlError(ImageSaveErrorCode.CAN_NOT_CREATE_PRESIGNED_URL);
    }
  }

  public void htmlSanitizer(BoardSaveRequest request) {
    try {
      sanitizer.sanitize(request.content());
    } catch (Exception e) {
      log.info("xss white list 통과 실패 : {}", e);
      throw new NotAllowedHtmlError(BoardErrorCode.NOT_ALLOWED_HTML_ERROR);
    }

  }

  @Transactional
  public void saveBoard(BoardSaveRequest request) {
    Board board = Board.builder()
        .title(request.title())
        .content(request.content())
        .authorEmail(request.authorEmail())
        .category(Category.valueOf(request.category()))
        .build();

    try {
      boardRepository.save(board);
    } catch (DataAccessException e) {
      log.error("Board 저장 중 DB 접근 에러 발생", e);
      throw new BoardSaveError(BoardErrorCode.BOARD_SAVE_ERROR);
    }

  }

  public void extractImageInfo(BoardSaveRequest request) {
    String html = sanitizer.sanitize(request.content());

    Document doc = Jsoup.parse(html);
    Elements imgTags = doc.select("img");

    for (Element img : imgTags) {
      String src = img.attr("src");

      if (src != null && !src.isBlank()) {
        String uuid = extractUuidFromUrl(src);
        //TODO
        //추출한 uuid를 기반으로 board테이블에 cascade기반 업로드
      }
    }
  }

  private String extractUuidFromUrl(String url) {
    try {
      String[] parts = url.split("/");
      return parts[parts.length - 1];
    } catch (ImageSrcExtractError e) {
      log.info("image 주소 추출중 오류 발생 = {}", e.getMessage());
      return imageExtractFailMessage;
    }
  }

}
