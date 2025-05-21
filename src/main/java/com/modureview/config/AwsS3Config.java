package com.modureview.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "cloud.aws.s3")
public class AwsS3Config {

  private String bucket;
  private String region;
  private Credentials credentials;

  @Getter
  public static class Credentials {
    private String accessKey;
    private String secretKey;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }
}