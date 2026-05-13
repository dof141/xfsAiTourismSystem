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
    title: '手机号登录',
    editable: true,
    placeholderText: '请输入11位手机号',
    success: async (res) => {
      if (res.confirm && res.content) {
        const phone = res.content.trim()
        if (!/^1[3-9]\d{9}$/.test(phone)) {
          uni.showToast({ title: '请输入正确的11位手机号', icon: 'none' })
          return
        }
        try {
          const sendMsg = await api.touristSendCode(phone)
          uni.showModal({
            title: '输入验证码',
            content: sendMsg,
            editable: true,
            placeholderText: '请输入6位验证码',
            success: async (codeRes) => {
              if (codeRes.confirm && codeRes.content) {
                const code = codeRes.content.trim()
                if (!/^\d{6}$/.test(code)) {
                  uni.showToast({ title: '请输入6位验证码', icon: 'none' })
                  return
                }
                try {
                  const loginRes = await api.touristLogin(phone, code)
                  uni.setStorageSync('xfs_token', loginRes.token)
                  uni.setStorageSync('tourist_info', JSON.stringify(loginRes))
                  uni.showToast({ title: '登录成功' })
                  onSuccess && onSuccess()
                } catch (e) {
                  uni.showToast({ title: '验证码错误或已过期', icon: 'none' })
                }
              }
            }
          })
        } catch (e) {
          uni.showToast({ title: '验证码发送失败，请重试', icon: 'none' })
        }
      }
    }
  })
}
