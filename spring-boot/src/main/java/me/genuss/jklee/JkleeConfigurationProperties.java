package me.genuss.jklee;

import java.nio.file.Path;
import java.util.List;
import lombok.Value;
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

    String sessionPrefix;

    @ConstructorBinding
    public SpringBootAdmin(@DefaultValue("${spring.application.name:}") String sessionPrefix) {
      this.sessionPrefix = sessionPrefix;
    }
  }

  @Value
  public static class AsyncProfiler {

    List<String> agentPathCandidates;
    Path resultsDir;
    boolean appendPidToDirs;

    @ConstructorBinding
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
