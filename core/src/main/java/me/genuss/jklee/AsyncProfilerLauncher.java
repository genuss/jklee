package me.genuss.jklee;

import static java.lang.String.format;
import static me.genuss.jklee.CommandParser.FILENAME_RESULT;
import static me.genuss.jklee.CommandParser.prepareCommand;
import static me.genuss.jklee.CommandParser.prepareStopCommand;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingResult;
import one.profiler.AsyncProfiler;

@Accessors(fluent = true)
@Getter
@Log
class AsyncProfilerLauncher {

  private static final ScheduledExecutorService POOL =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread thread = new Thread(r, "jklee");
            thread.setDaemon(true);
            return thread;
          });

  private final AsyncProfiler asyncProfiler;
  private final Path resultsDir;

  public AsyncProfilerLauncher(JkleeSettings settings) {
    asyncProfiler =
        settings.asyncProfiler().agentPathCandidates().stream()
            .map(DirsHelper::path)
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

  public void execute(ProfilingRequest request) throws Exception {
    if (!isLoaded()) {
      throw new JkleeInactiveException("Async profiler is not loaded");
    }
    validate(request);
    Path sessionDir = getSessionDir(request.id());
    Files.createDirectories(sessionDir);
    String startCommand = prepareCommand(request, sessionDir);
    log.fine("Executing raw command: " + startCommand);
    asyncProfiler.execute(startCommand);

    if (request.duration() != null) {
      POOL.schedule(() -> stop(request), request.duration().toMillis(), TimeUnit.MILLISECONDS);
    }
  }

  public List<ProfilingResult> getAvailableProfilingResults() {
    if (!isLoaded()) {
      return Collections.emptyList();
    }
    try (Stream<Path> stream = Files.find(resultsDir, 1, this::isDir)) {
      return stream
          .filter(other -> !resultsDir.equals(other))
          .map(this::toProfilingResult)
          .sorted(Comparator.comparing(ProfilingResult::endedAt).reversed())
          .collect(Collectors.toList());
    } catch (IOException e) {
      return Collections.emptyList();
    }
  }

  public Path getProfilingResult(String sessionName) {
    if (!isLoaded()) {
      return null;
    }
    try (Stream<Path> stream = Files.find(getSessionDir(sessionName), 1, this::isFile)) {
      return stream
          .filter(path -> path.getFileName().toString().startsWith(FILENAME_RESULT))
          .findAny()
          .orElse(null);
    } catch (IOException e) {
      return null;
    }
  }

  public boolean isLoaded() {
    return asyncProfiler != null;
  }

  private void stop(ProfilingRequest request) {
    Path sessionDir = getSessionDir(request.id());
    String stopCommand = prepareStopCommand(request, sessionDir);
    log.fine(() -> String.format("Executing stop command: %s", stopCommand));
    try {
      String result = asyncProfiler.execute(stopCommand);
      log.fine(
          () -> format("Stopped async profiler session %s with result: %s", request.id(), result));
    } catch (Exception e) {
      log.warning(() -> format("Error while stopping async profiler: %s", e.getMessage()));
    }
  }

  private ProfilingResult toProfilingResult(Path path) {
    return ProfilingResult.builder()
        .name(path.getFileName().toString())
        .endedAt(Instant.ofEpochMilli(path.toFile().lastModified()))
        .build();
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
    } catch (Exception | UnsatisfiedLinkError e) {
      log.fine(
          () ->
              format(
                  "Couldn't load async profiler from %s. Error was: %s", realPath, e.getMessage()));
      return null;
    }
    log.info(
        () ->
            format(
                "Loaded async profiler native library from '%s'. Version: %s",
                realPath, asyncProfiler.getVersion()));
    return asyncProfiler;
  }

  private void validate(ProfilingRequest request) {
    if (request.id() == null || request.id().isEmpty()) {
      throw new IllegalArgumentException("Session id is required");
    }
    if (request.rawArguments() == null || request.rawArguments().isEmpty()) {
      throw new IllegalArgumentException("Raw arguments are required");
    }
    if (request.format() == null) {
      throw new IllegalArgumentException("Format is required");
    }
  }

  private Path getSessionDir(String sessionName) {
    return DirsHelper.path(resultsDir.toString(), sessionName);
  }

  private boolean isDir(Path path, BasicFileAttributes attributes) {
    return attributes.isDirectory();
  }

  private boolean isFile(Path path, BasicFileAttributes attributes) {
    return attributes.isRegularFile();
  }
}
