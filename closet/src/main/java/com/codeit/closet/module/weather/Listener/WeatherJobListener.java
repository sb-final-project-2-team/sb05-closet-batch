package com.codeit.closet.module.weather.Listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WeatherJobListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.info("Weather batch started: {}", jobExecution.getJobId());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info("Weather batch finished: status={}", jobExecution.getStatus());
  }
}
