const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  devServer: {
    port: 5173,
    proxy: {
      '/auth': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      '/equipment': {
        target: 'http://localhost:8081', 
        changeOrigin: true
      },
      '/request': {
        target: 'http://localhost:8081', 
        changeOrigin: true
      },
      '/reports': {
        target: 'http://localhost:8081', 
        changeOrigin: true
      }
    }
  }
})