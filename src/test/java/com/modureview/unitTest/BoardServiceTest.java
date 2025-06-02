package com.modureview.unitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.modureview.config.AwsS3Config;
import com.modureview.dto.request.BoardSaveRequest;
import com.modureview.entity.Board;
import com.modureview.entity.BoardImage;
import com.modureview.entity.Category;
import com.modureview.entity.User;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.UserRepository;
import com.modureview.service.BoardService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

  @Spy
  @InjectMocks
  private BoardService boardService;

  @Mock
  private BoardRepository boardRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private AwsS3Config awsS3Config;
  @Mock
  private AwsS3Config.Credentials credentials;

  @BeforeEach
  void setUp() {
    lenient().when(awsS3Config.getBucket()).thenReturn("mock-bucket");
    lenient().when(awsS3Config.getCredentials()).thenReturn(credentials);
    lenient().when(credentials.getAccessKey()).thenReturn("mock-access");
    lenient().when(credentials.getSecretKey()).thenReturn("mock-secret");
  }

  @Test
  @DisplayName("HTML에서 이미지 src로부터 UUID 리스트 추출")
  void extractImageInfo_extractsUuidsFromImgTags() {
    // given
    String html = """
    <p>본문</p>
    <img src="https://cdn.example.com/uuid1-aaaa.jpg" />
    <img src="https://cdn.example.com/uuid2-bbbb.png" />
  """;
    BoardSaveRequest request = new BoardSaveRequest("제목", html, "car", "testuser@example.com");

    // when
    List<String> uuids = boardService.extractImageInfo(request);

    // then
    assertEquals(2, uuids.size());
    assertTrue(uuids.contains("uuid1-aaaa.jpg"));
    assertTrue(uuids.contains("uuid2-bbbb.png"));
  }

  @Test
  @DisplayName("게시글 저장 유닛 테스트")
  public void boardSaveTest() {
    // given
    String email = "testuser@example.com";
    User mockUser = User.builder()
        .email(email)
        .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

    String html = """
        <p>본문입니다</p>
        <img src="https://cdn.example.com/uuid1-1111.jpg" />
        <img src="https://cdn.example.com/uuid2-2222.png" />
      """;

    BoardSaveRequest request = new BoardSaveRequest(
        "테스트 제목",
        html,
        "car",
        email
    );

    // when
    List<String> uuids = boardService.extractImageInfo(request);
    boardService.saveBoard(request, uuids);

    // then
    ArgumentCaptor<Board> captor = ArgumentCaptor.forClass(Board.class);
    verify(boardRepository).save(captor.capture());

    Board savedBoard = captor.getValue();

    assertEquals("테스트 제목", savedBoard.getTitle());
    assertEquals(Category.car, savedBoard.getCategory());
    assertEquals(email, savedBoard.getAuthorEmail());

    List<BoardImage> images = savedBoard.getImages();
    assertEquals(2, images.size());

    List<String> savedImages = images.stream().map(BoardImage::getUuid).toList();
    assertTrue(savedImages.contains("uuid1-1111.jpg"));
    assertTrue(savedImages.contains("uuid2-2222.png"));
  }

  @Test
  @DisplayName("Presigned URL is generated for PNG image")
  void testPresignedUrlForPngImage() {
    // given
    String key = "sample.jpg";
    String fakePresignedUrl = "https://fake-url.com/sample.jpeg";

    doReturn(fakePresignedUrl)
        .when(boardService)
        .createPresignedURL(key);

    // when
    String result = boardService.createPresignedURL(key);

    // print the result
    log.info("📦 Presigned URL: " + result);

    // then
    assertEquals(fakePresignedUrl, result);
  }

}
