package me.genuss.jklee;

import me.genuss.jklee.JkleeSettings.AsyncProfiler;
import org.springframework.beans.factory.annotation.Value;
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
  public FormFieldsManager formFieldsManager(
      JkleeConfigurationProperties properties,
      @Value("${spring.application.name:}") String applicationName) {
    String sessionPrefix = properties.getSpringBootAdmin().getSessionPrefix();
    if (sessionPrefix == null || sessionPrefix.isEmpty()) {
      sessionPrefix = applicationName;
    }
    return new FormFieldsManager(sessionPrefix);
  }

  @Bean
  public JkleeFilesEndpoint jkleeFilesEndpoint(Jklee jklee, FormFieldsManager formFieldsManager) {
    return new JkleeFilesEndpoint(jklee, formFieldsManager);
  }

  @Bean
  public JkleeProfileEndpoint jkleeProfileEndpoint(Jklee jklee) {
    return new JkleeProfileEndpoint(jklee);
  }
}
