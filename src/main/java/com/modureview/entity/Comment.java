package com.modureview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long boardId;

  private String content;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "modified_at")
  private LocalDateTime modifiedAt;

  @PrePersist
  protected void onCreated() {
    this.createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdated() {
    this.modifiedAt = LocalDateTime.now();
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId")
  private User user;

}
