package me.genuss.jklee;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingRequest.Format;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.Environment;

class JkleeFormFieldsManager {

  private static final Pattern SESSION_PATTERN = Pattern.compile("^(.*)_(\\d+)$");

  private volatile FormFields fields;

  JkleeFormFieldsManager(FormFields defaults) {
    this.fields = defaults;
  }

  FormFields lastFormFields() {
    return fields;
  }

  void recordSubmission(
      String sessionName,
      @Nullable String rawArguments,
      @Nullable Duration duration,
      @Nullable Format format) {
    if (rawArguments != null && duration != null && format != null) {
      fields = new FormFields(nextSessionName(sessionName), rawArguments, duration, format);
    }
  }

  static JkleeFormFieldsManager withEnv(
      JkleeConfigurationProperties properties, Environment environment) {
    JkleeConfigurationProperties.SpringBootAdmin sba = properties.getSpringBootAdmin();
    String sessionPrefix = sba.getSessionPrefix();
    if (sessionPrefix == null) {
      sessionPrefix = environment.getProperty("spring.application.name", "");
    }
    FormFields defaults =
        new FormFields(
            sessionPrefix + "_001", sba.getRawArguments(), sba.getDuration(), sba.getFormat());
    return new JkleeFormFieldsManager(defaults);
  }

  private static String nextSessionName(String sessionName) {
    Matcher matcher = SESSION_PATTERN.matcher(sessionName);
    if (!matcher.matches()) {
      return sessionName;
    }
    long next = Long.parseLong(matcher.group(2)) + 1;
    return matcher.group(1) + "_" + String.format("%03d", next);
  }

  @Value
  static class FormFields {
    String sessionName;
    String rawArguments;
    Duration duration;
    Format format;
  }
}
