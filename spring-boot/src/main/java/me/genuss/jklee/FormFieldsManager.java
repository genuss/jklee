package me.genuss.jklee;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingResult;
import org.springframework.core.env.Environment;

public class FormFieldsManager {

  private final String sessionPrefix;

  FormFieldsManager(String sessionPrefix) {
    this.sessionPrefix = sessionPrefix == null ? "" : sessionPrefix;
  }

  public static FormFieldsManager withEnv(
      JkleeConfigurationProperties properties, Environment environment) {
    String sessionPrefix = properties.getSpringBootAdmin().getSessionPrefix();
    if (sessionPrefix == null || sessionPrefix.isEmpty()) {
      sessionPrefix = environment.getProperty("spring.application.name", "");
    }
    return new FormFieldsManager(sessionPrefix);
  }

  public FormFields buildFormFields(List<ProfilingResult> results) {
    return new FormFields(sessionPrefix, computeNextSessionName(results));
  }

  String computeNextSessionName(List<ProfilingResult> results) {
    Pattern pattern = Pattern.compile("^" + Pattern.quote(sessionPrefix) + "_(\\d+)$");
    long max = 0;
    for (ProfilingResult result : results) {
      Matcher matcher = pattern.matcher(result.name());
      if (matcher.matches()) {
        try {
          long value = Long.parseLong(matcher.group(1));
          if (value > max) {
            max = value;
          }
        } catch (NumberFormatException ignored) {
        }
      }
    }
    return sessionPrefix + "_" + String.format("%03d", max + 1);
  }

  @Value
  public static class FormFields {
    String sessionPrefix;
    String nextSessionName;
  }
}
