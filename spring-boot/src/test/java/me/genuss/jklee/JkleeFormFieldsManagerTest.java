package me.genuss.jklee;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import me.genuss.jklee.Jklee.ProfilingRequest.Format;
import me.genuss.jklee.JkleeFormFieldsManager.FormFields;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class JkleeFormFieldsManagerTest {

  private static final String DEFAULT_RAW_ARGUMENTS = "start,event=itimer,interval=1ms";
  private static final Duration DEFAULT_DURATION = Duration.ofSeconds(2);

  @Test
  void seedsInitialSessionNameFromPrefix() {
    assertThat(manager("jklee-sample_001").buildFormFields())
        .isEqualTo(
            new FormFields(
                "jklee-sample_001", DEFAULT_RAW_ARGUMENTS, DEFAULT_DURATION, Format.FLAMEGRAPH));
  }

  @ParameterizedTest
  @CsvSource({
    "jklee-sample_001, jklee-sample_002",
    "jklee-sample_007, jklee-sample_008",
    "app_999, app_1000",
    "loadtest, loadtest",
    "app_foo, app_foo",
  })
  void computesNextSessionNameFromSubmission(String submitted, String expected) {
    JkleeFormFieldsManager manager = manager("app_001");

    manager.recordSubmission(
        submitted, DEFAULT_RAW_ARGUMENTS, Duration.ofSeconds(2), Format.FLAMEGRAPH);

    assertThat(manager.buildFormFields().getSessionName()).isEqualTo(expected);
  }

  @Test
  void preservesLastSubmittedFields() {
    JkleeFormFieldsManager manager = manager("app_001");

    manager.recordSubmission("app_005", "start,event=cpu", Duration.ofMinutes(5), Format.TREE);

    assertThat(manager.buildFormFields())
        .isEqualTo(
            new FormFields("app_006", "start,event=cpu", Duration.ofMinutes(5), Format.TREE));
  }

  private static JkleeFormFieldsManager manager(String sessionName) {
    return new JkleeFormFieldsManager(
        new FormFields(sessionName, DEFAULT_RAW_ARGUMENTS, DEFAULT_DURATION, Format.FLAMEGRAPH));
  }
}
