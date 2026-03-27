const { defineConfig } = require('@vue/cli-service')

// Automatically switch between Docker's API Gateway and Localhost
const apiTarget = process.env.VUE_APP_API_TARGET || 'http://localhost:8080';

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  pages: {
    index: {
      entry: 'src/main.js',
      title: 'INVTR',
    },
  },
  devServer: {
    port: 5173,
    proxy: {
      '/auth': {
        target: apiTarget,
        changeOrigin: true
      },
      '/equipment': {
        target: apiTarget,
        changeOrigin: true
      },
      '/requests': {
        target: apiTarget,
        changeOrigin: true
      },
      '/reports': {
        target: apiTarget,
        changeOrigin: true
      }
    }
  }
})