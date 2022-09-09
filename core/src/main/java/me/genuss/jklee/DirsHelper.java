package me.genuss.jklee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class DirsHelper {

  private static final String DEFAULT_LOGS_DIR = "logs";
  private static final String DEFAULT_RESULTS_DIR = "results";

  static Path prepareLogsDir(JkleeSettings settings) throws IOException {
    return getPath(settings, settings.asyncProfiler().logsDir(), DEFAULT_LOGS_DIR);
  }

  static Path prepareResultsDir(JkleeSettings settings) throws IOException {
    return getPath(settings, settings.asyncProfiler().resultsDir(), DEFAULT_RESULTS_DIR);
  }

  private static Path getPath(JkleeSettings settings, Path dir, String fallback)
      throws IOException {
    var asyncProfiler = settings.asyncProfiler();
    Path logsDir = dir != null ? dir : defaultDir(fallback, asyncProfiler.appendPidToDirs());
    Files.createDirectories(logsDir);

    return logsDir;
  }

  private static Path defaultDir(String dir, boolean appendPidToDirs) {
    if (appendPidToDirs) {
      var myPid = String.valueOf(ProcessHandle.current().pid());
      return Path.of(System.getProperty("java.io.tmpdir"), "jklee", myPid, dir);
    } else {
      return Path.of(System.getProperty("java.io.tmpdir"), "jklee", dir);
    }
  }
}
