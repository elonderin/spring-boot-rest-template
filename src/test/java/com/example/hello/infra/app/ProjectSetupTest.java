package com.example.hello.infra.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ProjectSetupTest {

  @Test
  void junitRunnerJvmArgs() throws Exception {
    var inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
    assertThat(inputArgs)
        .contains("-Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener")
        .contains("-Xshare:off")
        .contains("-XX:+EnableDynamicAgentLoading")
    ;

  }
}
