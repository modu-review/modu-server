package com.modureview.Controller;


import com.modureview.Dto.File.Response.DownloadFileResponseDto;
import com.modureview.Dto.File.Response.UploadFileResponseDto;
import com.modureview.Service.FileService;
import com.modureview.Service.Utill.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("api/v0/Board/{Board}/file")
@RequiredArgsConstructor
public class FIleController{

  private final FileService fileService;

  @PostMapping("/upload")
  public ResponseEntity<List<UploadFileResponseDto>> upload_file(
      @PathVariable("Board") Long boardId,
      @RequestParam("file") List<MultipartFile> files) throws IOException {
    return ResponseEntity.status(HttpStatus.CREATED).body(fileService.upload_file(boardId, files));
  }
  @GetMapping("/download")

  public ResponseEntity<Resource> download_file(
      @RequestParam("fileId")Long fileId,
      @AuthenticationPrincipal CustomUserDetails user) throws IOException{
    DownloadFileResponseDto downloadDto = fileService.download_file(fileId,user);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.parseMediaType(downloadDto.getFileType()))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName = \"" + downloadDto.getFileName() + "\"")
        .body(new ByteArrayResource(downloadDto.getContent()));
  }

  @DeleteMapping("/delete")
  public ResponseEntity<Long> delete_file(
      @RequestParam("fileId") Long fileId,
      @AuthenticationPrincipal CustomUserDetails user) throws IOException {
    fileService.delete_file(fileId,user);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

}
