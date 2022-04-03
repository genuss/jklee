package me.genuss.jklee;

import java.util.List;
import lombok.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Endpoint(id = "jklee-files")
public class JkleeFilesEndpoint {

  private final Jklee jklee;

  public JkleeFilesEndpoint(Jklee jklee) {
    this.jklee = jklee;
  }

  @ReadOperation
  public ProfilingResultFiles getAvailableProfilingResults() {
    return new ProfilingResultFiles(jklee.getAvailableProfilingResults());
  }

  @ReadOperation
  public WebEndpointResponse<Resource> downloadFile(@Selector String fileName) {
    var result = jklee.getProfilingResult(fileName);
    if (result == null) {
      return new WebEndpointResponse<>(WebEndpointResponse.STATUS_NOT_FOUND);
    }

    return new WebEndpointResponse<>(new FileSystemResource(result));
  }

  @Value
  public static class ProfilingResultFiles {
    List<String> files;
  }
}
