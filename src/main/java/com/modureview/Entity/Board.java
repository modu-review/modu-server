package com.modureview.Entity;

import com.modureview.Entity.Status.BoardStatus;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "GalleryBoard")
public class Board extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "gallery_board_id")
  private Long Id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;


  private int view_count;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_entity_id",nullable = false)
  public User user;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  public BoardStatus status;

  @Column(name = "DELETE_TIME")
  private LocalDateTime deleteAt;

  @Builder.Default
  @OneToMany(mappedBy = "galleryBoard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  List<File> fileEntities = new ArrayList<>();
/*
  //TODO : COMMENT 설정 이후
  @Builder.Default
  @OneToMany(mappedBy = "galleryBoard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  List<Comment> commentEntities = new ArrayList<>();

*/





  // ==== 조회 수 증가 ==== //
  public void upViewCount(){this.view_count++;}



}
