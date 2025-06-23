package com.modureview.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationScheduler {

  private final JobLauncher jobLauncher;
  private final Job reviewAggregationJob;

  @Scheduled(cron = "*/10 * * * * *") // "30초 간격으로 계속 실행"
  public void runAggregationJob() {
    try {
      log.info("리뷰 집계 스케줄러를 시작합니다.");
      JobParameters jobParameters = new JobParametersBuilder()
          .addString("timestamp", String.valueOf(System.currentTimeMillis()))
          .toJobParameters();

      jobLauncher.run(reviewAggregationJob, jobParameters);

    } catch (Exception e) {
      log.error("리뷰 집계 스케줄러 실행 중 에러가 발생했습니다.", e);
    }
  }
}