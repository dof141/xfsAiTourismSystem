import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue'),
        meta: { title: '管理员登录' }
    },
    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('../views/Dashboard.vue'),
                meta: { title: '数据统计大屏' }
            },
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
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/dashboard'
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('xfs_token')
    if (to.path === '/login') {
        if (token) {
            next('/dashboard')
        } else {
            next()
        }
    } else {
        if (!token) {
            next('/login')
        } else {
            next()
        }
    }
})

export default router
