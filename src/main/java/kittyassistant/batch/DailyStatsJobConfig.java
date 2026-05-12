package kittyassistant.batch;

import kittyassistant.service.AnalyticsService;
import kittyassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class DailyStatsJobConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    @Bean
    public Job dailyStatsJob() {
        return new JobBuilder("dailyStatsJob", jobRepository)
                .start(dailyStatsStep())
                .build();
    }

    @Bean
    public Step dailyStatsStep() {
        return new StepBuilder("dailyStatsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Запуск ночной агрегации статистики...");

                    // Агрегируем для всех пользователей
                    userRepository.findAll().forEach(user ->
                            analyticsService.aggregateToday(user.getEmail())
                    );

                    log.info("Агрегация завершена.");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void runNightly() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(dailyStatsJob(), params);
        } catch (Exception e) {
            log.error("Ошибка при запуске dailyStatsJob: {}", e.getMessage());
        }
    }
}