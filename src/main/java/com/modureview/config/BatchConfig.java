package com.modureview.config;

import com.modureview.service.BestReviewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

  private final BestReviewsService bestReviewsService;

  @Bean
  public Job reviewAggregationJob(JobRepository jobRepository, Step aggregateStep) {
    return new JobBuilder("reviewAggregationJob", jobRepository)
        .start(aggregateStep)
        .build();
  }

  @Bean
  public Step aggregateStep(JobRepository jobRepository, Tasklet aggregateTasklet, PlatformTransactionManager transactionManager) {
    return new StepBuilder("aggregateStep", jobRepository)
        .tasklet(aggregateTasklet, transactionManager)
        .build();
  }

  @Bean
  public Tasklet aggregateTasklet() {
    return (contribution, chunkContext) -> {
      log.info("리뷰 집계 Batch Tasklet을 시작합니다.");
      bestReviewsService.aggregate();
      log.info("리뷰 집계 Batch Tasklet을 완료했습니다.");
      return RepeatStatus.FINISHED;
    };
  }
}