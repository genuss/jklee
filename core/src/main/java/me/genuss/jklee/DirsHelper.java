package me.genuss.jklee;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

class DirsHelper {

  static Path prepareResultsDir(JkleeSettings settings) throws IOException {
    return getPath(settings, settings.asyncProfiler().resultsDir());
  }

  static Path path(String path, String... more) {
    return FileSystems.getDefault().getPath(path, more);
  }

  private static Path getPath(JkleeSettings settings, Path dir) throws IOException {
    boolean appendPidToDirs = settings.asyncProfiler().appendPidToDirs();
    Path logsDir = dir != null ? dir : defaultDir(appendPidToDirs);
    Files.createDirectories(logsDir);

    return logsDir;
  }

  private static Path defaultDir(boolean appendPidToDirs) {
    String tmpDir = System.getProperty("java.io.tmpdir");
    if (appendPidToDirs) {
      return path(tmpDir, "jklee", currentPid());
    } else {
      return path(tmpDir, "jklee");
    }
  }

  private static String currentPid() {
    return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
  }
}
