package de.tomsit.example.restservice.infra.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class ProjectSetupTest {

  @Test
  void testJunitRunnerJvmArgs() throws Exception {
    var inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
    assertThat(inputArgs)
        .contains("-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener")
        .contains("-Xshare:off")
        .contains("-XX:+EnableDynamicAgentLoading")
    ;

  }

  @Nested
  class SpringApplicationYaml {

    @Autowired
    Environment env;

    @Test
    void testDefaultProfilers() {
      assertThat(env.getDefaultProfiles())
          .contains("default")
          .contains("common")
      ;
    }

    @Test
    void testProjectLogLevel_ShouldBeDefined(@Autowired StandardEnvironment env) {

      var logLevels = env.getPropertySources().stream()
                         .filter(ps -> ps instanceof EnumerablePropertySource)
                         .map(ps -> (EnumerablePropertySource<?>) ps)
                         .flatMap(ps -> Arrays.stream(ps.getPropertyNames()))
                         .filter(key -> key.startsWith("logging.level."))
                         .map(k -> k.replace("logging.level.", ""))
                         .toList();

      var packagePrefix = Arrays.stream(this.getClass().getPackageName().split("\\."))
                                .limit(2)
                                .collect(Collectors.joining("."));

      assertThat(logLevels)
          .as(  "there should be a logging.level defined for: " + packagePrefix )
          .anyMatch(s -> s.startsWith(packagePrefix));
    }

  }


}
