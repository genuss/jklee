package me.genuss.jklee.it;

import static org.assertj.core.api.Assertions.assertThat;

import me.genuss.jklee.Jklee;
import me.genuss.jklee.JkleeAutoConfiguration;
import me.genuss.jklee.JkleeSettingsEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = JkleeAutoConfiguration.class)
@ImportAutoConfiguration(JkleeAutoConfiguration.class)
class JkleeAutoConfigurationIT {

  @Autowired private ApplicationContext context;

  @Autowired private Jklee jklee;

  @Test
  void jkleeBeanExists() {
    assertThat(context.getBean(Jklee.class)).isNotNull();
  }

  @Test
  void jkleeSettingsEndpointBeanExists() {
    assertThat(context.getBean(JkleeSettingsEndpoint.class)).isNotNull();
  }

  @Test
  void asyncProfilerIsLoaded() {
    assertThat(jklee.getSettings().loaded()).isTrue();
  }
}
