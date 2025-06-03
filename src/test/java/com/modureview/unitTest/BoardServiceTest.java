package com.modureview.unitTest;

import com.modureview.config.AwsS3Config;
import com.modureview.dto.request.BoardSaveRequest;
import com.modureview.repository.BoardRepository;
import com.modureview.service.BoardService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

public class BoardServiceTest {

  @InjectMocks
  private BoardService boardService;

  @Mock
  private BoardRepository boardRepository;

  @Mock
  private AwsS3Config awsS3Config;


  @Spy
  private BoardService spyBoardService; // for partial mocking

  @Test
  void extractImageInfo_extractsUuidsFromImgTags() {
    // given
    String html = "<p>Some content</p><img src=\"https://d1izijuzr22yly.cloudfront.net/56b2dffa-428c-45d2-b8a3-80c8e2c9bb1b.png\" />";
    BoardSaveRequest request = Mockito.mock(BoardSaveRequest.class);

    BoardService service = Mockito.spy(new BoardService(boardRepository,
        awsS3Config));

    // when
    Mockito.when(request.content()).thenReturn(html);
    service.extractImageInfo(request);

    // then
    Mockito.verify(service).extractImageInfo(request);
  }

}
