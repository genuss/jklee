package me.genuss.jklee;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingResult;

@Value
public class FormFields {

  String sessionPrefix;
  String nextSessionName;

  public static FormFields from(String sessionPrefix, List<ProfilingResult> results) {
    String prefix = sessionPrefix == null ? "" : sessionPrefix;
    return new FormFields(prefix, computeNextSessionName(prefix, results));
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
}
