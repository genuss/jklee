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
  private final String applicationName;

  public JkleeFilesEndpoint(Jklee jklee, String applicationName) {
    this.jklee = jklee;
    this.applicationName = applicationName == null ? "" : applicationName;
  }

  @ReadOperation
  public ProfilingResultFiles getResults() {
    List<ProfilingResult> results = jklee.getAvailableProfilingResults();
    return new ProfilingResultFiles(results, computeNextSessionName(applicationName, results));
  }

  @ReadOperation
  public WebEndpointResponse<Resource> download(@Selector String sessionName) {
    Path result = jklee.getProfilingResult(sessionName);
    if (result == null) {
      return new WebEndpointResponse<>(WebEndpointResponse.STATUS_NOT_FOUND);
    }

    return new WebEndpointResponse<>(new FileSystemResource(result));
  }

  static String computeNextSessionName(String appName, List<ProfilingResult> results) {
    String name = appName == null ? "" : appName;
    Pattern pattern = Pattern.compile("^" + Pattern.quote(name) + "_(\\d+)$");
    long max = 0;
    if (results != null) {
      for (ProfilingResult result : results) {
        if (result == null || result.name() == null) {
          continue;
        }
        Matcher matcher = pattern.matcher(result.name());
        if (matcher.matches()) {
          try {
            long value = Long.parseLong(matcher.group(1));
            if (value > max) {
              max = value;
            }
          } catch (NumberFormatException ignored) {
            // suffix too large to parse; ignore it
          }
        }
      }
    }
    return name + "_" + String.format("%03d", max + 1);
  }

  @Value
  public static class ProfilingResultFiles {
    List<ProfilingResult> results;
    String nextSessionName;
  }
}
