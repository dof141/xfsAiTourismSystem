import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

// 请求拦截器：自动注入 Token
request.interceptors.request.use(config => {
    const token = localStorage.getItem('xfs_token')
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
}, error => {
    return Promise.reject(error)
})

// 响应拦截器：处理 401 登录失效
request.interceptors.response.use(response => {
    return response.data
}, error => {
    if (error.response && error.response.status === 401) {
        ElMessage.error('登录已失效，请重新登录')
        localStorage.removeItem('xfs_token')
        router.push('/login')
    } else {
        ElMessage.error(error.message || '网络请求失败')
    }
    return Promise.reject(error)
})

export default request
