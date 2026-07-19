package me.genuss.jklee;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingRequest.Format;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "jklee")
@Value
public class JkleeConfigurationProperties {

  boolean enabled;
  boolean failOnInitErrors;
  AsyncProfiler asyncProfiler;
  SpringBootAdmin springBootAdmin;

  @ConstructorBinding
  public JkleeConfigurationProperties(
      @DefaultValue("true") boolean enabled,
      boolean failOnInitErrors,
      @DefaultValue AsyncProfiler asyncProfiler,
      @DefaultValue SpringBootAdmin springBootAdmin) {
    this.enabled = enabled;
    this.failOnInitErrors = failOnInitErrors;
    this.asyncProfiler = asyncProfiler;
    this.springBootAdmin = springBootAdmin;
  }

  @Value
  public static class SpringBootAdmin {

    @Nullable String sessionPrefix;
    String rawArguments;
    Duration duration;
    Format format;

    @ConstructorBinding
    public SpringBootAdmin(
        @Nullable String sessionPrefix,
        @DefaultValue("start,event=itimer,interval=1ms") String rawArguments,
        @DefaultValue("1s") Duration duration,
        @DefaultValue("FLAMEGRAPH") Format format) {
      this.sessionPrefix = sessionPrefix;
      this.rawArguments = rawArguments;
      this.duration = duration;
      this.format = format;
    }
  }

  @Value
  public static class AsyncProfiler {

    List<String> agentPathCandidates;
    @Nullable Path resultsDir;
    boolean appendPidToDirs;

    @ConstructorBinding
    public AsyncProfiler(
        @DefaultValue List<String> agentPathCandidates,
        @Nullable Path resultsDir,
        @DefaultValue("true") boolean appendPidToDirs) {
      this.agentPathCandidates = agentPathCandidates;
      this.resultsDir = resultsDir;
      this.appendPidToDirs = appendPidToDirs;
    }
  }
}
