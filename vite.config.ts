import path from "path";
import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, ".", "");

  return {
    // 1. Убедись, что название репозитория в точности совпадает
    base: "/aaaaaaaaaaaaaaaaaaa/", 

    plugins: [react()],

    server: {
      port: 5000,
      host: "0.0.0.0",
      allowedHosts: true,
    },

    // 2. Настройки сборки (чтобы CSS не терялся)
    build: {
      outDir: "dist",
      assetsDir: "assets",
      emptyOutDir: true,
    },

    define: {
      "process.env.API_KEY": JSON.stringify(env.GEMINI_API_KEY),
      "process.env.GEMINI_API_KEY": JSON.stringify(env.GEMINI_API_KEY),
    },

    resolve: {
      alias: {
        // Исправил алиас: теперь @ указывает прямо на папку src
        // Это стандарт для React-проектов, так импорты не будут ломаться
        "@": path.resolve(__dirname, "./src"),
      },
    },
  };
});
