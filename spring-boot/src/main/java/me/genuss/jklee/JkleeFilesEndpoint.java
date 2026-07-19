package me.genuss.jklee;

import java.nio.file.Path;
import java.util.List;
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
  private final FormFieldsManager formFieldsManager;

  public JkleeFilesEndpoint(Jklee jklee, FormFieldsManager formFieldsManager) {
    this.jklee = jklee;
    this.formFieldsManager = formFieldsManager;
  }

  @ReadOperation
  public ProfilingResultFiles getResults() {
    List<ProfilingResult> results = jklee.getAvailableProfilingResults();
    return new ProfilingResultFiles(results, formFieldsManager.buildFormFields(results));
  }

  @ReadOperation
  public WebEndpointResponse<Resource> download(@Selector String sessionName) {
    Path result = jklee.getProfilingResult(sessionName);
    if (result == null) {
      return new WebEndpointResponse<>(WebEndpointResponse.STATUS_NOT_FOUND);
    }

    return new WebEndpointResponse<>(new FileSystemResource(result));
  }

  @Value
  public static class ProfilingResultFiles {
    List<ProfilingResult> results;
    FormFields formFields;
  }
}
