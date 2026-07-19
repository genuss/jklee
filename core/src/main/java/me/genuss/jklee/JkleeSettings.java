package me.genuss.jklee;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

@Builder
@Value
@Accessors(fluent = true)
public class JkleeSettings {

  @Builder.Default boolean enabled = true;

  @Builder.Default boolean failOnInitErrors = false;

  boolean cleanResultsDirOnStart;

  @Builder.Default AsyncProfiler asyncProfiler = AsyncProfiler.builder().build();

  @Builder
  @Value
  @Accessors(fluent = true)
  public static class AsyncProfiler {

    @Builder.Default List<String> agentPathCandidates = Collections.emptyList();

    @Nullable Path resultsDir;

    @Builder.Default boolean appendPidToDirs = true;
  }
}
