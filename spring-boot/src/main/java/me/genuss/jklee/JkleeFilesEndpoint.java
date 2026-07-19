package me.genuss.jklee;

import java.nio.file.Path;
import java.util.List;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingResult;
import me.genuss.jklee.JkleeFormFieldsManager.FormFields;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Endpoint(id = "jkleeFiles")
class JkleeFilesEndpoint {

  private final Jklee jklee;
  private final JkleeFormFieldsManager formFieldsManager;

  JkleeFilesEndpoint(Jklee jklee, JkleeFormFieldsManager formFieldsManager) {
    this.jklee = jklee;
    this.formFieldsManager = formFieldsManager;
  }

  @ReadOperation
  ProfilingResultFiles getResults() {
    List<ProfilingResult> results = jklee.getAvailableProfilingResults();
    return new ProfilingResultFiles(results, formFieldsManager.buildFormFields(results));
  }

  @ReadOperation
  WebEndpointResponse<Resource> download(@Selector String sessionName) {
    Path result = jklee.getProfilingResult(sessionName);
    if (result == null) {
      return new WebEndpointResponse<>(WebEndpointResponse.STATUS_NOT_FOUND);
    }

    return new WebEndpointResponse<>(new FileSystemResource(result));
  }

  @Value
  static class ProfilingResultFiles {
    List<ProfilingResult> results;
    FormFields formFields;
  }
}
