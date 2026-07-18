# Auto-Generated Profiling Report Names — Design

## Goal

When a user opens the Jklee panel for an application in Spring Boot Admin, the
"Session Name" field should be pre-filled with a sensible auto-generated name so
they can start a profiling run without inventing a name each time.

## Naming Rules

Given the current application name `<app_name>` (from the Spring Boot Admin
instance) and the list of existing profiling results:

- If there are no reports matching the pattern, use `<app_name>_001`.
- If there are reports named `<app_name>_<number>`, use `max(number) + 1`.
- Reports that do not match the exact pattern (custom names) are ignored.

### Details

- Match pattern: `^<app_name>_(\d+)$` where `<app_name>` is regex-escaped
  before building the pattern (app names may contain `.`, `-`, etc.).
- Numbering is based on the **maximum** existing number + 1, not the count, so
  gaps left by deleted reports never cause collisions.
- Zero-padded to a **minimum** of 3 digits (`001`), growing naturally beyond
  999 (`1000`).
- The app name comes from `instance.registration.name` in the Spring Boot Admin
  instance prop (the label shown in the sidebar, e.g. `jklee-sample`).

## Scope

Frontend-only. No changes to the `core` or `spring-boot` modules — the backend
already exposes the results list (`GET actuator/jkleeFiles`) and accepts the
session name as the profiling id. All logic lives in
`spring-boot-admin/src/main/vue/jklee.vue`.

## Behavior

- On page load, after the results list is fetched, compute the next name and set
  it as `profileRequest.sessionName`.
- After a profiling run completes and the results list is refreshed, recompute
  and update `profileRequest.sessionName` so the field is ready for the next run.
- The field remains editable; if the user overwrites it, their value is used for
  that run. On the next auto-refresh the computed name replaces the field again.

## Implementation Notes

- Add a `computeNextSessionName(appName, results)` helper method that:
  1. Escapes `appName` for use in a regex.
  2. Builds `^<escaped>_(\d+)$`.
  3. Maps matching `results[].name` to their numeric suffix.
  4. Returns `<appName>_<padded(max + 1, min 3)>`, or `<appName>_001` when none
     match.
- Call it in `created()` (after `updateResultsList`) and at the end of
  `updateResultsList()`.
- Remove the hardcoded `sessionName: 'test'` default.

## Testing

- Verify with the sample app: open the panel, confirm the field pre-fills to
  `<app_name>_001` on a clean results dir, and increments after each run.
- Manual check that custom-named reports (e.g. `test1`) are ignored.
