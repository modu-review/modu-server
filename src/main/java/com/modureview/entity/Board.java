package com.modureview.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private String authorEmail;

  @Enumerated(EnumType.STRING)
  private Category category;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String content;

  private String thumbnail;

  @Builder.Default
  private Integer commentsCount = 0;

  @Builder.Default
  private Integer bookmarksCount = 0;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "modified_at")
  private LocalDateTime modifiedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.modifiedAt = LocalDateTime.now();
  }

  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<BoardImage> images = new ArrayList<>();

  public void addImage(BoardImage image) {
    images.add(image);
    image.setBoard(this);
  }

  @Builder
  public Board(String title, String authorEmail, Category category, String content,
      Integer commentsCount, Integer bookmarksCount) {
    this.title = title;
    this.authorEmail = authorEmail;
    this.category = category;
    this.content = content;
    this.commentsCount = commentsCount;
    this.bookmarksCount = bookmarksCount;
  }

}
