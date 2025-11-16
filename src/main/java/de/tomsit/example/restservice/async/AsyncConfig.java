package de.tomsit.example.restservice.async;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public Executor taskExecutorA() {
    return Executors.newFixedThreadPool(4,
                                        new NamingThreadFactory("exec-a"));
  }

  @Bean
  public Executor taskExecutorB() {
    return Executors.newFixedThreadPool(4,
                                        new NamingThreadFactory("exec-b"));
  }

  @RequiredArgsConstructor
  static class NamingThreadFactory implements ThreadFactory {

    private final AtomicInteger counter = new AtomicInteger(1);
    private final String prefix;

    @Override
    public Thread newThread(Runnable r) {
      return new Thread(r, "%s-%d".formatted(prefix, counter.getAndIncrement()));
    }
  }
}
