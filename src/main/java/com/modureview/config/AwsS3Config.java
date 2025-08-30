package com.modureview.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsS3Config {

  private String bucket;
  private String region;
  private Credentials credentials = new Credentials();

  @Getter
  @Setter
  public static class Credentials {
    private String accessKey;
    private String secretKey;
  }

  @Bean
  public S3Client s3Client() {
    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
        getCredentials().getAccessKey(),
        getCredentials().getSecretKey()
    );

    return S3Client.builder()
        .region(Region.of(getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }

}