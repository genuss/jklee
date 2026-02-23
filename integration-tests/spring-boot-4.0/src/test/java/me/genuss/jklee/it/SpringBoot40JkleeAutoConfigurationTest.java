package me.genuss.jklee.it;

import static org.assertj.core.api.Assertions.assertThat;

import me.genuss.jklee.Jklee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringBoot40JkleeAutoConfigurationTest {

  @Autowired private Jklee jklee;

  @Test
  void springBootVersionSanityCheck() {
    assertThat(SpringBootVersion.getVersion()).startsWith("4.0");
  }

  @Test
  void asyncProfilerIsLoaded() {
    assertThat(jklee.getSettings().loaded()).isTrue();
  }
}
