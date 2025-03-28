package com.modureview.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "File")
public class FileEntity {
  @Id @GeneratedValue
  @Column(name = "file_entity_id")
  private Long Id;

  @Column(name = "ORIGIN_FILE_NAME")
  private String originFileName;

  @Column(name = "FILE_TYPE")
  private String fileType;

  @Column(name = "FILE_PATH")
  private String filePath;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id")
  public Board board;

/*
  // ==== GalleryBoard와 FileEntity의 편의 메소드 ==== //
  public void setMappingBoard(Board board){
    this.board = board;
    board.getFileEntities().add(this);
  }
*/

}
