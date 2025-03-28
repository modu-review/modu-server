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
public class DownloadFileResponseDto {
  private String fileName;
  private String fileType;
  private byte[] content;

  public static DownloadFileResponseDto fromFileResource(FileEntity file,String contentType,byte[] content){
    return DownloadFileResponseDto.builder()
        .fileName(file.getOriginFileName())
        .fileType(contentType)
        .content(content)
        .build();
  }
}
