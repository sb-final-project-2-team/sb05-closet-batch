package com.codeit.closet.module.batch.config;

import com.codeit.closet.common.entity.WeatherData;
import com.codeit.closet.common.entity.WeatherRegion;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class WeatherUpdateBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final EntityManagerFactory entityManagerFactory;

  public WeatherUpdateBatchConfig(JobRepository jobRepository,
      @Qualifier("businessTransactionManager") PlatformTransactionManager transactionManager,
      @Qualifier("businessManagerFactory") EntityManagerFactory entityManagerFactory) {
    this.jobRepository = jobRepository;
    this.transactionManager = transactionManager;
    this.entityManagerFactory = entityManagerFactory;
  }

  @Bean
  public JpaPagingItemReader<WeatherRegion> weatherRegionReader() {
    JpaPagingItemReader<WeatherRegion> reader = new JpaPagingItemReader<>();
    reader.setEntityManagerFactory(entityManagerFactory);
    reader.setQueryString("select wr from WeatherRegion wr order by wr.createdAt");
    reader.setPageSize(50);
    return reader;
  }

  @Bean
  public Step weatherUpdateStep(
      ItemReader<WeatherRegion> reader,
      ItemProcessor<WeatherRegion, List<WeatherData>> processor,
      ItemWriter<List<WeatherData>> writer) {

    return new StepBuilder("weatherUpdateStep", jobRepository)
        .<WeatherRegion, List<WeatherData>>chunk(10, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)

        // skip listener
        .listener(weatherSkipListener())

        // retry
        .faultTolerant()
        .retry(IllegalArgumentException.class)
        .retry(NumberFormatException.class)
        .retryLimit(3)

        // skip
        .skip(IllegalArgumentException.class)
        .skip(NumberFormatException.class)
        .skipLimit(100)
        .build();
  }

  @Bean
  public Job weatherUpdateJob(Step weatherUpdateStep) {
    return new JobBuilder("weatherUpdateJob", jobRepository)
        .start(weatherUpdateStep)
        .build();
  }

  @Bean
  public SkipListener<WeatherRegion, List<WeatherData>> weatherSkipListener() {
    return new SkipListener<>() {
      @Override
      public void onSkipInProcess(WeatherRegion item, Throwable t) {
        log.error("날씨 데이터 변환 실패 - regionId={}", item.getId(), t);
      }
    };
  }

}
