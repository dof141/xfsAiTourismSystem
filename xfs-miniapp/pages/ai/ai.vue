<template>
	<view class="container">
		<!-- 聊天内容区域 -->
		<scroll-view class="chat-list" scroll-y :scroll-into-view="lastMessageId" scroll-with-animation>
			<view class="message-item" v-for="(item, index) in messages" :key="index" :id="'msg-' + index">
				<!-- AI 消息 -->
				<view v-if="item.role === 'ai'" class="ai-msg">
					<image class="avatar" src="/static/logo.png" mode="aspectFill"></image>
					<view class="content">
						<text class="bubble">{{ item.content }}</text>
					</view>
				</view>
				<!-- 用户消息 -->
				<view v-else class="user-msg">
					<view class="content">
						<text class="bubble">{{ item.content }}</text>
					</view>
					<view class="avatar-user">我</view>
				</view>
			</view>
			
			<!-- 正在思考状态 -->
			<view v-if="loading" class="ai-msg" id="loading-msg">
				<image class="avatar" src="/static/logo.png" mode="aspectFill"></image>
				<view class="content">
					<view class="bubble loading-dots">雪峰百事通正在思考中...</view>
				</view>
			</view>
			
			<!-- 占位，防止被输入框遮挡 -->
			<view class="placeholder"></view>
		</scroll-view>

		<!-- 底部输入区域 -->
		<view class="input-section">
			<input 
				class="input-box" 
				v-model="inputText" 
				placeholder="想了解雪峰山什么？快来问我吧" 
				confirm-type="send"
				@confirm="send"
				:disabled="loading"
			/>
			<view class="send-btn" @tap="send" :class="{ 'disabled': loading || !inputText.trim() }">发送</view>
		</view>
	</view>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { api } from '@/api/request.js';

const messages = ref([
	{ role: 'ai', content: '您好！我是雪峰山专属导览助手“雪峰百事通”，关于穿岩山、大花瑶、阳雀坡等景区的问题，尽管问我吧！' }
]);
const inputText = ref('');
const loading = ref(false);
const lastMessageId = ref('');

const send = async () => {
	const text = inputText.value.trim();
	if (!text || loading.value) return;

	// 1. 添加用户消息
	messages.value.push({ role: 'user', content: text });
	inputText.value = '';
	loading.value = true;
	updateScroll();

	try {
		// 2. 调用后端接口 (使用封装好的 api 对象)
		const data = await api.aiChat(text);
		messages.value.push({ role: 'ai', content: data });
	} catch (e) {
		console.error('AI聊天失败:', e);
		// 如果后端返回了业务错误 code != 200，e 里面会有 msg
		const errorMsg = e.msg || '网络连接失败，请检查您的网络。';
		messages.value.push({ role: 'ai', content: errorMsg });
	} finally {
		loading.value = false;
		updateScroll();
	}
};

const updateScroll = () => {
	nextTick(() => {
		lastMessageId.value = 'msg-' + (messages.value.length - 1);
	});
};
</script>

<style lang="scss">
.container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.chat-list {
	flex: 1;
	padding: 20rpx;
	box-sizing: border-box;
}

.message-item {
	margin-bottom: 30rpx;
}

.ai-msg, .user-msg {
	display: flex;
	align-items: flex-start;
}

.user-msg {
	justify-content: flex-end;
}

.avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	background-color: #fff;
}

.avatar-user {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	background-color: #007aff;
	color: #fff;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 24rpx;
}

.content {
	max-width: 70%;
	margin: 0 20rpx;
}

.bubble {
	padding: 20rpx;
	border-radius: 20rpx;
	font-size: 28rpx;
	line-height: 1.5;
	display: inline-block;
	word-break: break-all;
}

.ai-msg .bubble {
	background-color: #fff;
	color: #333;
	border-top-left-radius: 2rpx;
}

.user-msg .bubble {
	background-color: #007aff;
	color: #fff;
	border-top-right-radius: 2rpx;
}

.loading-dots {
	color: #999;
	font-style: italic;
}

.placeholder {
	height: 120rpx;
}

.input-section {
	display: flex;
	align-items: center;
	padding: 20rpx;
	background-color: #fff;
	border-top: 1px solid #eee;
	padding-bottom: env(safe-area-inset-bottom);
}

.input-box {
	flex: 1;
	background-color: #f5f5f5;
	height: 80rpx;
	border-radius: 40rpx;
	padding: 0 30rpx;
	font-size: 28rpx;
}

.send-btn {
	margin-left: 20rpx;
	width: 120rpx;
	height: 70rpx;
	background-color: #007aff;
	color: #fff;
	border-radius: 35rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 26rpx;
	
	&.disabled {
		opacity: 0.5;
	}
}
</style>
