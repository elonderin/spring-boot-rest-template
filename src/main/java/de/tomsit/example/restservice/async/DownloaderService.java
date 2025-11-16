package de.tomsit.example.restservice.async;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DownloaderService {

  @Async("taskExecutorB")
  public CompletableFuture<String> download(String url) {
    // Simulate a slow download
    try {
      log.info("Starting download from {}", url);
      Thread.sleep(500);
    } catch (InterruptedException ignored) {
    }

    return CompletableFuture.completedFuture("content from " + url);
  }
}
