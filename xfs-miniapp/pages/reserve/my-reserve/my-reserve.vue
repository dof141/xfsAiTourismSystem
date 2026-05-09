<template>
  <view class="page-container">
    <view class="header">我的预约门票</view>

    <view class="list-container" v-if="recordList.length > 0">
      <view class="ticket-card" v-for="item in recordList" :key="item.id">
        <view class="ticket-top">
          <text class="date">游玩日期：{{ item.reserveDate }}</text>
          <text :class="['status', item.status === 1 ? 'status-pending' : 'status-used']">
            {{ item.status === 1 ? '待入园' : '已核销' }}
          </text>
        </view>
        <view class="ticket-mid">
         <view class="info">
           订单号：<text selectable>{{ item.orderNo }}</text>
         </view>
         <view class="info">
           核销短码：<text selectable>{{ item.verifyCode }}</text>
         </view>
        </view>
        <view class="ticket-bottom" v-if="item.status === 1">
          <button class="show-qr-btn" @click="fetchAndShowQr(item.orderNo)">出示入园码</button>
        </view>
      </view>
    </view>

    <view class="empty-tip" v-else>
      暂无预约记录，快去首页逛逛吧~
    </view>

    <view class="qr-mask" v-if="showQrPopup">
      <view class="qr-box">
        <view class="qr-title">请向工作人员出示此码</view>
        <image class="qr-img" :src="qrCodeUrl" mode="aspectFit"></image>
        <button class="qr-close-btn" @click="closeQrPopup">关闭</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { api } from '../../../api/request.js'

const recordList = ref([])
const showQrPopup = ref(false)
const qrCodeUrl = ref('')

const closeQrPopup = () => {
  showQrPopup.value = false
  fetchMyList()
}

const fetchMyList = async (options = {}) => {
  const { showSuccessToast = false } = options

  if (recordList.value.length === 0) {
    const cache = uni.getStorageSync('my_reserve_cache')
    if (cache) {
      try { recordList.value = JSON.parse(cache) } catch (e) {}
    }
  }

  try {
    const list = await api.myReserveList()
    if (list && list.length > 0) {
      recordList.value = list
      uni.setStorageSync('my_reserve_cache', JSON.stringify(list))
    } else {
      if (recordList.value.length === 0) {
        uni.showToast({ title: '暂无预约记录', icon: 'none' })
      }
    }
    if (showSuccessToast && list && list.length > 0) {
      uni.showToast({ title: '已更新', icon: 'success', duration: 1000 })
    }
  } catch (e) {
    uni.showToast({ title: '网络异常，请重试', icon: 'none' })
  }
}

const fetchAndShowQr = async (orderNo) => {
  uni.showLoading({ title: '生成中...' })
  try {
    qrCodeUrl.value = await api.getQrCode(orderNo)
    showQrPopup.value = true
  } catch (e) {
    uni.showToast({ title: '获取失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

onShow(() => {
  fetchMyList()
})

onPullDownRefresh(() => {
  fetchMyList({ showSuccessToast: true }).finally(() => {
    uni.stopPullDownRefresh()
  })
})
</script>

<style>
page { background-color: #f5f7fa; }
.page-container { padding: 30rpx; }
.header { font-size: 36rpx; font-weight: bold; color: #333; margin-bottom: 30rpx; }

.ticket-card {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.05);
  position: relative;
}
.ticket-top { display: flex; justify-content: space-between; border-bottom: 1px dashed #eee; padding-bottom: 20rpx; margin-bottom: 20rpx; }
.date { font-size: 30rpx; font-weight: bold; color: #333; }
.status { font-size: 26rpx; font-weight: bold; }
.status-pending { color: #e6a23c; }
.status-used { color: #909399; }
.ticket-mid { margin-bottom: 20rpx; }
.info { font-size: 26rpx; color: #666; margin-bottom: 10rpx; }
.ticket-bottom { text-align: right; }
.show-qr-btn { display: inline-block; background-color: #409eff; color: #fff; font-size: 26rpx; border-radius: 30rpx; padding: 0 40rpx; line-height: 60rpx; margin: 0; }

.empty-tip { text-align: center; color: #999; margin-top: 100rpx; font-size: 28rpx; }

.qr-mask { position: fixed; top: 0; left: 0; width: 100%; height: 100vh; background-color: rgba(0, 0, 0, 0.6); display: flex; justify-content: center; align-items: center; z-index: 9999; }
.qr-box { width: 600rpx; background-color: #ffffff; border-radius: 24rpx; padding: 50rpx 40rpx; display: flex; flex-direction: column; align-items: center; }
.qr-title { font-size: 32rpx; font-weight: bold; color: #333; margin-bottom: 40rpx; }
.qr-img { width: 400rpx; height: 400rpx; margin-bottom: 50rpx; border: 1px solid #eee; padding: 10rpx; }
.qr-close-btn { width: 100%; background-color: #f56c6c; color: #fff; border-radius: 40rpx; font-size: 32rpx; line-height: 80rpx; }
</style>
