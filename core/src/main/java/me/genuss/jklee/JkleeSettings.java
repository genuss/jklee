package me.genuss.jklee;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Builder
@Value
@Accessors(fluent = true)
public class JkleeSettings {

  @Builder.Default boolean enabled = true;

  @Builder.Default boolean failOnInitErrors = false;

  boolean cleanResultsDirOnStart;

  @Builder.Default @NonNull AsyncProfiler asyncProfiler = AsyncProfiler.builder().build();

  @Builder
  @Value
  @Accessors(fluent = true)
  public static class AsyncProfiler {

    @Builder.Default @NonNull List<@NonNull String> agentPathCandidates = Collections.emptyList();

    Path resultsDir;

    @Builder.Default boolean appendPidToDirs = true;
  }
}
