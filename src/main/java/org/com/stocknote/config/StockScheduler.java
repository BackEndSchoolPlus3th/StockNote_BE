package org.com.stocknote.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Configuration
@EnableScheduling
@Component
@Slf4j
public class StockScheduler {

  @Value("classpath:scripts/stock_download.py")
  private Resource pythonScript;

  @Bean
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(2);
    return scheduler;
  }

   @PostConstruct /*대신 Scheduled 사용*/
//  @Scheduled(cron = "0 30 8 * * *", zone = "Asia/Seoul") // 매일 오전 8:30 KST
  public void downloadStockData() {
    log.info("Starting scheduled stock data download at {}", LocalDateTime.now());
    try {
      log.info("Python script exists: {}", pythonScript.exists());
      log.info("Python script path: {}", pythonScript.getURL());

      File tempScript = File.createTempFile("stock_download", ".py");
      Files.copy(pythonScript.getInputStream(), tempScript.toPath(),
          StandardCopyOption.REPLACE_EXISTING);

      ProcessBuilder processBuilder = new ProcessBuilder("python", tempScript.getAbsolutePath());
      processBuilder.redirectErrorStream(true); // 에러 스트림을 표준 출력으로 리다이렉트
      Process process = processBuilder.start();

      // 실시간으로 로그 출력
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          log.info("Python script output: {}", line);
        }
      }

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        // 에러 스트림 확인
        try (BufferedReader errorReader =
            new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
          String line;
          while ((line = errorReader.readLine()) != null) {
            log.error("Python script error: {}", line);
          }
        }
        log.error("Python script failed with exit code: {}", exitCode);
      } else {
        log.info("Python script executed successfully");
      }

      tempScript.deleteOnExit();

    } catch (Exception e) {
      log.error("Scheduled stock download failed", e);
      e.printStackTrace(); // 스택 트레이스 출력
    }
  }
}
