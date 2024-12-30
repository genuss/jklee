# <img src="logo.png" style="width: 100, height:100"> Jklee


Jklee is a Java library that provides a convenient wrapper
around [async-profiler](https://github.com/async-profiler/async-profiler), allowing you to easily
invoke it from within a JVM-based application.

The name _jklee_ comes from the German word Klee, which means clover.

## Features

* Simple Java-API, which could be used in any JVM-based application
* Integration with Spring Boot
* Spring Boot Actuator endpoints for profiling and retrieving results
* [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) custom view as GUI for
  profiling and retrieving results.

## Getting Started

### Prerequisites

* Java 17 or higher
* Both Spring Boot 2.x and 3.x are supported for actuator endpoints (tested on 2.6.x and later)
* Spring boot 3.x is required for spring-boot-admin custom view module

### Installation

Use artifacts of choice. In kotlin-gradle syntax:

```kotlin
dependencies {
  implementation(platform("me.genuss.jklee:bom:$version"))
  implementation("me.genuss.jklee:core")
  implementation("me.genuss.jklee:spring-boot")
  implementation("me.genuss.jklee:spring-boot-admin")
}
```

* `bom` is the artifact which provides dependency management for the library.
  This could be handy if you are using multiple modules of the library.
* `core` is the module which provides the Java API.
* `spring-boot` is the module which provides the Spring Boot Actuator integration.
* `spring-boot-admin` is the module which provides the Spring Boot Admin custom view.
  It's intended to be used as a part of your Spring Boot Admin server.

### Configuration

The only required setting is `AsyncProfiler#agentPathCandidates`.
This is a list of paths where async-profiler will be searched for.
The first found library will be used.
For other knobs and dials, see `me.genuss.jklee.JkleeSettings` class.

### Usage of `core` module

Here is an example of how to use Jklee:

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.random.RandomGenerator;
import me.genuss.jklee.Jklee;
import me.genuss.jklee.Jklee.ProfilingRequest;
import me.genuss.jklee.Jklee.ProfilingResponse;
import me.genuss.jklee.Jklee.ResultCode;
import me.genuss.jklee.JkleeSettings;
import me.genuss.jklee.JkleeSettings.AsyncProfiler;

class JkleeExample {

  public static void main(String[] args) throws Exception {
    // Replace this value with the actual path on your machine
    var agentPaths = List.of("/opt/async-profiler/lib/libasyncProfiler.dylib");
    var jklee =
        new Jklee(
            JkleeSettings.builder()
                .asyncProfiler(AsyncProfiler.builder().agentPathCandidates(agentPaths).build())
                .build());
    // The Duration here is very short for demonstration purposes
    var profilingDuration = Duration.ofSeconds(1);
    // Every profiling must have a unique ID. This id corresponds to the filename of the result and
    // is used for retrieving the result after profiling is done.
    var profilingId = "profile-" + RandomGenerator.getDefault().nextInt();
    // These args are passed to async-profiler. See docs here:
    // https://github.com/async-profiler/async-profiler/blob/5a3636d5dfa5b9da323d29c692db5597d2393194/docs/ProfilerOptions.md
    var asyncProfilerArgs = "start,event=itimer,interval=1ms";
    ProfilingResponse response =
        jklee.start(
            ProfilingRequest.builder()
                .id(profilingId)
                .rawArguments(asyncProfilerArgs)
                .duration(profilingDuration)
                .format(ProfilingRequest.Format.TEXT)
                .build());
    if (response.result() != ResultCode.STARTED)
      throw new IllegalStateException("Failed to start profiling: " + response.errorMessage());

    // Wait for profiling to finish
    Thread.sleep(profilingDuration.toMillis());

    Path resultLocation = jklee.getProfilingResult(profilingId);
    System.out.printf("Profiling result saved in %s%n", resultLocation);
    // The result is truncated for brevity
    Files.readString(resultLocation).lines().limit(10).forEach(System.out::println);
    System.out.println("... TRUNCATED ...");
  }
}

```

After running the snippet, you should see the truncated profiling result in the console or an error
indicating that something is wrong with the configuration.

### Usage in Spring Boot Admin

You can manage your profiling sessions and retrieve results using Spring Boot Admin custom view.
To do it, you need a Spring Boot Admin server with `spring-boot-admin` module enabled.
The custom view will be available under the `jklee` tab.

### Usage in Spring Boot

If you aren't willing to use Spring Boot Admin, there is a `spring-boot` module for you.
It contains autoconfiguration which is enabled by default.
You can disable it by setting `jklee.enabled` to `false`.
All the knobs and dials are available as configuration properties under `jklee` prefix.
Use your IDE autocompletion to see what's available.
An example application is available in the `samples` gradle module.

Profiling is implemented as actuator endpoints to achieve maximal portability.
To start a profiling session, issue an actuator write operation to
`me.genuss.jklee.JkleeProfileEndpoint#start()`.
To get the result, issue a read operation to
`me.genuss.jklee.JkleeFilesEndpoint#download()`.

## Contributing

The project is in the early stages of development, so contributions are very welcome!
If you'd like to help, please open an issue.
In case your change isn't large, a pull request is even better.

## Roadmap

There are some features/improvements I'd like to implement in the future:

* Better requests validation.
* Better error handling.
* Better UI.
  I'm not a frontend developer, so UI looks not very good at the moment.
  Any help in this area would be much appreciated.
* Profiling progress bar when it's in progress.
* Hiding jklee tab when async-profiler couldn't be loaded.
* Better request builder: instead of asking for raw arguments, implement a type-safe builder.

Very long-term goals:

* Continuous profiling.
* Automatic downloading of async-profiler binaries. Embedding binaries in jars?
* Support for other frameworks like Quarkus, Micronaut, etc.
