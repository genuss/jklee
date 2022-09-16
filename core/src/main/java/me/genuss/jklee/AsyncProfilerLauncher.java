package me.genuss.jklee;

import static java.util.function.Predicate.not;
import static me.genuss.jklee.CommandParser.FILENAME_RESULT;
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
      resultsDir = null;
      return;
    }

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
    var sessionDir = getSessionDirPath(request.id());
    try {
      Files.createDirectories(sessionDir);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    var startCommand = prepareCommand(request, sessionDir);
    log.fine("Executing raw command: " + startCommand);
    try {
      asyncProfiler.execute(startCommand);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    POOL.schedule(
        () -> {
          var stopCommand = prepareStopCommand(request, sessionDir);
          log.fine("Executing stop command: " + stopCommand);
          return asyncProfiler.execute(stopCommand);
        },
        request.duration().toMillis(),
        TimeUnit.MILLISECONDS);
  }

  public List<String> getAvailableProfilingResults() {
    try (var stream = Files.find(resultsDir, 1, this::isDir)) {
      return stream
          .filter(not(resultsDir::equals))
          .map(Path::getFileName)
          .map(Path::toString)
          .sorted()
          .collect(Collectors.toList());
    } catch (IOException e) {
      return List.of();
    }
  }

  public Path getProfilingResult(String sessionName) {
    try (var stream = Files.find(getSessionDirPath(sessionName), 1, this::isFile)) {
      return stream
          .filter(path -> path.getFileName().toString().startsWith(FILENAME_RESULT))
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

  private Path getSessionDirPath(String sessionName) {
    return Path.of(resultsDir.toString(), sessionName);
  }

  private boolean isDir(Path path, BasicFileAttributes attributes) {
    return attributes.isDirectory();
  }

  private boolean isFile(Path path, BasicFileAttributes attributes) {
    return attributes.isRegularFile();
  }
}
