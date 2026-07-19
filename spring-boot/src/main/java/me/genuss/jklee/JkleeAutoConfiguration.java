package me.genuss.jklee;

import me.genuss.jklee.JkleeSettings.AsyncProfiler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(JkleeConfigurationProperties.class)
@ConditionalOnProperty(value = "jklee.enabled", matchIfMissing = true)
class JkleeAutoConfiguration {

  @Bean
  Jklee jklee(JkleeConfigurationProperties properties) {
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
  JkleeSettingsEndpoint jkleeSettingsEndpoint(Jklee jklee) {
    return new JkleeSettingsEndpoint(jklee);
  }

  @Bean
  JkleeFormFieldsManager jkleeFormFieldsManager(
      JkleeConfigurationProperties properties, Environment environment) {
    return JkleeFormFieldsManager.withEnv(properties, environment);
  }

  @Bean
  JkleeFilesEndpoint jkleeFilesEndpoint(Jklee jklee, JkleeFormFieldsManager formFieldsManager) {
    return new JkleeFilesEndpoint(jklee, formFieldsManager);
  }

  @Bean
  JkleeProfileEndpoint jkleeProfileEndpoint(Jklee jklee) {
    return new JkleeProfileEndpoint(jklee);
  }
}
