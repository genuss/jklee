package me.genuss.jklee;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.java.Log;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingRequest.Command;
import me.genuss.jklee.JkleeSettings.AsyncProfilerOptions;

@Log
public class JkleeTest {

  public static void main(String[] args) throws Exception {
    var jklee =
        new Jklee(
            JkleeSettings.builder()
                .enabled(true)
                .failOnInitErrors(true)
                .asyncProfilerOptions(
                    AsyncProfilerOptions.builder()
                        .agentPathCandidates(
                            List.of(
                                "/Users/agenus/soft/async-profiler-2.8.3-macos/build/libasyncProfiler.so"))
                        .build())
                .build());
    var eater =
        new Thread(
            () -> {
              var integers = new HashMap<Integer, Integer>();
              while (!Thread.interrupted()) {
                integers.put(
                    ThreadLocalRandom.current().nextInt(100),
                    ThreadLocalRandom.current().nextInt(100));
              }
              log.info("integers.size() = " + integers.size());
            },
            "cpu-eater");
    eater.start();
    jklee.start(
        ProfilingRequest.builder()
            .command(Command.START)
            .event("itimer")
            .interval(Duration.ofMillis(1))
            .duration(Duration.ofSeconds(2))
            .id("idempotent-id")
            .build());

    Thread.sleep(3000);
    eater.interrupt();
  }
}
