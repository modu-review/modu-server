package com.modureview.controller;

import static com.modureview.entity.Category.food;

import com.modureview.dto.request.CommentSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CommentControllerTest {

  @Autowired
  CommentController commentController;

  @Test
  @DisplayName("save comment결과")
  void saveComment() {
    CommentSaveRequest commentSaveRequest = new CommentSaveRequest("user1@example.com", food,
        "네네 아이고아이고");
    commentController.addComment(1L, commentSaveRequest);
  }

}