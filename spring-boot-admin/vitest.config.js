import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    include: ['src/test/vue/**/*.test.js'],
  },
});
