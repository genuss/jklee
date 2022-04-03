package me.genuss.jklee;

import java.nio.file.Path;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingRequest.Command;

class CommandParser {

  static String prepareCommand(ProfilingRequest request, Path resultsDir, Path logsDir) {
    if (request.rawArguments() != null) {
      return request.rawArguments() + fileAndLogArgs(request, resultsDir, logsDir);
    }

    if (request.command() == Command.START) {
      return request.command().command()
          + ",event="
          + request.event()
          + ",interval="
          + request.interval().toNanos()
          + fileAndLogArgs(request, resultsDir, logsDir);
    }
    throw new UnsupportedOperationException(
        String.format("Command %s is not supported yet", request.command()));
  }

  public static String prepareStopCommand(ProfilingRequest request, Path resultsDir, Path logsDir) {
    return "stop" + fileAndLogArgs(request, resultsDir, logsDir);
  }

  private static String fileAndLogArgs(ProfilingRequest request, Path resultsDir, Path logsDir) {
    var id = request.id();
    var formatString = request.format().format().isEmpty() ? "," + request.format().format() : "";
    var resultFile = resultsDir + "/" + id + request.format().extension();
    var logFile = logsDir + "/" + id + ".log";
    return formatString + ",file=" + resultFile + ",log=" + logFile;
  }
}
