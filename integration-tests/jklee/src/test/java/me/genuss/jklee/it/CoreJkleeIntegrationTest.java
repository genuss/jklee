package me.genuss.jklee.it;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;
import me.genuss.jklee.Jklee;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingResponse;
import me.genuss.jklee.Jklee.ResultCode;
import me.genuss.jklee.JkleeSettings;
import me.genuss.jklee.JkleeSettings.AsyncProfiler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CoreJkleeIntegrationTest {

  @Test
  void loadAndProfile() throws Exception {
    String asyncProfilerBasePath = System.getProperty("jklee.libs.dir");
    List<String> agentPathCandidates =
        Stream.of(
                "/async-profiler/macos/libasyncProfiler.dylib",
                "/async-profiler/linux-x64/libasyncProfiler.so",
                "/async-profiler/linux-arm64/libasyncProfiler.so")
            .map(asyncProfilerBasePath::concat)
            .toList();
    var jklee =
        new Jklee(
            JkleeSettings.builder()
                .failOnInitErrors(true)
                .asyncProfiler(
                    AsyncProfiler.builder().agentPathCandidates(agentPathCandidates).build())
                .build());
    ProfilingResponse response =
        jklee.start(
            ProfilingRequest.builder()
                .rawArguments("start,event=itimer,interval=1ms")
                .duration(Duration.ofSeconds(1))
                .id("idempotent-id")
                .build());

    Assertions.assertThat(response.result()).as("response.result()").isEqualTo(ResultCode.STARTED);

    Thread.sleep(1500);
    Assertions.assertThat(jklee.getAvailableProfilingResults())
        .as("jklee.getAvailableProfilingResults()")
        .isNotEmpty();
  }
}
