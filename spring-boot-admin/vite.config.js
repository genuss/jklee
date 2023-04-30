import vue from "@vitejs/plugin-vue";
import path from "path";
import {defineConfig} from "vite";
import {viteStaticCopy} from "vite-plugin-static-copy";

// noinspection JSUnusedGlobalSymbols
export default defineConfig({
  plugins: [
    vue(),
    viteStaticCopy({
      targets: [
        {
          src: "src/main/vue/routes.txt",
          dest: "./",
        },
      ],
    }),
  ],
  build: {
    target: "es2015",
    sourcemap: true,
    minify: false,
    outDir: "src/main/resources/META-INF/spring-boot-admin-server-ui/extensions/jklee",
    lib: {
      entry: path.resolve(__dirname, "src/main/vue/index.js"),
      name: "jklee",
      formats: ["umd"],
      fileName: () => "jklee.js",
    },
    define: {
      __VUE_PROD_DEVTOOLS__: true,
    },
    rollupOptions: {
      external: ["vue"],
      output: {
        globals: {
          vue: "Vue",
        },
      },
    },
  },
});
