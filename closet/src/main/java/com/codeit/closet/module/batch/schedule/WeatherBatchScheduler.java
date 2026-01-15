package com.codeit.closet.module.batch.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherBatchScheduler {

  private final JobLauncher jobLauncher;
  private final Job weatherUpdateJob;

  @Scheduled(cron = "0 0 0 * * *")
  public void run() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("runTime", System.currentTimeMillis())
        .toJobParameters();

    jobLauncher.run(weatherUpdateJob, jobParameters);
  }

  @Bean
  public ApplicationRunner runWeatherJob(JobLauncher jobLauncher, Job weatherUpdateJob) {
    return args -> {
      JobParameters params = new JobParametersBuilder()
          .addLong("runTime", System.currentTimeMillis())
          .toJobParameters();

      jobLauncher.run(weatherUpdateJob, params);
    };
  }
}
