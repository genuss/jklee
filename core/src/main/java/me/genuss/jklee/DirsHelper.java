package me.genuss.jklee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class DirsHelper {

  static Path prepareResultsDir(JkleeSettings settings) throws IOException {
    return getPath(settings, settings.asyncProfiler().resultsDir());
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
      var myPid = String.valueOf(ProcessHandle.current().pid());
      return Path.of(tmpDir, "jklee", myPid);
    } else {
      return Path.of(tmpDir, "jklee");
    }
  }
}
