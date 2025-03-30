package com.modureview.entity.utill;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
  @CreatedDate
  @Column(name = "CREATED_AT",updatable  = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy/MM/dd HH:mm:ss")
  // 이 어노테이션은 JSON 직렬화 및 역직렬화 시, LocalDateTime을 "yyyy/MM/dd HH:mm:ss" 형식의 문자열로 변환합니다.
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "MODIFIED_AT")
  @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy/MM/dd HH:mm:ss")
  // 이 어노테이션은 JSON 직렬화 및 역직렬화 시, LocalDateTime을 "yyyy/MM/dd HH:mm:ss" 형식의 문자열로 변환합니다.
  private LocalDateTime modifiedAt;

  //포멧을 변경하고 싶다면 직접 getter를 추가 가능
  public String getFormattedCreatedAt(){
    return createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
  }

  public String getFormattedModifiedAt() {
    return modifiedAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
  }


}
