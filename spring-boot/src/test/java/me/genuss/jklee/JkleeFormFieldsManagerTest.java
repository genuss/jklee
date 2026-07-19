package me.genuss.jklee;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import me.genuss.jklee.Jklee.ProfilingResult;
import me.genuss.jklee.JkleeFormFieldsManager.FormFields;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JkleeFormFieldsManagerTest {

  @ParameterizedTest
  @MethodSource("nextSessionNameCases")
  void computesNextSessionName(
      String sessionPrefix, List<ProfilingResult> results, FormFields expected) {
    assertThat(new JkleeFormFieldsManager(sessionPrefix).buildFormFields(results))
        .isEqualTo(expected);
  }

  static Stream<Arguments> nextSessionNameCases() {
    return Stream.of(
        Arguments.of("jklee-sample", Collections.emptyList(), new FormFields("jklee-sample_001")),
        Arguments.of(
            "jklee-sample",
            Arrays.asList(
                result("jklee-sample_001", 1),
                result("jklee-sample_007", 3),
                result("jklee-sample_003", 2)),
            new FormFields("jklee-sample_008")),
        Arguments.of(
            "jklee-sample",
            Arrays.asList(
                result("test1", 1),
                result("jklee-sample", 2),
                result("jklee-sample_foo", 3),
                result("jklee-sample_002", 4)),
            new FormFields("jklee-sample_003")),
        Arguments.of(
            "app", Collections.singletonList(result("app_999", 1)), new FormFields("app_1000")),
        Arguments.of(
            "app",
            Arrays.asList(result("app_005", 1), result("loadtest_002", 2)),
            new FormFields("loadtest_003")),
        Arguments.of("", Collections.emptyList(), new FormFields("_001")));
  }

  private static ProfilingResult result(String name, long endedAtEpochSecond) {
    return ProfilingResult.builder()
        .name(name)
        .endedAt(Instant.ofEpochSecond(endedAtEpochSecond))
        .build();
  }
}
