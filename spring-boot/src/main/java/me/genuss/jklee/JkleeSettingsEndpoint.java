package me.genuss.jklee;

import java.util.ArrayList;
import java.util.List;
import lombok.Value;
import me.genuss.jklee.JkleeSettingsEndpoint.ConfigResponse.ConfigEntry;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id = "jkleeSettings")
class JkleeSettingsEndpoint {

  private final Jklee jklee;

  JkleeSettingsEndpoint(Jklee jklee) {
    this.jklee = jklee;
  }

  @ReadOperation
  ConfigResponse getConfig() {
    Jklee.EffectiveProperties settings = jklee.getSettings();
    List<ConfigEntry> settingsMap = new ArrayList<>();
    settingsMap.add(new ConfigEntry("enabled", settings.enabled()));
    settingsMap.add(new ConfigEntry("failOnInitErrors", settings.failOnInitErrors()));
    settingsMap.add(new ConfigEntry("cleanResultsDirOnStart", settings.cleanResultsDirOnStart()));
    settingsMap.add(new ConfigEntry("asyncProfiler.resultsDir", settings.resultsDir()));
    for (int i = 0; i < settings.agentPathCandidates().size(); i++) {
      String candidate = settings.agentPathCandidates().get(i);
      settingsMap.add(new ConfigEntry("agentPathCandidates[" + i + "]", candidate));
    }
    settingsMap.add(new ConfigEntry("loaded", settings.loaded()));

    return new ConfigResponse(settingsMap);
  }

  @Value
  static class ConfigResponse {
    List<ConfigEntry> settings;

    @Value
    static class ConfigEntry {
      String name;
      Object value;
    }
  }
}
