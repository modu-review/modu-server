package com.modureview.utill;

import com.modureview.entity.Board;
import com.modureview.entity.Category;
import com.modureview.entity.User;

public class TestUtil {

  public User newUser(String email) {
    return User.builder()
        .email(email)
        .build();
  }

  public Board newBoard(User newUser) {
    return Board.builder()
        .title("테스트")
        .user(newUser)
        .authorEmail(newUser.getEmail())
        .category(Category.car)
        .content("<p>내용 예시</p>")
        .commentsCount(10)
        .bookmarksCount(10)
        .build();

  }

}
