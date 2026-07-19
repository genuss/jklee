package me.genuss.jklee;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Value;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingRequest.Format;
import me.genuss.jklee.Jklee.ProfilingResponse;
import me.genuss.jklee.JkleeFormFieldsManager.FormFields;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

@Endpoint(id = "jkleeProfile")
class JkleeProfileEndpoint {

  private final Jklee jklee;
  private final JkleeFormFieldsManager formFieldsManager;

  JkleeProfileEndpoint(Jklee jklee, JkleeFormFieldsManager formFieldsManager) {
    this.jklee = jklee;
    this.formFieldsManager = formFieldsManager;
  }

  @ReadOperation
  ProfilingOptions profilingOptions() {
    List<Map<String, String>> formats =
        Arrays.stream(Format.values())
            .map(
                format -> {
                  Map<String, String> map = new HashMap<>();
                  map.put("label", format.name().toLowerCase());
                  map.put("value", format.name());
                  return Collections.unmodifiableMap(map);
                })
            .collect(Collectors.toList());
    return ProfilingOptions.builder()
        .formats(formats)
        .formFields(formFieldsManager.lastFormFields())
        .build();
  }

  @WriteOperation
  ProfilingResponse start(
      @Selector String id, String rawArguments, Duration duration, Format format) {
    // rawArguments example start,event=itimer,interval=1ms
    formFieldsManager.recordSubmission(id, rawArguments, duration, format);
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
  static class ProfilingOptions {
    List<Map<String, String>> formats;
    FormFields formFields;
  }
}
