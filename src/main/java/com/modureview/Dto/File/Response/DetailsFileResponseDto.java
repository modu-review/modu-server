package com.modureview.Dto.File.Response;

import com.modureview.Entity.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailsFileResponseDto {
  private Long fileId;
  private String originalFileName;
  private String fileType;

  public static DetailsFileResponseDto fromEntity(FileEntity file){
    return DetailsFileResponseDto.builder()
        .fileId(file.getId())
        .originalFileName(file.getOriginFileName())
        .fileType(file.getFileType())
        .build();

  }

}