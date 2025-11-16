package de.tomsit.example.restservice.async;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadCoordinator {

  private final DownloaderService downloader;

  public void runParallelDownloads() {
    long start = System.currentTimeMillis();

    CompletableFuture<String> a = downloader.download("https://a.example");
    CompletableFuture<String> b = downloader.download("https://b.example");
    CompletableFuture<String> c = downloader.download("https://c.example");

    CompletableFuture.allOf(a, b, c).join();

    System.out.println("A length = " + a.join().length());
    System.out.println("B length = " + b.join().length());
    System.out.println("C length = " + c.join().length());

    System.out.println("Total time = " + (System.currentTimeMillis() - start) + " ms");
  }
}
