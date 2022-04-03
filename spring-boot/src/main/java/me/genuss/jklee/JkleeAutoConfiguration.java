package me.genuss.jklee;

import java.util.List;
import me.genuss.jklee.JkleeSettings.AsyncProfilerOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JkleeAutoConfiguration {

  @Bean
  public Jklee jklee() {
    return new Jklee(
        JkleeSettings.builder()
            .asyncProfilerOptions(
                AsyncProfilerOptions.builder()
                    .agentPathCandidates(
                        List.of(
                            "/Users/agenus/soft/async-profiler-2.8.3-macos/build/libasyncProfiler.so"))
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
