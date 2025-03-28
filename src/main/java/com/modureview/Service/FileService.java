package com.modureview.Service;


import com.modureview.Dto.File.Response.DownloadFileResponseDto;
import com.modureview.Dto.File.Response.UploadFileResponseDto;
import com.modureview.Entity.Board;
import com.modureview.Entity.FileEntity;
import com.modureview.Repository.BoardRepository;
import com.modureview.Repository.FileRepository;
import com.modureview.Service.Utill.CustomUserDetails;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

  private final FileRepository fileRepository;
  private final BoardRepository boardRepository;
  private final BoardService boardService;

  @Value("${project.folderPath}")
  private String FOLDER_PATH;

  public List<UploadFileResponseDto> upload_file(Long boardId, List<MultipartFile> multipartFiles) throws IOException {
    Board board = boardService.checkExistBoard(boardId);
    List<FileEntity> fileEntities = new ArrayList<>();
    for (MultipartFile multipartFile : multipartFiles){
      //진짜 파일 이름 가져오기
      String fileName = multipartFile.getOriginalFilename();
      //랜덤 UUID 를 넣는다
      String fileRandomId = UUID.randomUUID().toString();
      // 파일 이름 만들기 ex)POST_galleryBoardId_fileRandomId.확장자
      String filePath = "POST_" + board.getId() + "_" + fileRandomId.concat(fileName.substring(fileName.lastIndexOf(".")));
      //File.separator : os에 따른 구분자
      String fileResourcePath = FOLDER_PATH + File.separator+filePath;
      //create Folder if not created
      File f = new File(FOLDER_PATH);
      if (!f.exists()) {
        //CustomLogicException으로 바꿔야할 것
        if (f.mkdir()) {
          System.out.println("Directory created successfully.");
        } else {
          System.err.println("Failed to create directory.");
        }
      }
      //file copy in folder
      Files.copy(multipartFile.getInputStream(), Paths.get(fileResourcePath));

      /*create File Entity => dto로 변경 예정
       * 변경이유  1. type가 어떤 걸로 들어오는지 확힐하게 알 수 있다.
       *           2. 유지보수 입장에서 DTO만 파악하면 어떤게 들어오는지 확실히 알수있어서 좋다
       *           3. 나중에 ENTITY 의 변경점이 생길때 DTO만 변경가능하다?
       * */
      FileEntity saveFile = FileEntity.builder()
          .originFileName(multipartFile.getOriginalFilename())
          .fileType(multipartFile.getContentType())
          .filePath(filePath)
          .build();
      /*
       * UploadFileRequestDto request = UploadFileRequestDto.builder()
       *                       .originFileName(multipartFile.getOriginalFilename())
       *                           @Pattern을 넣어서 위험하지 않은 값을 넣을 수 있지 않을까? 솎아내기
       *                       .fileType(multipartFile.getContentType())
       *                       .filePath(filePath)
       *                       .build();
       *
       * saveFile.setMappingGalleryBoard(galleryBoard);
       *
       * fileEntities.add(fileRepository.save(saveFile));*/

      //연관관계 매핑
      /*saveFile.setMappingBoard(board);*/
      //File Entity 저장 및 DTO 로 변환 및 전송
      fileEntities.add(fileRepository.save(saveFile));
    }
    return fileEntities.stream()
        .map(UploadFileResponseDto::fromEntity)
        .collect(Collectors.toList());
  }
  public DownloadFileResponseDto download_file(Long fileId, CustomUserDetails user) throws IOException {
    FileEntity file = checkFileId(fileId);
    //중복 사용
    if(!file.getBoard().getUser().getEmail().equals(user.getUserEmail())){
      //throw new CustomLogicException(ExceptionCode.USER_NOT_MATCH_WRITER);
      throw new RuntimeException("USER_NOT_MATCH_WRITER");
    }
    String filePath  = FOLDER_PATH + file.getFilePath();
    String contentType = determineContentType(file.getFileType());
    byte[] content = Files.readAllBytes(new File(filePath).toPath());
    return DownloadFileResponseDto.fromFileResource(file,contentType,content);
  }
  public void delete_file(Long fileId,CustomUserDetails user) throws IOException {
    FileEntity file = checkFileId(fileId);
    //중복 사용
    if(!file.getBoard().getUser().getEmail().equals(user.getUserEmail())){
      //throw new CustomLogicException(ExceptionCode.USER_NOT_MATCH_WRITER);
      throw new RuntimeException("USER_NOT_MATCH_WRITER");
    }
    String filePath = FOLDER_PATH + File.separator + file.getFilePath();
    File physicalFile = new File(filePath);
    if (physicalFile.exists()) {
      if (physicalFile.delete()) {
        System.out.println("파일이 성공적으로 삭제되었습니다.");
      } else {
        System.err.println("파일 삭제에 실패했습니다: " + physicalFile.getAbsolutePath());
      }
    } else {
      System.out.println("삭제하려는 파일이 존재하지 않습니다: " + physicalFile.getAbsolutePath());
    }
    fileRepository.delete(file);
  }
  private String determineContentType(String contentType) {
    return switch (contentType) {
      case "image/png" -> MediaType.IMAGE_PNG_VALUE;
      case "image/jpeg" -> MediaType.IMAGE_JPEG_VALUE;
      case "text/plain" -> MediaType.TEXT_PLAIN_VALUE;
      default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
    };
  }
  private FileEntity checkFileId(Long fileId){
    return fileRepository.findById(fileId).orElseThrow(
        () -> /*new CustomLogicException(ExceptionCode.RESOURCE_NOT_FOUND, "FILE", "FileId", String.valueOf(fileId)*/
    new RuntimeException("File not found with id: " + fileId));
  }
}
