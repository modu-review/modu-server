package com.modureview.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.modureview.dto.request.BookmarkRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class BookmarkControllerTest {

  @Autowired
  private BookmarkController bookmarkController;

  @Test
  @DisplayName("북마크 제거")
  void deleteBookmark(){
    BookmarkRequest request= new BookmarkRequest("user1@example.com");
    bookmarkController.deleteBookmark(1L,request);
  }

  @Test
  @DisplayName("북마크추가")
  void addBookmark(){
    BookmarkRequest request= new BookmarkRequest("user1@example.com");
    bookmarkController.updateBookmark(999L, request);

  }



}