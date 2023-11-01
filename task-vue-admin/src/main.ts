import './assets/css/main.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { Message } from '@arco-design/web-vue';
import { Notification } from '@arco-design/web-vue';

import App from './App.vue'
import router from './router'

const app = createApp(App);

Message._context = app._context;
Notification._context = app._context;

app.use(createPinia());
app.use(router);

app.mount('#app');
