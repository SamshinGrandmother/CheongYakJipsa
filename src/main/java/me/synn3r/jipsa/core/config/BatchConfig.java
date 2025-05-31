package me.synn3r.jipsa.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
  private final JobLauncher jobLauncher;
  private final JobExplorer jobExplorer;
  private final JobRepository jobRepository;
  @Value("${spring.batch.job.names}")
  private String jobName;

  @Bean
  @Profile("batch")
  public JobLauncherApplicationRunner jobLauncherApplicationRunner() {

    JobLauncherApplicationRunner jobLauncherApplicationRunner = new JobLauncherApplicationRunner(
      jobLauncher, jobExplorer, jobRepository);
    jobLauncherApplicationRunner.setJobName(jobName);
    return jobLauncherApplicationRunner;
  }

}
