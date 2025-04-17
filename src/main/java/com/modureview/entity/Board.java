package com.modureview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Table(name = "board")
@Getter
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "category_id", nullable = false)
  private Long categoryId;

  @Column(name = "user_id")
  private Long userId;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "view_count")
  private Long viewCount;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  private Integer stars;

}
