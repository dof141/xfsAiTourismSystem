import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router' // 引入我们刚才写的路由

const app = createApp(App)

app.use(ElementPlus)
app.use(router) // 使用路由
app.mount('#app')