# jklee Spring Boot Admin UI Polish Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Improve the visual polish of the jklee Spring Boot Admin plugin by replacing one-off Tailwind overrides and hand-built markup with SBA's native global components — looks only, no layout rearrangement or new features.

**Architecture:** All changes live in one Vue single-file component, `spring-boot-admin/src/main/vue/jklee.vue`. We swap hand-built markup for SBA 4.0.1 global components (`sba-select`, `sba-button`, `sba-key-value-table`), add an inline SVG clock gauge for profiling progress, and add a local fixed-format timestamp helper. Template and small script additions only; existing data flow, timers, and endpoints are untouched.

**Tech Stack:** Vue 3.3.4 (Options API), Vite 4.4.9, Tailwind utility classes, Spring Boot Admin 4.0.1 global `sba-*` components.

## Global Constraints

- Target Spring Boot Admin **4.0.1**; only use globally-registered `sba-*` components (verified in 4.0.1 `components/index.ts`).
- **Looks only** — no layout rearrangement, no feature/behavior changes.
- Do not add npm dependencies; the module bundles only `vue` (externalized) and uses SBA's runtime-provided components.
- No comments in code (project code style).
- Google Java Format / Spotless does not apply to `.vue`; keep existing 2-space indentation and style of the file.
- Timestamps use **fixed** formatting (zero-padded, 24-hour), not locale-based.
- The only file that changes is `spring-boot-admin/src/main/vue/jklee.vue`.

**Verification note:** This module has no JS test harness (no test deps in `package.json`). Each task verifies by building the component (compile success) plus a described manual visual check. There is no failing-test-first cycle for these UI tasks.

**Build command (run from repo root):**
```bash
./gradlew :spring-boot-admin:build
```
Expected: `BUILD SUCCESSFUL`, and `spring-boot-admin/src/main/resources/META-INF/spring-boot-admin-server-ui/extensions/jklee/jklee.js` is regenerated without errors.

---

### Task 1: Fix `sba-select` overrides and `name` prop

**Files:**
- Modify: `spring-boot-admin/src/main/vue/jklee.vue` (the `sba-select` block in the Session panel)

**Interfaces:**
- Consumes: existing `profileProperties.formats` (array of option objects) and `profileRequest.format` (string) from component data.
- Produces: no new script symbols.

- [ ] **Step 1: Remove the redundant class override and fix `name`**

In the Session panel, replace the current `sba-select`:

```html
<sba-select
  :name="$t('jklee.ui.session.format')"
  :label="$t('jklee.ui.session.format')"
  :options="profileProperties.formats"
  v-model="profileRequest.format"
  class="focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md"
>
</sba-select>
```

with:

```html
<sba-select
  name="format"
  :label="$t('jklee.ui.session.format')"
  :options="profileProperties.formats"
  v-model="profileRequest.format"
/>
```

- [ ] **Step 2: Build to verify it compiles**

Run: `./gradlew :spring-boot-admin:build`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Manual visual check**

Run the sample app (`./gradlew :samples:bootRun`), open the jklee view for an instance, confirm the Format select renders with SBA's native styling (focus ring, border, rounded) and matches the sibling `sba-input` fields; selecting a format still updates `profileRequest.format`.

- [ ] **Step 4: Commit**

```bash
git add spring-boot-admin/src/main/vue/jklee.vue
git commit -m "Fix sba-select overrides and name prop in jklee UI"
```

---

### Task 2: Profile button — primary + SVG clock gauge

**Files:**
- Modify: `spring-boot-admin/src/main/vue/jklee.vue` (profile `sba-button` block in Session panel; `computed` block)

**Interfaces:**
- Consumes: existing `profiling` (boolean) and `progressPercentage` (computed number 0–100) from the component.
- Produces: new computed `clockHandAngle` (number, degrees 0–360) used only within this component's template.

- [ ] **Step 1: Add the `clockHandAngle` computed**

In the `computed` block, alongside the existing `progressPercentage`, add:

```js
clockHandAngle() {
  return this.progressPercentage * 3.6;
},
```

- [ ] **Step 2: Replace the profile button content with primary + clock gauge**

Replace the current profile `sba-button`:

```html
<sba-button
  :disabled="profiling"
  @click="profile(profileRequest)"
  class="relative overflow-hidden"
>
  <template v-if="!profiling">
    <font-awesome-icon icon="download"/>
    <span v-text="$t('jklee.ui.session.start')"/>
  </template>
  <template v-else>
    <div class="flex items-center">
      <span>{{ Math.floor(progressPercentage) }}%</span>
    </div>
  </template>
</sba-button>
```

with:

```html
<sba-button
  primary
  :disabled="profiling"
  @click="profile(profileRequest)"
>
  <template v-if="!profiling">
    <font-awesome-icon icon="download"/>
    <span v-text="$t('jklee.ui.session.start')"/>
  </template>
  <template v-else>
    <span class="inline-flex items-center justify-center">
      <svg
        viewBox="0 0 36 36"
        class="w-10 h-10"
        fill="none"
        stroke="currentColor"
      >
        <circle cx="18" cy="18" r="16" stroke-width="2" opacity="0.3"/>
        <line
          x1="18"
          y1="18"
          x2="18"
          y2="5"
          stroke-width="2"
          stroke-linecap="round"
          :transform="`rotate(${clockHandAngle} 18 18)`"
        />
        <text
          x="18"
          y="19"
          text-anchor="middle"
          dominant-baseline="middle"
          fill="currentColor"
          stroke="none"
          font-size="8"
        >{{ Math.floor(progressPercentage) }}%</text>
      </svg>
    </span>
  </template>
</sba-button>
```

- [ ] **Step 3: Build to verify it compiles**

Run: `./gradlew :spring-boot-admin:build`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Manual visual check**

In the sample app, start a profiling run (e.g. duration `5s`). Confirm: the button is `primary`-styled when idle; during the run the button shows a clock face with a single hand sweeping from 12 o'clock through a full 360° circle over the run duration, the centered percentage counts up 0→100%, and the button is disabled. After the run it returns to the idle download label.

- [ ] **Step 5: Commit**

```bash
git add spring-boot-admin/src/main/vue/jklee.vue
git commit -m "Add primary profile button with SVG clock gauge progress"
```

---

### Task 3: Results list — download buttons + fixed timestamp formatting

**Files:**
- Modify: `spring-boot-admin/src/main/vue/jklee.vue` (results grid in Results panel; `methods` block)

**Interfaces:**
- Consumes: existing `results` array of `{ name, endedAt }`; `instance.id`.
- Produces: new method `formatEndedAt(endedAt)` returning a string (`HH:mm:ss` if today, else `YYYY-MM-DD HH:mm:ss`).

- [ ] **Step 1: Add the `formatEndedAt` method**

In the `methods` block, add:

```js
formatEndedAt(endedAt) {
  if (!endedAt) return '';
  const date = new Date(endedAt);
  if (isNaN(date.getTime())) return endedAt;
  const pad = (n) => String(n).padStart(2, '0');
  const time = `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
  const now = new Date();
  const isToday =
    date.getFullYear() === now.getFullYear() &&
    date.getMonth() === now.getMonth() &&
    date.getDate() === now.getDate();
  if (isToday) return time;
  const day = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
  return `${day} ${time}`;
},
```

- [ ] **Step 2: Replace the results grid item markup**

Replace the current results `<template v-for>` item:

```html
<div class="border p-2 flex">
  <div class="w-1/2 pr-2">
    <a :href="`instances/${instance.id}/actuator/jkleeFiles/${result.name}`">
      <font-awesome-icon icon="download" class="mr-2"/>
      <span v-text="result.name"/>
    </a>
  </div>
  <div class="w-1/2" v-text="result.endedAt"/>
</div>
```

with:

```html
<div class="flex items-center gap-4">
  <sba-button
    as="a"
    :href="`instances/${instance.id}/actuator/jkleeFiles/${result.name}`"
    class="flex-1"
  >
    <font-awesome-icon icon="download" class="mr-2"/>
    <span v-text="result.name"/>
  </sba-button>
  <span class="text-sm text-gray-500" v-text="formatEndedAt(result.endedAt)"/>
</div>
```

- [ ] **Step 3: Build to verify it compiles**

Run: `./gradlew :spring-boot-admin:build`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Manual visual check**

In the sample app, run a profile to produce a result. Confirm each result row shows a native SBA button (download icon + filename) that links to the download endpoint, and a timestamp: time-only (`HH:mm:ss`) for results created today, full `YYYY-MM-DD HH:mm:ss` for older results.

- [ ] **Step 5: Commit**

```bash
git add spring-boot-admin/src/main/vue/jklee.vue
git commit -m "Use sba-button download links and fixed timestamps in jklee results"
```

---

### Task 4: Settings panel — `sba-key-value-table` and spacing rhythm

**Files:**
- Modify: `spring-boot-admin/src/main/vue/jklee.vue` (Settings panel; `computed` block; panel spacing across all three panels)

**Interfaces:**
- Consumes: existing `settings.data` (array of `{ name, value }`).
- Produces: new computed `settingsMap` (object mapping `name` → `value`) passed to `sba-key-value-table`'s `map` prop.

- [ ] **Step 1: Add the `settingsMap` computed**

In the `computed` block, add:

```js
settingsMap() {
  return this.settings.data.reduce((acc, setting) => {
    acc[setting.name] = setting.value;
    return acc;
  }, {});
},
```

- [ ] **Step 2: Replace the Settings panel body**

Replace the current Settings `<template v-for>`:

```html
<template v-for="(setting, index) in settings.data" :key="setting.name">
  <div>
    <div class="flex items-center px-4 py-3" :class="{
      'bg-gray-50': index % 2 === 0,
    }">
      <div class="flex-1 sm:break-all">
        <span v-text="setting.name"/>
      </div>
      <div>
        <span v-text="setting.value"/>
      </div>
    </div>
  </div>
</template>
```

with:

```html
<sba-key-value-table :map="settingsMap"/>
```

- [ ] **Step 3: Normalize panel spacing**

In the Results panel grid, change `class="grid grid-cols-2 gap-4"` to `class="grid grid-cols-2 gap-4 mt-2"` for consistent top spacing (leave the grid gap at `gap-4`).

In the Session panel, change the second row wrapper `class="flex"` (the one containing the arguments input and the profile button) to `class="flex gap-4 items-end mt-4"`, and remove the now-redundant `mr-4` from its inner arguments `<div class="flex-1 mr-4">` → `<div class="flex-1">`. Change the first row wrapper `class="flex gap-6 flex-col lg:flex-row"` to `class="flex gap-4 flex-col lg:flex-row"` so both rows share the same `gap-4` rhythm.

- [ ] **Step 4: Build to verify it compiles**

Run: `./gradlew :spring-boot-admin:build`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Manual visual check**

In the sample app, confirm the Settings panel renders as an SBA key/value table with native zebra striping (dark-mode aware), showing each setting name and value. Confirm the Session panel rows and the Results grid have consistent, even spacing and the profile button aligns with the arguments input.

- [ ] **Step 6: Commit**

```bash
git add spring-boot-admin/src/main/vue/jklee.vue
git commit -m "Use sba-key-value-table for settings and normalize jklee spacing"
```

---

## Self-Review

**Spec coverage:**
- Section 1 (sba-select cleanup + name fix) → Task 1. ✓
- Section 2 (primary button + clock gauge, centered %) → Task 2. ✓
- Section 3 (sba-button download links + fixed HH:mm:ss / YYYY-MM-DD HH:mm:ss) → Task 3. ✓
- Section 4 (sba-key-value-table + spacing rhythm) → Task 4. ✓
- Out-of-scope items (no layout rearrangement, error handling untouched) respected across tasks. ✓

**Placeholder scan:** No TBD/TODO; all code steps show full code. ✓

**Type consistency:** `progressPercentage` (existing) → `clockHandAngle` (Task 2); `settings.data` → `settingsMap` (Task 4); `formatEndedAt` (Task 3) referenced only in Task 3 template. Names consistent across tasks. ✓
