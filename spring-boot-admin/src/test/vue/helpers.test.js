import { describe, it, expect } from 'vitest';
import { computeNextSessionName, formatEndedAt, parseToMillis } from '../../main/vue/helpers.js';

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

describe('formatEndedAt', () => {
  it('returns empty string for falsy input', () => {
    expect(formatEndedAt('')).toBe('');
    expect(formatEndedAt(null)).toBe('');
    expect(formatEndedAt(undefined)).toBe('');
  });

  it('returns the raw value when it is not a valid date', () => {
    expect(formatEndedAt('not-a-date')).toBe('not-a-date');
  });

  it('shows only the time for today', () => {
    const now = new Date();
    now.setHours(9, 5, 7, 0);
    expect(formatEndedAt(now.getTime())).toBe('09:05:07');
  });

  it('shows date and time for other days', () => {
    const date = new Date(2020, 0, 2, 3, 4, 5);
    expect(formatEndedAt(date.getTime())).toBe('2020-01-02 03:04:05');
  });
});

describe('parseToMillis', () => {
  it('returns 0 for falsy or non-numeric input', () => {
    expect(parseToMillis('')).toBe(0);
    expect(parseToMillis('abc')).toBe(0);
  });

  it('parses seconds, minutes, hours, and days', () => {
    expect(parseToMillis('2s')).toBe(2000);
    expect(parseToMillis('3m')).toBe(180000);
    expect(parseToMillis('1h')).toBe(3600000);
    expect(parseToMillis('1d')).toBe(86400000);
  });

  it('assumes milliseconds without a unit', () => {
    expect(parseToMillis('500')).toBe(500);
  });
});
