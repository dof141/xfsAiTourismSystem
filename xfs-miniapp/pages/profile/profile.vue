<template>
  <view class="profile-container">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="avatar-area">
        <view class="avatar">🏔️</view>
        <view class="user-info">
          <text class="nickname">{{ nickname }}</text>
          <text class="phone">{{ phone }}</text>
        </view>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="menu-list">
      <view class="menu-item" @click="goToMyReserve">
        <text class="menu-icon">🎫</text>
        <text class="menu-label">我的预约</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goToAi">
        <text class="menu-icon">🤖</text>
        <text class="menu-label">AI 智能导览</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn" @click="handleLogout">退出登录</view>

    <!-- 版本信息 -->
    <view class="version">雪峰山智慧导览 v1.0</view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const nickname = ref('未登录')
const phone = ref('')

const loadUserInfo = () => {
  try {
    const info = JSON.parse(uni.getStorageSync('tourist_info') || '{}')
    nickname.value = info.nickname || '雪峰游客'
    phone.value = info.phone ? info.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '未绑定手机'
  } catch (e) {
    nickname.value = '雪峰游客'
    phone.value = ''
  }
}

const goToMyReserve = () => {
  uni.navigateTo({ url: '/pages/reserve/my-reserve/my-reserve' })
}

const goToAi = () => {
  uni.navigateTo({ url: '/pages/ai/ai' })
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        // 清除用户专属缓存
        try {
          const info = JSON.parse(uni.getStorageSync('tourist_info') || '{}')
          uni.removeStorageSync('my_reserve_cache_' + (info.touristId || 'guest'))
        } catch {}
        uni.removeStorageSync('xfs_token')
        uni.removeStorageSync('tourist_info')
        uni.showToast({ title: '已退出', icon: 'success' })
        setTimeout(() => {
          uni.reLaunch({ url: '/pages/index/index' })
        }, 1000)
      }
    }
  })
}

onShow(() => {
  loadUserInfo()
})
</script>

<style>
page {
  background-color: #f5f7fa;
}

.profile-container {
  padding: 30rpx;
}

.user-card {
  background: linear-gradient(135deg, #6366f1, #0ea5e9);
  border-radius: 24rpx;
  padding: 50rpx 40rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 8rpx 24rpx rgba(99, 102, 241, 0.3);
}

.avatar-area {
  display: flex;
  align-items: center;
}

.avatar {
  width: 100rpx;
  height: 100rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  margin-right: 30rpx;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.nickname {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8rpx;
}

.phone {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.menu-list {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.03);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 36rpx 30rpx;
  border-bottom: 1px solid #f5f7fa;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 24rpx;
}

.menu-label {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.menu-arrow {
  font-size: 36rpx;
  color: #ccc;
}

.logout-btn {
  background: #fff;
  border-radius: 20rpx;
  text-align: center;
  padding: 30rpx;
  font-size: 30rpx;
  color: #f56c6c;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.03);
  margin-bottom: 30rpx;
}

.logout-btn:active {
  background: #fef2f2;
}

.version {
  text-align: center;
  font-size: 24rpx;
  color: #ccc;
  margin-top: 40rpx;
}
</style>
