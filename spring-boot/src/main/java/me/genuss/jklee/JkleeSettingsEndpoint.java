package me.genuss.jklee;

import java.util.ArrayList;
import java.util.List;
import lombok.Value;
import me.genuss.jklee.JkleeSettingsEndpoint.ConfigResponse.ConfigEntry;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id = "jkleeSettings")
public class JkleeSettingsEndpoint {

  private final Jklee jklee;

  public JkleeSettingsEndpoint(Jklee jklee) {
    this.jklee = jklee;
  }

  @ReadOperation
  public ConfigResponse getConfig() {
    Jklee.EffectiveProperties settings = jklee.getSettings();
    ArrayList<ConfigEntry> settingsMap = new ArrayList<>();
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
  public static class ConfigResponse {
    List<ConfigEntry> settings;

    @Value
    public static class ConfigEntry {
      String name;
      Object value;
    }
  }
}
