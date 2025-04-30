package com.modureview.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Board extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String category;

  @Column(name = "user_id")
  private String email;


  @Column(nullable = false)
  private String title;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "view_count")
  private Long viewCount;

  private Integer stars;

  public void upViewCount() {
    this.viewCount++;
  }


}
