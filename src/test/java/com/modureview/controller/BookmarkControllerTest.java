package com.modureview.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.modureview.dto.request.BookmarkRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class BookmarkControllerTest {

  @Autowired
  private BookmarkController bookmarkController;

  @Test
  @DisplayName("redis key가 있는 경우")
  void redisKeyExist(){
    BookmarkRequest request= new BookmarkRequest(1L,1L);
    bookmarkController.updateBookmark(request);
  }

}