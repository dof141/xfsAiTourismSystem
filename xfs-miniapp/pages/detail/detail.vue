<template>
  <view class="detail-container">
    <!-- 1. 沉浸式顶部封面 -->
    <view class="hero-section">
      <image class="cover-img" :src="areaData.coverImg || '/static/logo.png'" mode="aspectFill"></image>
      <view class="hero-mask"></view>
      <view class="hero-content">
        <text class="area-level">{{ areaData.level }}景区</text>
        <text class="area-name">{{ areaData.name }}</text>
        <view class="address-box">
          <text class="location-icon">📍</text>
          <text>{{ areaData.address }}</text>
        </view>
      </view>
    </view>

    <view class="main-content">
      <!-- 2. 景区概况卡片 -->
      <view class="info-card">
        <view class="section-title">景区概况</view>
        <view class="intro-text">{{ areaData.intro }}</view>
        <view class="meta-row">
          <view class="meta-item">
            <text class="label">咨询电话</text>
            <text class="value">{{ areaData.tel }}</text>
          </view>
          <view class="meta-item">
            <text class="label">入园时间</text>
            <text class="value">08:00 - 17:30</text>
          </view>
        </view>
      </view>

      <!-- 3. 核心景点画廊 (重头戏) -->
      <view class="spots-section" v-if="spotList.length > 0">
        <view class="header-row">
          <text class="section-title">核心景点推荐</text>
          <text class="count-tag">{{ spotList.length }}个地点</text>
        </view>
        
        <scroll-view class="spots-scroll" scroll-x="true" enable-flex>
          <view class="spot-card" v-for="spot in spotList" :key="spot.id">
            <image class="spot-img" :src="spot.spotImg || '/static/logo.png'" mode="aspectFill"></image>
            <view class="spot-info">
              <text class="spot-name">{{ spot.name }}</text>
              <view class="spot-price-row">
                <text class="currency">¥</text>
                <text class="price">{{ spot.price }}</text>
                <text class="unit">/人</text>
              </view>
              <text class="spot-time">🕒 {{ spot.openTime }}</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 4. 游玩提示 -->
      <view class="tips-card">
        <view class="section-title">入园须知</view>
        <view class="tip-item">· 请提前通过本系统完成实名预约</view>
        <view class="tip-item">· 入园时请出示核销码进行扫描</view>
        <view class="tip-item">· 建议自备适量饮用水，保持景区环境整洁</view>
      </view>
    </view>

    <!-- 底部操作条 -->
    <view class="bottom-bar">
      <button class="reserve-btn" @click="handleReserve">立即预约门票</button>
    </view>
	
	<!-- 全局 AI 助手 -->
	<AiAssistant />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { api } from '@/api/request.js'
import { ensureLogin } from '@/utils/auth.js'
import AiAssistant from '@/components/AiAssistant.vue'

const areaData = ref({})
const spotList = ref([])

onLoad(async (options) => {
  if (options.data) {
    areaData.value = JSON.parse(decodeURIComponent(options.data))
    uni.setNavigationBarTitle({ title: areaData.value.name })
    
    // 获取子景点数据
    fetchSpots()
  }
})

const fetchSpots = async () => {
  try {
    const data = await api.spotListByArea(areaData.value.id)
    spotList.value = data
  } catch (e) { uni.showToast({ title: '景点信息加载失败', icon: 'none' }) }
}

const handleReserve = () => {
  ensureLogin(proceedToReserve, { title: '温馨提示', content: '预约门票需要先进行极简登录哦' })
}

const proceedToReserve = () => {
  uni.navigateTo({
    url: `/pages/reserve/reserve?id=${areaData.value.id}&name=${areaData.value.name}`
  })
}

</script>

<style lang="scss">
page {
  background-color: #f8fafc;
}

.detail-container {
  padding-bottom: 140rpx;
}

/* 沉浸式 Hero 区域 */
.hero-section {
  position: relative;
  width: 100%;
  height: 550rpx;
  overflow: hidden;
  
  .cover-img {
    width: 100%;
    height: 100%;
    transform: scale(1.1); /* 轻微放大，增加视觉张力 */
  }
  
  .hero-mask {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, rgba(0,0,0,0.2), rgba(0,0,0,0.7));
  }
  
  .hero-content {
    position: absolute;
    bottom: 60rpx;
    left: 40rpx;
    right: 40rpx;
    color: #fff;
    
    .area-level {
      font-size: 24rpx;
      background: rgba(255, 255, 255, 0.2);
      backdrop-filter: blur(10px);
      padding: 6rpx 20rpx;
      border-radius: 30rpx;
      margin-bottom: 16rpx;
      display: inline-block;
    }
    
    .area-name {
      font-size: 48rpx;
      font-weight: 800;
      margin-bottom: 12rpx;
      text-shadow: 0 4rpx 10rpx rgba(0,0,0,0.3);
    }
    
    .address-box {
      display: flex;
      align-items: center;
      font-size: 26rpx;
      opacity: 0.9;
      gap: 8rpx;

      .location-icon {
        font-size: 28rpx;
      }
    }
  }
}

.main-content {
  margin-top: -40rpx;
  position: relative;
  z-index: 10;
  padding: 0 30rpx;
}

/* 通用卡片样式 */
.info-card, .spots-section, .tips-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 40rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.03);
}

.section-title {
  font-size: 34rpx;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 24rpx;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: -8rpx;
    width: 40rpx;
    height: 6rpx;
    background: #6366f1;
    border-radius: 3rpx;
  }
}

.intro-text {
  font-size: 28rpx;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 30rpx;
  text-align: justify;
}

.meta-row {
  display: flex;
  border-top: 1px solid #f1f5f9;
  padding-top: 30rpx;
  gap: 40rpx;
  
  .meta-item {
    flex: 1;
    .label { display: block; font-size: 22rpx; color: #94a3b8; margin-bottom: 4rpx; }
    .value { font-size: 28rpx; color: #1e293b; font-weight: 600; }
  }
}

/* 景点画廊样式 */
.spots-section {
  padding-right: 0; /* 让滚动条贴边 */
  
  .header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-right: 40rpx;
    margin-bottom: 30rpx;
    
    .count-tag {
      font-size: 22rpx;
      color: #6366f1;
      background: #eef2ff;
      padding: 4rpx 16rpx;
      border-radius: 20rpx;
    }
  }
}

.spots-scroll {
  display: flex;
  flex-direction: row;
  white-space: nowrap;
  
  .spot-card {
    display: inline-block;
    width: 320rpx;
    margin-right: 24rpx;
    background: #f8fafc;
    border-radius: 24rpx;
    overflow: hidden;
    
    .spot-img {
      width: 100%;
      height: 220rpx;
    }
    
    .spot-info {
      padding: 20rpx;
      
      .spot-name {
        display: block;
        font-size: 28rpx;
        font-weight: 700;
        color: #1e293b;
        margin-bottom: 10rpx;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      
      .spot-price-row {
        margin-bottom: 8rpx;
        .currency { font-size: 20rpx; color: #ef4444; }
        .price { font-size: 32rpx; font-weight: 800; color: #ef4444; }
        .unit { font-size: 20rpx; color: #94a3b8; }
      }
      
      .spot-time {
        font-size: 20rpx;
        color: #94a3b8;
      }
    }
  }
}

.tip-item {
  font-size: 26rpx;
  color: #64748b;
  margin-bottom: 12rpx;
}

/* 底部栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 120rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  padding: 0 30rpx;
  padding-bottom: env(safe-area-inset-bottom);
  box-sizing: border-box;
  box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.05);
  z-index: 1000;
  
  .reserve-btn {
    flex: 1;
    height: 84rpx;
    background: linear-gradient(135deg, #6366f1, #0ea5e9);
    color: #fff;
    font-size: 30rpx;
    font-weight: 700;
    border-radius: 42rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    box-shadow: 0 8rpx 16rpx rgba(99, 102, 241, 0.3);
  }
}
</style>
