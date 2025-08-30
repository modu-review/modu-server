package com.modureview.service.utill;


import com.modureview.enums.errors.MypageErrorCode;
import com.modureview.exception.CustomException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3UploadService {

  private final S3Client s3Client;

  @Value("${aws.bucket}")
  private String bucket;

  public String upload(MultipartFile file, String dirName, String uniqueFileName) {
    String key = dirName + "/" + uniqueFileName;

    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .contentType(file.getContentType())
          .contentLength(file.getSize())
          .build();

      s3Client.putObject(putObjectRequest,
          RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

      return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(key)).toString();

    } catch (IOException | S3Exception e) {
      log.error("S3 파일 업로드에 실패했습니다. 파일: {}", key, e);
      throw new CustomException(MypageErrorCode.FILE_UPLOAD_FAILED);
    }
  }


  public void deleteImage(String fileUrl) {
    if (!StringUtils.hasText(fileUrl)) {
      return;
    }
    try {
      String key = fileUrl.substring(fileUrl.indexOf(bucket + "/") + bucket.length() + 1);

      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      s3Client.deleteObject(deleteObjectRequest);
      log.info("S3 파일 삭제 성공: {}", key);
    } catch (Exception e) {
      log.error("S3 파일 삭제 중 오류 발생. URL: {}", fileUrl, e);
    }
  }
}
