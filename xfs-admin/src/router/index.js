import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
    {
        path: '/',
        component: Layout,
        redirect: '/area',
        children: [
            {
                path: 'area',
                name: 'Area',
                component: () => import('../views/AreaList.vue'),
                meta: { title: '景区信息管理' }
            },
            {
                path: 'ai',
                name: 'AiChat',
                component: () => import('../views/AiChat.vue'),
                meta: { title: 'AI 智能文旅助手' }
            },{
                path: 'verify',
                name: 'Verify',
                component: () => import('../views/Verify.vue'),
                meta: { title: '二维码核销' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router