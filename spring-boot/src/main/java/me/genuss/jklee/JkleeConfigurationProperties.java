package me.genuss.jklee;

import java.nio.file.Path;
import java.util.List;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "jklee")
@Value
public class JkleeConfigurationProperties {

  boolean enabled;
  boolean failOnInitErrors;
  AsyncProfiler asyncProfiler;

  public JkleeConfigurationProperties(
      @DefaultValue("true") boolean enabled,
      boolean failOnInitErrors,
      @DefaultValue AsyncProfiler asyncProfiler) {
    this.enabled = enabled;
    this.failOnInitErrors = failOnInitErrors;
    this.asyncProfiler = asyncProfiler;
  }

  @Value
  public static class AsyncProfiler {

    List<String> agentPathCandidates;
    Path resultsDir;
    boolean appendPidToDirs;

    public AsyncProfiler(
        @DefaultValue List<String> agentPathCandidates,
        Path resultsDir,
        @DefaultValue("true") boolean appendPidToDirs) {
      this.agentPathCandidates = agentPathCandidates;
      this.resultsDir = resultsDir;
      this.appendPidToDirs = appendPidToDirs;
    }
  }
}
