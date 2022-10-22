package me.genuss.jklee;

import java.time.Duration;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingRequest.Format;
import me.genuss.jklee.Jklee.ProfilingResponse;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

@Endpoint(id = "jklee-profile")
public class JkleeProfileEndpoint {

  private final Jklee jklee;

  public JkleeProfileEndpoint(Jklee jklee) {
    this.jklee = jklee;
  }

  @ReadOperation
  public void readOperation() {
    // this operation is only needed for spring-boot-admin to recognize write-operation
    throw new UnsupportedOperationException("Only write operations are allowed");
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
}
