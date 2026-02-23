package me.genuss.jklee;

import me.genuss.jklee.JkleeSettings.AsyncProfiler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JkleeConfigurationProperties.class)
@ConditionalOnProperty(value = "jklee.enabled", matchIfMissing = true)
public class JkleeAutoConfiguration {

  @Bean
  public Jklee jklee(JkleeConfigurationProperties properties) {
    JkleeConfigurationProperties.AsyncProfiler asyncProfiler = properties.getAsyncProfiler();
    return new Jklee(
        JkleeSettings.builder()
            .enabled(properties.isEnabled())
            .failOnInitErrors(properties.isFailOnInitErrors())
            .asyncProfiler(
                AsyncProfiler.builder()
                    .agentPathCandidates(asyncProfiler.getAgentPathCandidates())
                    .resultsDir(asyncProfiler.getResultsDir())
                    .appendPidToDirs(asyncProfiler.isAppendPidToDirs())
                    .build())
            .build());
  }

  @Bean
  public JkleeSettingsEndpoint jkleeSettingsEndpoint(Jklee jklee) {
    return new JkleeSettingsEndpoint(jklee);
  }

  @Bean
  public JkleeFilesEndpoint jkleeFilesEndpoint(Jklee jklee) {
    return new JkleeFilesEndpoint(jklee);
  }

  @Bean
  public JkleeProfileEndpoint jkleeProfileEndpoint(Jklee jklee) {
    return new JkleeProfileEndpoint(jklee);
  }
}
