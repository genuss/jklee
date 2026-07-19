package me.genuss.jklee;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingResult;
import org.springframework.core.env.Environment;

class JkleeFormFieldsManager {

  private static final Pattern SESSION_PATTERN = Pattern.compile("^(.*)_(\\d+)$");

  private final FormFields formFields;

  JkleeFormFieldsManager(String sessionPrefix) {
    this.formFields = new FormFields(sessionPrefix + "_000");
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
    return new JkleeFormFieldsManager(sessionPrefix);
  }

  private String nextSessionName(List<ProfilingResult> results) {
    List<ProfilingResult> matching =
        results.stream()
            .filter(result -> SESSION_PATTERN.matcher(result.name()).matches())
            .collect(Collectors.toList());

    String prefix =
        matching.stream()
            .max(
                Comparator.comparing(
                    ProfilingResult::endedAt, Comparator.nullsLast(Comparator.naturalOrder())))
            .map(result -> prefixOf(result.name()))
            .orElse(prefixOf(formFields.getSessionName()));

    long max =
        matching.stream()
            .filter(result -> prefix.equals(prefixOf(result.name())))
            .mapToLong(result -> numberOf(result.name()))
            .max()
            .orElse(0L);

    return prefix + "_" + String.format("%03d", max + 1);
  }

  private static String prefixOf(String name) {
    Matcher matcher = SESSION_PATTERN.matcher(name);
    return matcher.matches() ? matcher.group(1) : "";
  }

  private static long numberOf(String name) {
    Matcher matcher = SESSION_PATTERN.matcher(name);
    if (matcher.matches()) {
      try {
        return Long.parseLong(matcher.group(2));
      } catch (NumberFormatException ignored) {
      }
    }
    return 0L;
  }

  @Value
  static class FormFields {
    String sessionName;
  }
}
