package me.genuss.jklee;

import java.nio.file.Path;
import java.time.Duration;
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

  public Jklee() {
    this(JkleeSettings.defaults());
  }

  public Jklee(JkleeSettings settings) {
    this(settings, new AsyncProfilerLauncher(settings));
  }

  public Jklee(JkleeSettings settings, AsyncProfilerLauncher asyncProfiler) {
    this.settings = settings;
    this.asyncProfiler = asyncProfiler;
    if (!settings.enabled()) {
      log.config("jklee is disabled");
    }
  }

  public JkleeSettings getSettings() {
    return settings;
  }

  public void start(ProfilingRequest request) {
    asyncProfiler.execute(request);
  }

  public List<String> getAvailableProfilingResults() {
    return asyncProfiler.getAvailableProfilingResults();
  }

  public Path getProfilingResult(String fileName) {
    return asyncProfiler.getProfilingResult(fileName);
  }

  @Builder
  @Value
  @Accessors(fluent = true)
  public static class ProfilingRequest {

    String id;
    Command command;
    String rawArguments;
    @Builder.Default Format format = Format.TEXT;
    Duration duration;
    String event;
    Duration interval;

    @Getter
    @Accessors(fluent = true)
    public enum Command {
      START("start"),
      RESUME("resume"),
      STOP("stop"),
      DUMP("dump"),
      CHECK("check"),
      STATUS("status"),
      LIST("list"),
      ;

      private final String command;

      Command(String command) {
        this.command = command;
      }
    }

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
