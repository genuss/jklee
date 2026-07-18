# jklee Spring Boot Admin UI Polish — Design

## Goal

Improve the visual polish of the jklee Spring Boot Admin plugin. Scope is
**looks only** — no layout rearrangement, no new features. The work replaces
one-off Tailwind overrides and hand-built markup with Spring Boot Admin's native
global components so the panel blends into the rest of SBA.

Target: Spring Boot Admin **4.0.1**. All `sba-*` components referenced below are
globally registered by SBA (verified against the 4.0.1 `components/index.ts`) and
therefore usable in an extension without imports.

## Affected file

- `spring-boot-admin/src/main/vue/jklee.vue` (single-file component; the only file
  that changes)

## Section 1 — Form controls (Session panel)

- **`sba-select` cleanup:** Remove the redundant one-off class string
  (`focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm
  border-gray-300 rounded-md`). SBA's `sba-select` already applies these focus/
  border/rounded styles internally; the override conflicts with the component.
- **`sba-select` `name` fix:** `name` is a required prop and must be a stable
  field identifier, not display text. Currently the translated *label* is passed
  as `name`. Change to `name="format"` and keep `:label` for the visible text.
- Inputs (`sessionName`, `duration`, `rawArguments`) remain `sba-input`; only
  spacing is normalized (Section 4).

## Section 2 — Profile action & progress (clock gauge)

- Mark the profile button **`primary`** so the main action reads as primary per
  SBA convention.
- While profiling, replace the button's inner content with an inline **SVG clock
  gauge**:
  - A circle outline (the clock face/ring) plus **one hand** rotating around the
    center.
  - Rotation angle = `progressPercentage * 3.6` degrees, bound reactively so it
    advances with the existing 100ms timer. Because it tracks real elapsed time,
    a single full sweep (0→360°) equals one profiling run and self-corrects if
    timing drifts.
  - The **percentage is centered inside the clock face** (gauge style), e.g.
    `42%`.
  - The SVG uses `currentColor` so it inherits the button's text color and stays
    theme-consistent.
- Keep the button **disabled** during the run.
- Keep all existing timer / `progressPercentage` / `parseToMillis` logic
  unchanged; only the rendering inside the button changes.

Note: SBA 4.0.1 registers no standalone progress-bar component globally, so the
custom SVG clock is the progress affordance (no third-party additions).

## Section 3 — Results list

- Replace each raw `<a>` download link with **`sba-button as="a"`**, `href` set to
  the download endpoint (`instances/${instance.id}/actuator/jkleeFiles/${result.name}`),
  keeping the download icon + filename. Gives native button styling and hover
  states, consistent with SBA.
- **Timestamp formatting** (no relative time, no `sba-time-ago`): add a local
  `formatEndedAt(endedAt)` method with **fixed formats**:
  - If the date is **today** → **time only**, `HH:mm:ss`.
  - Otherwise → **full date + time**, `YYYY-MM-DD HH:mm:ss`.
  - Fixed formatting (zero-padded, 24-hour) for consistency across users — not
    locale-based.
- Keep the existing 2-column grid layout. Drop the ad-hoc `border p-2` / manual
  `w-1/2` columns in favor of consistent spacing (Section 4).

## Section 4 — Settings list & spacing consistency

- **Settings panel:** replace the hand-built `v-for` loop (with manual
  `bg-gray-50` zebra striping) with **`sba-key-value-table`**. It renders name/
  value rows with SBA's native zebra striping and semantic colors. Requires a
  small computed property to transform `settings.data` (array of `{name, value}`)
  into the `map` object shape `sba-key-value-table` expects. No data-flow change.
- **Spacing rhythm:** normalize the inconsistent gaps (`gap-4` / `gap-6` / `mr-4`
  / ad-hoc `px-4 py-3`) to a consistent scale across the three panels.

## Out of scope

- No layout rearrangement or feature changes.
- Error handling stays as-is (`error` passed to `sba-instance-section`); not
  converting to `sba-alert`, since that touches behavior.

## Verification

- `./gradlew :spring-boot-admin:npmWatch` (or the module build) compiles the Vue
  component without errors.
- Manual visual check in the sample app (`./gradlew :samples:bootRun`) against a
  running instance: form controls, clock gauge during a profiling run, results
  download buttons + timestamps, and the settings key/value table.
