package me.genuss.jklee;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.java.Log;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.JkleeSettings.AsyncProfiler;

@Log
public class JkleeTest {

  public static void main(String[] args) throws Exception {
    Jklee jklee =
        new Jklee(
            JkleeSettings.builder()
                .enabled(true)
                .failOnInitErrors(true)
                .asyncProfiler(
                    AsyncProfiler.builder()
                        .agentPathCandidates(
                            Collections.singletonList(
                                "/Users/agenus/soft/async-profiler-2.8.3-macos/build/libasyncProfiler.so"))
                        .build())
                .build());
    Thread eater =
        new Thread(
            () -> {
              Map<Integer, Integer> integers = new HashMap<>();
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
        ProfilingRequest.builder().duration(Duration.ofSeconds(2)).id("idempotent-id").build());

    Thread.sleep(3000);
    eater.interrupt();
  }
}
