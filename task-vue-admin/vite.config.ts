import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite';
import { ArcoResolver } from 'unplugin-vue-components/resolvers';
import { vitePluginForArco } from '@arco-plugins/vite-vue'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    css: {
        postcss: {
            plugins: [require('tailwindcss')]
        }
    },
    plugins: [
        vue(),
        AutoImport({
            resolvers: [ArcoResolver()]
        }),
        Components({
            resolvers: [
                ArcoResolver({
                    sideEffect: false
                })
            ]
        }),
        vitePluginForArco({
            style: 'css'
        })
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:5400', //接口域名
                changeOrigin: true,             //是否跨域
                rewrite: (path) => path.replace(/^\/api/, ''),
            }
        }
    }
})