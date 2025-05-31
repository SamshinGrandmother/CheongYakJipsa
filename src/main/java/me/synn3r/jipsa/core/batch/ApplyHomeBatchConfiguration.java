package me.synn3r.jipsa.core.batch;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.batch.domain.HouseScheduleResponse;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ApplyHomeBatchConfiguration {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final ItemReader<HouseScheduleResponse> homeReader;
  private final ItemWriter<HouseScheduleResponse> homeWriter;

  @Bean(name = "applyHomeBatchJob")
  public Job applyHomeBatchJob() {
    return new JobBuilder("applyHomeBatchJob", jobRepository)
      .incrementer(new RunIdIncrementer())
      .start(applyHomeBatchStep())
      .preventRestart()
      .build();
  }

  @Bean
  @JobScope
  public Step applyHomeBatchStep() {
    return new StepBuilder("applyHomeBatchStep", jobRepository)
      .<HouseScheduleResponse, HouseScheduleResponse>chunk(1000, transactionManager)
      .reader(homeReader)
      .writer(homeWriter)
      .build();

  }

}
