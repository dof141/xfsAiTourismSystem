<template>
	<view class="container">
	<view class="header" style="display: flex; justify-content: space-between; align-items: center;">
	      <text class="title">🏔️ 雪峰山智慧导览</text>
	      <text style="color: #409eff; font-size: 28rpx;" @click="goToMyReserve">我的预约</text>
	    </view>

		<view class="hot-section" v-if="hotList.length > 0">
			<view class="section-title">🔥 本周热门推荐</view>
			<scroll-view class="hot-scroll" scroll-x="true">
				<view class="hot-card" v-for="(item, index) in hotList" :key="item.id" @click="goToDetail(item)">
					<view :class="['rank-badge', 'rank-' + (index + 1)]">TOP {{ index + 1 }}</view>
					<view class="hot-name">{{ item.name }}</view>
					<view class="hot-data">👀 {{ item.viewCount }} 人浏览</view>
				</view>
			</scroll-view>
		</view>

		<view class="area-list">
			<view class="section-title">📍 全部景区</view>
			<view class="area-card" v-for="item in areaList" :key="item.id" @click="goToDetail(item)">
				<view class="area-name">{{ item.name }}</view>
				<view class="area-tags">
					<text class="tag">{{ item.level }}景区</text>
					<text class="tag location">{{ item.address }}</text>
				</view>
				<view class="area-intro">{{ item.intro }}</view>
				<view class="click-hint">查看详情 ➔</view>
			</view>
		</view>

		<!-- 新的全局 AI 助手组件 -->
		<AiAssistant />
	</view>
</template>

<script setup>
	import { ref } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'
	import { api } from '../../api/request.js'
	import AiAssistant from '@/components/AiAssistant.vue'

	const areaList = ref([])
	const hotList = ref([])

	const fetchAreaList = async () => {
		try { areaList.value = await api.areaList() } catch (e) {}
	}

	const goToMyReserve = () => {
	  if (!uni.getStorageSync('xfs_token')) {
	    return showLoginModal();
	  }
	  uni.navigateTo({ url: '/pages/reserve/my-reserve/my-reserve' })
	}

	const showLoginModal = () => {
	  uni.showModal({
	    title: '需要登录',
	    content: '为了保障预约安全，请先进行极简登录',
	    confirmText: '去登录',
	    success: (res) => {
	      if (res.confirm) {
	        // 这里我们弹出一个输入框模拟登录，或者跳转专门的登录页
	        // 为了简单，我们先用弹窗模拟手机号录入
	        promptLogin();
	      }
	    }
	  });
	}

	const promptLogin = () => {
	  uni.showModal({
	    title: '极简登录',
	    editable: true,
	    placeholderText: '请输入11位手机号',
	    success: async (res) => {
	      if (res.confirm && res.content) {
	        try {
	          const loginRes = await api.touristLogin(res.content);
	          uni.setStorageSync('xfs_token', loginRes.token);
	          uni.setStorageSync('tourist_info', JSON.stringify(loginRes));
	          uni.showToast({ title: '登录成功' });
	        } catch (e) {}
	      }
	    }
	  });
	}

	const fetchHotList = async () => {
		try { hotList.value = await api.hotList() } catch (e) {}
	}

	const goToDetail = (item) => {
		const itemData = encodeURIComponent(JSON.stringify(item))
		uni.navigateTo({
			url: `/pages/detail/detail?data=${itemData}`
		})
	}

	onLoad(() => {
		fetchAreaList()
		fetchHotList()
	})
</script>

<style>
	page {
		background-color: #f5f7fa;
	}

	.container {
		padding: 20rpx;
		padding-bottom: 50rpx;
	}

	.header {
		margin-bottom: 30rpx;
		text-align: center;
	}

	.title {
		font-size: 38rpx;
		font-weight: bold;
		color: #333;
	}

	.section-title {
		font-size: 34rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		margin-top: 20rpx;
	}

	.hot-section {
		margin-bottom: 40rpx;
	}

	.hot-scroll {
		white-space: nowrap;
		width: 100%;
	}

	.hot-card {
		display: inline-block;
		  vertical-align: top;
		  width: 290rpx;
		  height: 180rpx;
		  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
		  border-radius: 16rpx;
		  margin-right: 20rpx;
		  padding: 30rpx 20rpx;
		  box-sizing: border-box;
		  position: relative;
		  box-shadow: 0 4rpx 12rpx rgba(255, 154, 158, 0.3);
	}

	.hot-name {
		font-size: 30rpx;
	  font-weight: bold;
	  color: #fff;
	  margin-top: 16rpx;
	  white-space: normal;
	  line-height: 1.4;
	  display: -webkit-box;
	  -webkit-box-orient: vertical;
	  -webkit-line-clamp: 2;
	  overflow: hidden;
	  text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1);
	}

	.hot-data {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.9);
		margin-top: 10rpx;
	}

	.rank-badge {
		position: absolute;
		top: 0;
		right: 0;
		font-size: 20rpx;
		color: #fff;
		padding: 4rpx 12rpx;
		border-radius: 0 16rpx 0 16rpx;
		font-weight: bold;
	}

	.rank-1 { background-color: #f56c6c; }
	.rank-2 { background-color: #e6a23c; }
	.rank-3 { background-color: #409eff; }

	.area-card {
		background-color: #ffffff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		position: relative;
	}

	.area-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #2c3e50;
		margin-bottom: 16rpx;
	}

	.area-tags {
		display: flex;
		margin-bottom: 20rpx;
	}

	.tag {
		font-size: 24rpx;
		background-color: #e1f3d8;
		color: #67c23a;
		padding: 4rpx 16rpx;
		border-radius: 8rpx;
		margin-right: 16rpx;
	}

	.tag.location {
		background-color: #ecf5ff;
		color: #409eff;
	}

	.area-intro {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		margin-bottom: 30rpx;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
	}

	.click-hint {
		position: absolute;
		right: 30rpx;
		bottom: 20rpx;
		font-size: 24rpx;
		color: #909399;
	}
</style>
