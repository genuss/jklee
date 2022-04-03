package me.genuss.jklee;

import java.time.Duration;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingRequest.Format;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

@Endpoint(id = "jklee-profile")
public class JkleeProfileEndpoint {

  private final Jklee jklee;

  public JkleeProfileEndpoint(Jklee jklee) {
    this.jklee = jklee;
  }

  @WriteOperation
  public void downloadFile(
      @Selector String id, String rawArguments, Duration duration, Format format) {
    // rawArguments example start,event=itimer,interval=1ms
    jklee.start(
        ProfilingRequest.builder()
            .id(id)
            .rawArguments(rawArguments)
            .duration(duration)
            .format(format)
            .build());
  }
}
