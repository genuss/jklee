package me.genuss.jklee;

import lombok.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id = "jklee-settings")
public class JkleeSettingsEndpoint {

  private final Jklee jklee;

  public JkleeSettingsEndpoint(Jklee jklee) {
    this.jklee = jklee;
  }

  @ReadOperation
  public ConfigResponse getConfig() {
    return new ConfigResponse(jklee.getSettings());
  }

  @Value
  public static class ConfigResponse {
    JkleeSettings settings;
  }
}
