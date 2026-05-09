<template>
  <view class="reserve-container">
    <view class="info-card">
      <text class="label">当前预约景区</text>
      <text class="value">{{ areaName }}</text>
    </view>

    <view class="form-group">
      <view class="section-title"><text class="dot"></text> 1. 选择游玩日期</view>
      <picker mode="date" :value="selectedDate" :start="startDate" @change="onDateChange">
        <view class="picker-box">
          <text :class="selectedDate ? 'date-text' : 'placeholder'">
            {{ selectedDate || '请点击选择游玩日期' }}
          </text>
         <text class="arrow"> ❯ </text>
        </view>
      </picker>
    </view>

    <view class="form-group">
      <view class="section-title"><text class="dot"></text> 2. 选择入园时段</view>
      <view class="slot-grid" v-if="slotList.length > 0">
        <view
          v-for="item in slotList" :key="item.id"
          :class="['slot-item', selectedSlotId === item.id ? 'active' : '']"
          @click="selectedSlotId = item.id"
        >
          <text class="time">{{ item.slotName }}</text>
          <text class="status">余量充足</text>
        </view>
      </view>
      <view v-else class="empty-tip">
        正在加载时段数据，若长时间无反应，请检查 Java 后端是否启动并报错。
      </view>
    </view>

    <view class="bottom-bar">
      <button class="submit-btn" @click="submitReserve">确认提交预约</button>
    </view>
	<view class="qr-mask" v-if="showQrPopup">
	      <view class="qr-box">
	        <view class="qr-title">✅ 预约成功</view>
	        <view class="qr-tips">请在入园时向工作人员出示此码</view>
	        <image class="qr-img" :src="qrCodeUrl" mode="aspectFit"></image>
	        <button class="qr-close-btn" @click="closeQrPopup">我知道了</button>
	      </view>
	    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { api } from '../../api/request.js'

const areaId = ref(null)
const showQrPopup = ref(false)
const qrCodeUrl = ref('')

const closeQrPopup = () => {
  showQrPopup.value = false
  uni.navigateBack()
}
const areaName = ref('')
const selectedDate = ref('')
const selectedSlotId = ref(null)
const slotList = ref([])
const startDate = new Date().toISOString().split('T')[0]

onLoad((options) => {
  areaId.value = options.id
  areaName.value = options.name
  fetchSlots()
})

const fetchSlots = async () => {
  try {
    slotList.value = await api.slotList()
  } catch (e) {
    uni.showToast({ title: '网络错误，请检查后端', icon: 'none' })
  }
}

const onDateChange = (e) => {
  selectedDate.value = e.detail.value
}

const submitReserve = async () => {
  if (!selectedDate.value) {
    return uni.showToast({ title: '请先选择游玩日期', icon: 'none' })
  }
  if (!selectedSlotId.value) {
    return uni.showToast({ title: '请选择入园时段', icon: 'none' })
  }

  uni.showLoading({ title: '正在提交...' })
  try {
    const base64Data = await api.addReserve({
      areaId: areaId.value,
      reserveDate: selectedDate.value,
      slotId: selectedSlotId.value
    })
    qrCodeUrl.value = base64Data
    showQrPopup.value = true
  } catch (e) {
    uni.showModal({ title: '预约失败', content: e.msg || '请稍后重试', showCancel: false })
  } finally {
    uni.hideLoading()
  }
}
</script>

<style>
page {
  background-color: #f5f7fa;
}
.reserve-container {
  padding: 30rpx;
  padding-bottom: 150rpx;
}

.info-card {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 20rpx;
  padding: 40rpx;
  margin-bottom: 40rpx;
  box-shadow: 0 8rpx 16rpx rgba(79, 172, 254, 0.3);
}
.info-card .label {
  display: block;
  font-size: 26rpx;
  color: rgba(255,255,255,0.8);
  margin-bottom: 10rpx;
}
.info-card .value {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  color: #ffffff;
}

.form-group {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.03);
}
.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
  display: flex;
  align-items: center;
}
.dot {
  width: 12rpx;
  height: 12rpx;
  background-color: #409eff;
  border-radius: 50%;
  margin-right: 16rpx;
}

.picker-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f8f9fa;
  padding: 24rpx 30rpx;
  border-radius: 12rpx;
  border: 1px solid #ebeef5;
}
.placeholder { color: #999; font-size: 28rpx; }
.date-text { color: #333; font-size: 30rpx; font-weight: bold; }
.arrow { color: #ccc; }

.slot-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
.slot-item {
  width: 48%;
  background-color: #f8f9fa;
  border: 2px solid transparent;
  border-radius: 12rpx;
  padding: 24rpx 0;
  margin-bottom: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: all 0.3s;
}
.slot-item .time {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 8rpx;
}
.slot-item .status {
  font-size: 22rpx;
  color: #67c23a;
}
.slot-item.active {
  background-color: #ecf5ff;
  border-color: #409eff;
}
.slot-item.active .time, .slot-item.active .status {
  color: #409eff;
}

.empty-tip {
  font-size: 26rpx;
  color: #f56c6c;
  text-align: center;
  padding: 20rpx 0;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #ffffff;
  padding: 20rpx 40rpx 40rpx;
  box-sizing: border-box;
  box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.05);
  z-index: 99;
}
.submit-btn {
  background-color: #409eff;
  color: #ffffff;
  border-radius: 50rpx;
  font-size: 32rpx;
  font-weight: bold;
  line-height: 88rpx;
}
.qr-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.qr-box {
  width: 600rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 50rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.2);
}

.qr-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #67c23a;
  margin-bottom: 20rpx;
}

.qr-tips {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 40rpx;
}

.qr-img {
  width: 400rpx;
  height: 400rpx;
  margin-bottom: 50rpx;
  border: 1px solid #eee;
  padding: 10rpx;
}

.qr-close-btn {
  width: 100%;
  background-color: #409eff;
  color: #fff;
  border-radius: 40rpx;
  font-size: 32rpx;
  line-height: 80rpx;
}
</style>
