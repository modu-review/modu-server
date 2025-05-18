package com.modureview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String author;

  @Enumerated(EnumType.STRING)
  private Category category;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String content;

  private Integer commentsCount;

  private Integer bookmarksCount;

  @Column( name = "created_at")
  private LocalDateTime createdAt;

  @Column( name = "modified_at")
  private LocalDateTime modifiedAt;



  @PrePersist
  protected void onCreate(){
    this.createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate(){
    this.modifiedAt = LocalDateTime.now();
  }

  @Builder
  public Board(String title, String author, Category category, String content, Integer commentsCount, Integer bookmarksCount) {
    this.title = title;
    this.author = author;
    this.category = category;
    this.content = content;
    this.commentsCount = commentsCount;
    this.bookmarksCount = bookmarksCount;
  }

}
