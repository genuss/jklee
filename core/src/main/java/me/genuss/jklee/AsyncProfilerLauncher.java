package me.genuss.jklee;

import static me.genuss.jklee.CommandParser.prepareCommand;
import static me.genuss.jklee.CommandParser.prepareStopCommand;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.java.Log;
import me.genuss.jklee.Jklee.ProfilingRequest;
import one.profiler.AsyncProfiler;

@Log
class AsyncProfilerLauncher {

  private static final ScheduledExecutorService POOL =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            var thread = new Thread(r, "jklee");
            thread.setDaemon(true);
            return thread;
          });

  private final AsyncProfiler asyncProfiler;
  private final Path logsDir;
  private final Path resultsDir;

  public AsyncProfilerLauncher(JkleeSettings settings) {
    asyncProfiler =
        settings.asyncProfiler().agentPathCandidates().stream()
            .map(Path::of)
            .filter(Files::exists)
            .map(this::tryLoad)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    if (asyncProfiler == null && settings.failOnInitErrors()) {
      throw new IllegalStateException("Couldn't find or load async profiler library");
    } else if (asyncProfiler == null) {
      log.config(
          "Agent path not specified or async profiler couldn't be loaded from there. Disabling jklee");
      logsDir = null;
      resultsDir = null;
      return;
    }

    Path logsDir;
    try {
      logsDir = DirsHelper.prepareLogsDir(settings);
    } catch (IOException e) {
      if (settings.failOnInitErrors()) {
        throw new UncheckedIOException(e);
      }
      this.logsDir = null;
      this.resultsDir = null;
      return;
    }
    this.logsDir = logsDir;

    Path resultsDir;
    try {
      resultsDir = DirsHelper.prepareResultsDir(settings);
    } catch (IOException e) {
      if (settings.failOnInitErrors()) {
        throw new UncheckedIOException(e);
      }
      this.resultsDir = null;
      return;
    }
    this.resultsDir = resultsDir;
  }

  public void execute(ProfilingRequest request) {
    var startCommand = prepareCommand(request, resultsDir, logsDir);
    log.fine("Executing raw command: " + startCommand);
    try {
      asyncProfiler.execute(startCommand);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    POOL.schedule(
        () -> {
          var stopCommand = prepareStopCommand(request, resultsDir, logsDir);
          log.fine("Executing stop command: " + stopCommand);
          return asyncProfiler.execute(stopCommand);
        },
        request.duration().toMillis(),
        TimeUnit.MILLISECONDS);
  }

  public List<String> getAvailableProfilingResults() {
    try (var stream = Files.find(resultsDir, 1, this::isResult)) {
      return stream
          .map(Path::getFileName)
          .map(Path::toString)
          .sorted()
          .collect(Collectors.toList());
    } catch (IOException e) {
      return List.of();
    }
  }

  public Path getProfilingResult(String fileName) {
    try (var stream = Files.find(resultsDir, 1, this::isResult)) {
      return stream
          .filter(path -> path.getFileName().toString().equals(fileName))
          .findAny()
          .orElse(null);
    } catch (IOException e) {
      return null;
    }
  }

  private AsyncProfiler tryLoad(Path path) {
    Path realPath;
    try {
      realPath = path.toRealPath();
    } catch (IOException e) {
      log.fine("Not a path: " + path);
      return null;
    }
    AsyncProfiler asyncProfiler;
    try {
      asyncProfiler = AsyncProfiler.getInstance(realPath.toString());
    } catch (Exception e) {
      log.fine(
          () ->
              String.format(
                  "Couldn't load async profiler from %s. Error was: %s", realPath, e.getMessage()));
      return null;
    }
    log.info(
        () ->
            String.format(
                "Loaded async profiler native library from '%s'. Version: %s",
                realPath, asyncProfiler.getVersion()));
    return asyncProfiler;
  }

  private boolean isResult(Path path, BasicFileAttributes attributes) {
    return attributes.isRegularFile();
  }
}
