package de.tomsit.example.restservice;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(WebTestClientBuilderCustomizer.class)
@Configuration
class WebTestClientConfig {

  @Value("${app.test.webtestclient.timeout.seconds:500}")
  private int seconds;

  @Bean
  WebTestClientBuilderCustomizer customizer() {
    //useful for debugging when running tests
    return builder -> builder
        .responseTimeout(Duration.ofSeconds(seconds));
  }
}
