# Auto-Generated Profiling Report Names Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Pre-fill the Spring Boot Admin "Session Name" field with an auto-generated name `<app_name>_<NNN>` that increments based on existing matching reports.

**Architecture:** Frontend-only change in the `spring-boot-admin` Vue plugin. The pure naming logic is extracted into a standalone ES module (`sessionName.js`) so it can be unit-tested with vitest, then wired into `jklee.vue` on load and after each results refresh.

**Tech Stack:** Vue 3, Vite, vitest (added as devDependency).

## Global Constraints

- App name source: `instance.registration.name` (Spring Boot Admin instance prop).
- Match pattern: `^<escaped_app_name>_(\d+)$`; app name must be regex-escaped.
- Next number = max matching number + 1; `001` when no matches. Ignore non-matching (custom) names.
- Zero-pad to a minimum of 3 digits; grow naturally beyond 999.
- No changes to `core` or `spring-boot` modules.
- All commands run from `spring-boot-admin/`.

---

### Task 1: Pure session-name generator + tests

**Files:**
- Create: `spring-boot-admin/src/main/vue/sessionName.js`
- Create: `spring-boot-admin/src/main/vue/sessionName.test.js`
- Modify: `spring-boot-admin/package.json` (add vitest devDependency + `test` script)

**Interfaces:**
- Produces: `computeNextSessionName(appName: string, results: Array<{name: string}>): string`

- [ ] **Step 1: Add vitest and a test script to package.json**

In `spring-boot-admin/package.json`, add to `scripts`:

```json
    "test": "vitest run"
```

Add to `devDependencies`:

```json
    "vitest": "^1.6.0"
```

- [ ] **Step 2: Install the new devDependency**

Run: `cd spring-boot-admin && npm install`
Expected: `node_modules/.bin/vitest` exists, no errors.

- [ ] **Step 3: Write the failing test**

Create `spring-boot-admin/src/main/vue/sessionName.test.js`:

```js
import { describe, it, expect } from 'vitest';
import { computeNextSessionName } from './sessionName.js';

describe('computeNextSessionName', () => {
  it('returns <app>_001 when there are no results', () => {
    expect(computeNextSessionName('jklee-sample', [])).toBe('jklee-sample_001');
  });

  it('increments the max matching number', () => {
    const results = [
      { name: 'jklee-sample_001' },
      { name: 'jklee-sample_007' },
      { name: 'jklee-sample_003' },
    ];
    expect(computeNextSessionName('jklee-sample', results)).toBe('jklee-sample_008');
  });

  it('ignores custom-named reports', () => {
    const results = [
      { name: 'test1' },
      { name: 'jklee-sample' },
      { name: 'jklee-sample_foo' },
      { name: 'jklee-sample_002' },
    ];
    expect(computeNextSessionName('jklee-sample', results)).toBe('jklee-sample_003');
  });

  it('grows beyond 3 digits past 999', () => {
    expect(computeNextSessionName('app', [{ name: 'app_999' }])).toBe('app_1000');
  });

  it('escapes regex-special characters in the app name', () => {
    const results = [{ name: 'a.b_005' }, { name: 'axb_009' }];
    expect(computeNextSessionName('a.b', results)).toBe('a.b_006');
  });

  it('falls back to _001 when app name is missing', () => {
    expect(computeNextSessionName('', [])).toBe('_001');
  });
});
```

- [ ] **Step 4: Run the test to verify it fails**

Run: `cd spring-boot-admin && npm test`
Expected: FAIL — cannot resolve `./sessionName.js` / `computeNextSessionName is not a function`.

- [ ] **Step 5: Implement the generator**

Create `spring-boot-admin/src/main/vue/sessionName.js`:

```js
function escapeRegExp(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

export function computeNextSessionName(appName, results) {
  const name = appName || '';
  const pattern = new RegExp('^' + escapeRegExp(name) + '_(\\d+)$');
  let max = 0;
  for (const result of results || []) {
    const match = pattern.exec(result.name);
    if (match) {
      const value = parseInt(match[1], 10);
      if (value > max) {
        max = value;
      }
    }
  }
  const next = String(max + 1).padStart(3, '0');
  return name + '_' + next;
}
```

- [ ] **Step 6: Run the test to verify it passes**

Run: `cd spring-boot-admin && npm test`
Expected: PASS — all 6 tests green.

- [ ] **Step 7: Commit**

```bash
git add spring-boot-admin/src/main/vue/sessionName.js spring-boot-admin/src/main/vue/sessionName.test.js spring-boot-admin/package.json spring-boot-admin/package-lock.json
git commit -m "feat(admin): add auto-generated session name helper"
```

---

### Task 2: Wire the generator into jklee.vue

**Files:**
- Modify: `spring-boot-admin/src/main/vue/jklee.vue`

**Interfaces:**
- Consumes: `computeNextSessionName(appName, results)` from `./sessionName.js`

- [ ] **Step 1: Import the helper and drop the hardcoded default**

In `jklee.vue`, at the top of `<script>` add:

```js
import { computeNextSessionName } from './sessionName.js';
```

In `data()`, change the `profileRequest.sessionName` default from `'test'` to `''`:

```js
    profileRequest: {
      sessionName: '',
      rawArguments: 'start,event=itimer,interval=1ms',
      duration: '2s',
      format: 'FLAMEGRAPH',
    },
```

- [ ] **Step 2: Add a method to refresh the auto name**

In `methods`, add:

```js
    refreshSessionName() {
      const appName = this.instance.registration
        ? this.instance.registration.name
        : '';
      this.profileRequest.sessionName = computeNextSessionName(appName, this.results);
    },
```

- [ ] **Step 3: Call it after results are loaded/refreshed**

At the end of `updateResultsList()`'s `try` block, after `this.results = response.data.results;`, add:

```js
        this.refreshSessionName();
```

- [ ] **Step 4: Build the frontend to verify it compiles**

Run: `cd spring-boot-admin && npm run build`
Expected: Build succeeds, no errors.

- [ ] **Step 5: Commit**

```bash
git add spring-boot-admin/src/main/vue/jklee.vue
git commit -m "feat(admin): pre-fill session name with auto-generated value"
```

---

### Task 3: Manual verification with the sample app

**Files:** none (verification only)

- [ ] **Step 1: Build the whole project**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 2: Manual smoke test**

Run the sample app + Spring Boot Admin, open the Jklee panel, and confirm:
- On a clean results dir the Session Name shows `<app_name>_001`.
- After a profiling run, the field advances to the next number.
- A custom-named report (e.g. `test1`) does not affect numbering.
