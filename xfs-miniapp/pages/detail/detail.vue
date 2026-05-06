<template>
  <view class="detail-container">
    <view class="cover-image">
      <text class="cover-text">{{ areaData.name }}</text>
    </view>

    <view class="content-box">
      <view class="header-info">
        <text class="title">{{ areaData.name }}</text>
        <view class="tags-row">
          <text class="tag level">{{ areaData.level }}景区</text>
          <text class="tag location">{{ areaData.address }}</text>
        </view>
      </view>

      <view class="section">
        <view class="section-title">景区介绍</view>
        <view class="section-content">{{ areaData.intro }}</view>
      </view>

      <view class="section">
        <view class="section-title">基础信息</view>
        <view class="info-row">
          <text class="label">咨询电话：</text>
          <text class="value">{{ areaData.tel }}</text>
        </view>
        <view class="info-row">
          <text class="label">入园时间：</text>
          <text class="value">08:00 - 17:30</text>
        </view>
      </view>
    </view>

    <view class="bottom-bar">
      <button class="reserve-btn" @click="handleReserve">立即预约</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

// 用于存放当前景区的数据
const areaData = ref({})

// 页面加载时，解析首页传过来的数据
onLoad((options) => {
  if (options.data) {
    // 解码并转换为对象
    areaData.value = JSON.parse(decodeURIComponent(options.data))
    
    // 动态设置当前页面的标题栏
    uni.setNavigationBarTitle({
      title: areaData.value.name
    })
  }
})

// 点击预约按钮的事件
const handleReserve = () => {
  uni.navigateTo({
    url: `/pages/reserve/reserve?id=${areaData.value.id}&name=${areaData.value.name}`
  })
}
  // 提示：下一步我们将在这里跳转到真正的【选择日期和时段】的页面！

</script>

<style>
/* 整个页面加个灰色背景 */
page {
  background-color: #f5f7fa;
}

.detail-container {
  /* 底部留出空间，防止被吸底按钮挡住内容 */
  padding-bottom: 120rpx; 
}

/* 模拟封面图 */
.cover-image {
  width: 100%;
  height: 400rpx;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}
.cover-text {
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
  letter-spacing: 4rpx;
  text-shadow: 0 4rpx 8rpx rgba(0,0,0,0.2);
}

/* 内容卡片区 */
.content-box {
  background-color: #fff;
  border-radius: 30rpx 30rpx 0 0;
  margin-top: -30rpx; /* 向上偏移一点，做出压在图片上的层叠感 */
  padding: 40rpx 30rpx;
  position: relative;
  z-index: 10;
}

.header-info {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 30rpx;
  margin-bottom: 30rpx;
}
.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}
.tags-row {
  display: flex;
}
.tag {
  font-size: 24rpx;
  padding: 6rpx 20rpx;
  border-radius: 30rpx;
  margin-right: 20rpx;
}
.tag.level { background-color: #e1f3d8; color: #67c23a; }
.tag.location { background-color: #ecf5ff; color: #409eff; }

/* 块级内容区 */
.section {
  margin-bottom: 40rpx;
}
.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  border-left: 8rpx solid #409eff;
  padding-left: 16rpx;
}
.section-content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
  text-align: justify;
}

/* 列表信息 */
.info-row {
  display: flex;
  margin-bottom: 16rpx;
  font-size: 28rpx;
}
.label { color: #999; width: 140rpx; }
.value { color: #333; flex: 1; }

/* 底部吸底按钮条 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 120rpx;
  background-color: #fff;
  box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 30rpx;
  box-sizing: border-box;
  z-index: 999;
}
.reserve-btn {
  width: 100%;
  background-color: #409eff;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 40rpx;
  line-height: 80rpx;
}
</style>