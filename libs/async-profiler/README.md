# async-profiler native libraries

These are pre-built native libraries from the [async-profiler](https://github.com/async-profiler/async-profiler) project, used by integration tests to verify that jklee can load the profiler agent.

## Version

**async-profiler v4.3**

## Source

Downloaded from: https://github.com/async-profiler/async-profiler/releases/tag/v4.3

Archives used:
- `async-profiler-4.3-macos.zip` -> `macos/libasyncProfiler.dylib`
- `async-profiler-4.3-linux-x64.tar.gz` -> `linux-x64/libasyncProfiler.so`
- `async-profiler-4.3-linux-arm64.tar.gz` -> `linux-arm64/libasyncProfiler.so`

## How to update

1. Download the new release archives from the [async-profiler releases page](https://github.com/async-profiler/async-profiler/releases)
2. Extract the native libraries:
   - macOS: `libasyncProfiler.dylib` from `lib/` in the zip
   - Linux x64: `libasyncProfiler.so` from `lib/` in the tar.gz
   - Linux arm64: `libasyncProfiler.so` from `lib/` in the tar.gz
3. Replace the files in the corresponding platform directories
4. Update this README with the new version number

## License

async-profiler is licensed under the Apache License 2.0.
See https://github.com/async-profiler/async-profiler/blob/master/LICENSE for details.
