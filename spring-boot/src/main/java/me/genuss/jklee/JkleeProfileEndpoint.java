package me.genuss.jklee;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingRequest.Format;
import me.genuss.jklee.Jklee.ProfilingResponse;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

@Endpoint(id = "jkleeProfile")
public class JkleeProfileEndpoint {

  private final Jklee jklee;

  public JkleeProfileEndpoint(Jklee jklee) {
    this.jklee = jklee;
  }

  @ReadOperation
  public ProfilingOptions profilingOptions() {
    var formats =
        Arrays.stream(Format.values())
            .map(format -> Map.of("label", format.name().toLowerCase(), "value", format.name()));
    return ProfilingOptions.builder().formats(formats.toList()).build();
  }

  @WriteOperation
  public ProfilingResponse start(
      @Selector String id, String rawArguments, Duration duration, Format format) {
    // rawArguments example start,event=itimer,interval=1ms
    return jklee.start(
        ProfilingRequest.builder()
            .id(id)
            .rawArguments(rawArguments)
            .duration(duration)
            .format(format)
            .build());
  }

  @Builder
  @Value
  public static class ProfilingOptions {
    List<Map<String, String>> formats;
  }
}
