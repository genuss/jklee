package me.genuss.jklee;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingResult;
import org.springframework.core.env.Environment;

class JkleeFormFieldsManager {

  private final FormFields formFields;

  JkleeFormFieldsManager(String sessionPrefix) {
    this.formFields = new FormFields(sessionPrefix);
  }

  FormFields buildFormFields(List<ProfilingResult> results) {
    return new FormFields(nextSessionName(results));
  }

  static JkleeFormFieldsManager withEnv(
      JkleeConfigurationProperties properties, Environment environment) {
    String sessionPrefix = properties.getSpringBootAdmin().getSessionPrefix();
    if (sessionPrefix == null) {
      sessionPrefix = environment.getProperty("spring.application.name", "");
    }
    String nextSessionName = sessionPrefix + "_000";
    return new JkleeFormFieldsManager(nextSessionName);
  }

  String nextSessionName(List<ProfilingResult> results) {
    Pattern pattern = Pattern.compile("^(.*)_(\\d+)$");
    String lastSessionName =
        results.stream()
            .max(Comparator.comparing(ProfilingResult::endedAt))
            .map(ProfilingResult::name)
            .orElse(formFields.getSessionName());
    String lastSessionPrefix = "";
    long lastSessionId = 0L;
    Matcher matcher = pattern.matcher(lastSessionName);
    if (matcher.matches()) {
      try {
        lastSessionPrefix = matcher.group(1);
        lastSessionId = Long.parseLong(matcher.group(2));
      } catch (NumberFormatException ignored) {
      }
    }

    return lastSessionPrefix + "_" + String.format("%03d", lastSessionId + 1);
  }

  @Value
  static class FormFields {
    String sessionName;
  }
}
