package com.modureview.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aws")
public class AwsS3Config {

  private String bucket;
  private String region;
  private Credentials credentials = new Credentials();  // ⬅️ null 방지 기본값

  @Getter
  @Setter               // ⬅️  내부 클래스에도 Setter 추가
  public static class Credentials {
    private String accessKey;
    private String secretKey;
  }
}