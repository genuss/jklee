package me.genuss.jklee;

import static me.genuss.jklee.FormFields.computeNextSessionName;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.genuss.jklee.Jklee.ProfilingResult;
import org.junit.jupiter.api.Test;

class FormFieldsTest {

  @Test
  void returnsFirstNameWhenNoResults() {
    assertThat(computeNextSessionName("jklee-sample", Collections.emptyList()))
        .isEqualTo("jklee-sample_001");
  }

  @Test
  void incrementsMaxMatchingNumber() {
    List<ProfilingResult> results =
        Arrays.asList(
            result("jklee-sample_001"), result("jklee-sample_007"), result("jklee-sample_003"));
    assertThat(computeNextSessionName("jklee-sample", results)).isEqualTo("jklee-sample_008");
  }

  @Test
  void ignoresCustomNamedReports() {
    List<ProfilingResult> results =
        Arrays.asList(
            result("test1"),
            result("jklee-sample"),
            result("jklee-sample_foo"),
            result("jklee-sample_002"));
    assertThat(computeNextSessionName("jklee-sample", results)).isEqualTo("jklee-sample_003");
  }

  @Test
  void growsBeyondThreeDigitsPast999() {
    assertThat(computeNextSessionName("app", Collections.singletonList(result("app_999"))))
        .isEqualTo("app_1000");
  }

  @Test
  void escapesRegexSpecialCharactersInAppName() {
    List<ProfilingResult> results = Arrays.asList(result("a.b_005"), result("axb_009"));
    assertThat(computeNextSessionName("a.b", results)).isEqualTo("a.b_006");
  }

  @Test
  void fallsBackToFirstNameWhenPrefixIsEmpty() {
    assertThat(computeNextSessionName("", Collections.emptyList())).isEqualTo("_001");
  }

  private static ProfilingResult result(String name) {
    return ProfilingResult.builder().name(name).build();
  }
}
