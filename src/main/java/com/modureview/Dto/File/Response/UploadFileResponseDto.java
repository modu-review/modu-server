package com.modureview.Dto.File.Response;

import com.modureview.Entity.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFileResponseDto {
  private Long fileId;
  private String originFileName;
  private String filePath;
  private String fileType;

  public static UploadFileResponseDto fromEntity(FileEntity file){
    return UploadFileResponseDto.builder()
        .fileId(file.getId())
        .originFileName(file.getOriginFileName())
        .filePath(file.getFilePath())
        .fileType(file.getFileType())
        .build();
  }
}
