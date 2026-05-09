// 统一请求封装

// 后端地址：开发环境用 localhost，生产环境用正式域名
const BASE_URL = process.env.NODE_ENV === 'development'
  ? 'http://localhost:8080'
  : 'https://your-server.com'

/**
 * 封装 uni.request，统一处理 loading、错误提示
 */
export function request(method, url, data = {}, options = {}) {
  const {
    showLoading = false,
    loadingText = '加载中...',
    showError = true,
    timeout,
  } = options

  return new Promise((resolve, reject) => {
    if (showLoading) {
      uni.showLoading({ title: loadingText, mask: true })
    }

    uni.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      header: {
        'Authorization': 'Bearer ' + (uni.getStorageSync('xfs_token') || '')
      },
      timeout: options.timeout || 15000,
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data.data)
        } else if (res.statusCode === 401) {
            uni.removeStorageSync('xfs_token')
            uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
            reject(res.data)
        } else {
          if (showError) {
            uni.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
          }
          reject(res.data)
        }
      },
      fail: (err) => {
        if (showError) {
          uni.showToast({ title: '网络异常', icon: 'none' })
        }
        reject(err)
      },
      complete: () => {
        if (showLoading) {
          uni.hideLoading()
        }
      }
    })
  })
}

// 快捷方法
export const get = (url, data, options) => request('GET', url, data, options)
export const post = (url, data, options) => request('POST', url, data, options)

// 接口列表
export const api = {
  // 景区
  areaList: () => get('/api/area/list'),
  hotList: () => get('/api/area/hot'),

  // 时段
  slotList: () => get('/api/slot/list'),

  // 预约
  addReserve: (data) => post('/api/reserve/add', data),
  myReserveList: () => get('/api/reserve/myList'),
  getQrCode: (orderNo) => get(`/api/reserve/qrcode/${orderNo}`),

  // AI（超时设为60秒，大模型响应较慢）
  aiChat: (text) => post('/api/ai/chat', { text }, { timeout: 60000 }),

  // 子景点
  spotListByArea: (areaId) => get(`/api/area/${areaId}/spots`),

  // 游客登录
  touristLogin: (phone) => post('/api/tourist/login', { phone }),
}
