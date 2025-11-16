package de.tomsit.example.restservice.async;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {
        AsyncConfig.class,
        DownloaderService.class,
        DownloadCoordinator.class
    })
class DownloadCoordinatorTest {

  @Autowired
  DownloadCoordinator downloadCoordinator;

  @Test
  void run() throws Exception {
    downloadCoordinator.runParallelDownloads();
  }
}
