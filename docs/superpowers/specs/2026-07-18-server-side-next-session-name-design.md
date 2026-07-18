# Server-Side `nextSessionName` — Design

## Goal

Pre-fill the "Session Name" field in the Spring Boot Admin Jklee panel with a
sensible auto-generated name. Unlike the frontend-only variant, the running
application instance computes the suggested name and exposes it as
`nextSessionName`; the client simply consumes that value.

## Naming Rules

Given the application name `<app_name>` (from `spring.application.name`) and the
list of existing profiling results:

- If no report matches the pattern, use `<app_name>_001`.
- If reports named `<app_name>_<number>` exist, use `max(number) + 1`.
- Reports not matching the exact pattern (custom names) are ignored.

### Details

- Match pattern: `^<app_name>_(\d+)$`, with `<app_name>` regex-escaped.
- Numbering is `max + 1`, so gaps from deleted reports never collide.
- Zero-padded to a minimum of 3 digits (`001`), growing naturally beyond 999.
- When `spring.application.name` is unset, the app name is an empty string,
  producing `_001`, `_002`, ...

## Scope

Server side lives entirely in the **spring-boot** module. This is a Spring Boot
Admin convenience feature, so the logic stays out of `core`.

- The naming logic is a package-private `static` method inside
  `JkleeFilesEndpoint` for easy unit testing.
- `JkleeFilesEndpoint` holds the application name (constructor-injected).
- The `jkleeFiles` read response (`ProfilingResultFiles`) gains a
  `nextSessionName` field, computed in `getResults()`.
- `JkleeAutoConfiguration` injects `${spring.application.name:}` into the
  `jkleeFilesEndpoint` bean.

Client side lives in **spring-boot-admin** (`jklee.vue`):

- The `jkleeFiles` fetch already runs on load and after each run. Read
  `response.data.nextSessionName` and set it as `profileRequest.sessionName`.
- Remove the hardcoded `sessionName: 'test'` default.
- The field stays editable; on the next refresh the server value replaces it.

## Behavior

- On page load, after the results list is fetched, set
  `profileRequest.sessionName` from `nextSessionName`.
- After a profiling run completes and the list refreshes, update the field
  again from the new `nextSessionName`.

## Testing

- `JkleeFilesEndpointTest` (spring-boot module) covering the naming logic:
  no-match → `_001`, `max + 1`, custom names ignored, growth past 999, regex
  escaping in the app name, and empty app name.
- Manual check with the sample app.
