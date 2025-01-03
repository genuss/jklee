package me.genuss.jklee;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
public class Jklee {

  private final JkleeSettings settings;
  private final AsyncProfilerLauncher asyncProfiler;

  public Jklee(JkleeSettings settings) {
    this(settings, new AsyncProfilerLauncher(settings));
  }

  Jklee(JkleeSettings settings, AsyncProfilerLauncher asyncProfiler) {
    this.settings = settings;
    this.asyncProfiler = asyncProfiler;
    if (!settings.enabled()) {
      log.config("jklee is disabled");
    }
  }

  public EffectiveProperties getSettings() {
    if (asyncProfiler.isLoaded()) {
      return EffectiveProperties.builder()
          .agentPathCandidates(settings.asyncProfiler().agentPathCandidates())
          .resultsDir(String.valueOf(asyncProfiler.resultsDir()))
          .cleanResultsDirOnStart(settings.cleanResultsDirOnStart())
          .enabled(settings.enabled())
          .failOnInitErrors(settings.failOnInitErrors())
          .loaded(asyncProfiler.isLoaded())
          .build();
    }

    return EffectiveProperties.builder()
        .agentPathCandidates(settings.asyncProfiler().agentPathCandidates())
        .enabled(settings.enabled())
        .failOnInitErrors(settings.failOnInitErrors())
        .loaded(asyncProfiler.isLoaded())
        .build();
  }

  public ProfilingResponse start(ProfilingRequest request) {
    try {
      asyncProfiler.execute(request);
    } catch (IllegalArgumentException e) {
      return ProfilingResponse.builder()
          .result(ResultCode.INVALID_COMMAND)
          .errorMessage(e.getMessage())
          .build();
    } catch (IllegalStateException e) {
      return ProfilingResponse.builder()
          .result(ResultCode.ASYNC_PROFILER_ERROR)
          .errorMessage(e.getMessage())
          .build();
    } catch (JkleeInactiveException e) {
      return ProfilingResponse.builder()
          .result(ResultCode.JKLEE_NOT_ACTIVE)
          .errorMessage(e.getMessage())
          .build();
    } catch (IOException e) {
      return ProfilingResponse.builder()
          .result(ResultCode.FILESYSTEM_ERROR)
          .errorMessage(e.getMessage())
          .build();
    } catch (Exception e) {
      return ProfilingResponse.builder()
          .result(ResultCode.UNKNOWN)
          .errorMessage(e.getMessage())
          .build();
    }
    return ProfilingResponse.ok();
  }

  public List<ProfilingResult> getAvailableProfilingResults() {
    return asyncProfiler.getAvailableProfilingResults();
  }

  public Path getProfilingResult(String sessionName) {
    return asyncProfiler.getProfilingResult(sessionName);
  }

  @Builder
  @Value
  @Accessors(fluent = true)
  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  @JsonInclude(Include.NON_EMPTY)
  public static class ProfilingRequest {

    String id;
    String rawArguments;
    @Builder.Default Format format = Format.TEXT;
    Duration duration;

    @Getter
    @Accessors(fluent = true)
    public enum Format {
      TEXT("", ".txt"),
      COLLAPSED("collapsed", ".collapsed"),
      FLAMEGRAPH("flamegraph", ".html"),
      TREE("tree", ".html"),
      JFR("jfr", ".jfr"),
      ;

      private final String format;
      private final String extension;

      Format(String format, String extension) {

        this.format = format;
        this.extension = extension;
      }
    }
  }

  @Builder
  @Value
  @Accessors(fluent = true)
  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  @JsonInclude(Include.NON_EMPTY)
  public static class ProfilingResponse {
    ResultCode result;
    String errorMessage;

    static ProfilingResponse ok() {
      return ProfilingResponse.builder().result(ResultCode.STARTED).build();
    }
  }

  @Builder
  @Value
  @Accessors(fluent = true)
  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  @JsonInclude(Include.NON_EMPTY)
  public static class ProfilingResult {
    String name;
    Instant endedAt;
  }

  public enum ResultCode {
    STARTED,
    FILESYSTEM_ERROR,
    JKLEE_NOT_ACTIVE,
    ASYNC_PROFILER_ERROR,
    INVALID_COMMAND,
    UNKNOWN,
  }

  @Builder
  @Value
  @Accessors(fluent = true)
  public static class EffectiveProperties {
    List<String> agentPathCandidates;
    String resultsDir;
    boolean cleanResultsDirOnStart;
    boolean enabled;
    boolean failOnInitErrors;
    boolean loaded;
  }
}

/*
// The format of the string is:
//     arg[,arg...]
// where arg is one of the following options:
//     start            - start profiling
//     resume           - start or resume profiling without resetting collected data
//     stop             - stop profiling
//     dump             - dump collected data without stopping profiling session
//     check            - check if the specified profiling event is available
//     status           - print profiling status (inactive / running for X seconds)
//     list             - show the list of available profiling events
//     version[=full]   - display the agent version
//     event=EVENT      - which event to trace (cpu, wall, cache-misses, etc.)
//     alloc[=BYTES]    - profile allocations with BYTES interval
//     lock[=DURATION]  - profile contended locks longer than DURATION ns
//     collapsed        - dump collapsed stacks (the format used by FlameGraph script)
//     flamegraph       - produce Flame Graph in HTML format
//     tree             - produce call tree in HTML format
//     jfr              - dump events in Java Flight Recorder format
//     jfrsync[=CONFIG] - start Java Flight Recording with the given config along with the profiler
//     traces[=N]       - dump top N call traces
//     flat[=N]         - dump top N methods (aka flat profile)
//     samples          - count the number of samples (default)
//     total            - count the total value (time, bytes, etc.) instead of samples
//     chunksize=N      - approximate size of JFR chunk in bytes (default: 100 MB)
//     chunktime=N      - duration of JFR chunk in seconds (default: 1 hour)
//     interval=N       - sampling interval in ns (default: 10'000'000, i.e. 10 ms)
//     jstackdepth=N    - maximum Java stack depth (default: 2048)
//     safemode=BITS    - disable stack recovery techniques (default: 0, i.e. everything enabled)
//     file=FILENAME    - output file name for dumping
//     log=FILENAME     - log warnings and errors to the given dedicated stream
//     filter=FILTER    - thread filter
//     threads          - profile different threads separately
//     sched            - group threads by scheduling policy
//     cstack=MODE      - how to collect C stack frames in addition to Java stack
//                        MODE is 'fp' (Frame Pointer), 'lbr' (Last Branch Record) or 'no'
//     allkernel        - include only kernel-mode events
//     alluser          - include only user-mode events
//     fdtransfer       - use fdtransfer to pass fds to the profiler
//     simple           - simple class names instead of FQN
//     dot              - dotted class names
//     sig              - print method signatures
//     ann              - annotate Java method names
//     lib              - prepend library names
//     include=PATTERN  - include stack traces containing PATTERN
//     exclude=PATTERN  - exclude stack traces containing PATTERN
//     begin=FUNCTION   - begin profiling when FUNCTION is executed
//     end=FUNCTION     - end profiling when FUNCTION is executed
//     title=TITLE      - FlameGraph title
//     minwidth=PCT     - FlameGraph minimum frame width in percent
//     reverse          - generate stack-reversed FlameGraph / Call tree
//
// It is possible to specify multiple dump options at the same time


 */
