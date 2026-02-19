package me.genuss.jklee;

import java.io.File;
import java.nio.file.Path;
import me.genuss.jklee.Jklee.ProfilingRequest;

class CommandParser {

  public static final String FILENAME_RESULT = "result";
  public static final String FILENAME_LOG = "async-profiler.log";

  static String prepareCommand(ProfilingRequest request, Path sessionDir) {
    if (request.rawArguments() != null) {
      return request.rawArguments() + fileAndLogArgs(request, sessionDir);
    }

    throw new UnsupportedOperationException("Parser without raw arguments is not implemented");
  }

  public static String prepareStopCommand(ProfilingRequest request, Path sessionDir) {
    return "stop" + fileAndLogArgs(request, sessionDir);
  }

  private static String fileAndLogArgs(ProfilingRequest request, Path sessionDir) {
    String formatString =
        request.format().format().isEmpty() ? "," + request.format().format() : "";
    String resultFile =
        sessionDir + File.separator + FILENAME_RESULT + request.format().extension();
    String logFile = sessionDir + File.separator + FILENAME_LOG;
    return formatString + ",file=" + resultFile + ",log=" + logFile;
  }
}
