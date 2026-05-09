import { api } from '@/api/request.js'

/**
 * 检查是否已登录
 */
export function isLoggedIn() {
  return !!uni.getStorageSync('xfs_token')
}

/**
 * 确保已登录，未登录则弹窗引导登录
 * @param {Function} onReady - 登录成功后的回调
 * @param {Object} options - 可选配置
 * @param {string} options.title - 弹窗标题
 * @param {string} options.content - 弹窗提示内容
 */
export function ensureLogin(onReady, options = {}) {
  if (isLoggedIn()) {
    onReady && onReady()
    return
  }

  const {
    title = '温馨提示',
    content = '请先进行极简登录'
  } = options

  uni.showModal({
    title,
    content,
    confirmText: '去登录',
    success: (res) => {
      if (res.confirm) {
        promptLogin(onReady)
      }
    }
  })
}

/**
 * 弹出手机号输入框并执行登录
 * @param {Function} onSuccess - 登录成功后的回调
 */
function promptLogin(onSuccess) {
  uni.showModal({
    title: '极简登录',
    editable: true,
    placeholderText: '请输入11位手机号',
    success: async (res) => {
      if (res.confirm && res.content) {
        try {
          const loginRes = await api.touristLogin(res.content)
          uni.setStorageSync('xfs_token', loginRes.token)
          uni.setStorageSync('tourist_info', JSON.stringify(loginRes))
          uni.showToast({ title: '登录成功' })
          onSuccess && onSuccess()
        } catch (e) {
          uni.showToast({ title: '登录失败，请重试', icon: 'none' })
        }
      }
    }
  })
}
