package me.genuss.jklee;

import java.io.File;
import java.nio.file.Path;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingRequest.Command;

class CommandParser {

  public static final String FILENAME_RESULT = "result";
  public static final String FILENAME_LOG = "async-profiler.log";

  static String prepareCommand(ProfilingRequest request, Path sessionDir) {
    if (request.rawArguments() != null) {
      return request.rawArguments() + fileAndLogArgs(request, sessionDir);
    }

    if (request.command() == Command.START) {
      return request.command().command()
          + ",event="
          + request.event()
          + ",interval="
          + request.interval().toNanos()
          + fileAndLogArgs(request, sessionDir);
    }
    throw new UnsupportedOperationException(
        String.format("Command %s is not supported yet", request.command()));
  }

  public static String prepareStopCommand(ProfilingRequest request, Path sessionDir) {
    return "stop" + fileAndLogArgs(request, sessionDir);
  }

  private static String fileAndLogArgs(ProfilingRequest request, Path sessionDir) {
    var formatString = request.format().format().isEmpty() ? "," + request.format().format() : "";
    var resultFile = sessionDir + File.separator + FILENAME_RESULT + request.format().extension();
    var logFile = sessionDir + File.separator + FILENAME_LOG;
    return formatString + ",file=" + resultFile + ",log=" + logFile;
  }
}
