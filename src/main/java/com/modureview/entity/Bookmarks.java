package com.modureview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookmarks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Bookmarks {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "board_id", nullable = false)
  private Long boardId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @PrePersist
  public void prePersist() {
    this.createdDate = LocalDateTime.now();
  }

  public Bookmarks(Long userId, Long boardId) {
    this.userId = userId;
    this.boardId = boardId;
  }
}
