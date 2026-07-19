package me.genuss.jklee;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Arrays;
import me.genuss.jklee.Jklee.ProfilingRequest.Format;
import me.genuss.jklee.JkleeFormFieldsManager.FormFields;
import org.junit.jupiter.api.Test;

class JkleeEndpointsTest {

  @Test
  void filesEndpointReturnsOnlyResultFiles() {
    JkleeFilesEndpoint endpoint = new JkleeFilesEndpoint(jklee());

    JkleeFilesEndpoint.ProfilingResultFiles response = endpoint.getResults();

    assertThat(response.getResults()).isEmpty();
    assertThat(JkleeFilesEndpoint.ProfilingResultFiles.class.getDeclaredFields())
        .extracting(Field::getName)
        .containsOnly("results");
  }

  @Test
  void profileEndpointReturnsFormatsAndFormFields() {
    FormFields formFields =
        new FormFields(
            "jklee-sample_001",
            "start,event=itimer,interval=1ms",
            Duration.ofSeconds(2),
            Format.FLAMEGRAPH);
    JkleeProfileEndpoint endpoint =
        new JkleeProfileEndpoint(jklee(), new JkleeFormFieldsManager(formFields));

    JkleeProfileEndpoint.ProfilingOptions options = endpoint.profilingOptions();

    assertThat(options.getFormFields()).isEqualTo(formFields);
    assertThat(options.getFormats())
        .extracting(format -> format.get("value"))
        .containsExactly(Arrays.stream(Format.values()).map(Format::name).toArray(String[]::new));
  }

  private static Jklee jklee() {
    return new Jklee(JkleeSettings.builder().enabled(false).build());
  }
}
