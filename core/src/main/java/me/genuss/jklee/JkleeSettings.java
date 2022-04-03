package me.genuss.jklee;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.nio.file.Path;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Builder
@Value
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonInclude(Include.NON_EMPTY)
public class JkleeSettings {

  @Builder.Default boolean enabled = true;

  @Builder.Default boolean failOnInitErrors = false;

  @Builder.Default @NonNull
  AsyncProfilerOptions asyncProfilerOptions = AsyncProfilerOptions.builder().build();

  public static JkleeSettings defaults() {
    return JkleeSettings.builder().build();
  }

  @Builder
  @Value
  @Accessors(fluent = true)
  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  @JsonInclude(Include.NON_EMPTY)
  public static class AsyncProfilerOptions {

    @Builder.Default @NonNull List<@NonNull String> agentPathCandidates = List.of();

    Path resultsDir;

    Path logsDir;

    @Builder.Default boolean appendPidToDirs = true;
  }
}
