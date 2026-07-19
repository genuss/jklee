package me.genuss.jklee;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingResult;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Endpoint(id = "jkleeFiles")
public class JkleeFilesEndpoint {

  private final Jklee jklee;
  private final String sessionPrefix;

  public JkleeFilesEndpoint(Jklee jklee, String sessionPrefix) {
    this.jklee = jklee;
    this.sessionPrefix = sessionPrefix == null ? "" : sessionPrefix;
  }

  @ReadOperation
  public ProfilingResultFiles getResults() {
    List<ProfilingResult> results = jklee.getAvailableProfilingResults();
    FormFields formFields =
        new FormFields(sessionPrefix, computeNextSessionName(sessionPrefix, results));
    return new ProfilingResultFiles(results, formFields);
  }

  @ReadOperation
  public WebEndpointResponse<Resource> download(@Selector String sessionName) {
    Path result = jklee.getProfilingResult(sessionName);
    if (result == null) {
      return new WebEndpointResponse<>(WebEndpointResponse.STATUS_NOT_FOUND);
    }

    return new WebEndpointResponse<>(new FileSystemResource(result));
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
  public static class ProfilingResultFiles {
    List<ProfilingResult> results;
    FormFields formFields;
  }

  @Value
  public static class FormFields {
    String sessionPrefix;
    String nextSessionName;
  }
}
