package me.genuss.jklee;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingResult;

public class FormFieldsManager {

  private final String sessionPrefix;

  public FormFieldsManager(String sessionPrefix) {
    this.sessionPrefix = sessionPrefix == null ? "" : sessionPrefix;
  }

  public FormFields buildFormFields(List<ProfilingResult> results) {
    return new FormFields(sessionPrefix, computeNextSessionName(sessionPrefix, results));
  }

  static String computeNextSessionName(String sessionPrefix, List<ProfilingResult> results) {
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
