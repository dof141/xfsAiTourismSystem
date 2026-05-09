<template>
	<view class="ai-assistant-wrapper">
		<!-- 1. 悬浮按钮 -->
		<view class="float-btn" @click="toggleChat" v-if="!showChat">
			<view class="icon-box">🤖</view>
			<view class="label-box">AI导览</view>
		</view>

		<!-- 2. 聊天悬浮窗 (Modal) -->
		<view class="chat-modal" v-if="showChat" @touchmove.stop.prevent>
			<view class="chat-window">
				<!-- 头部：标题与关闭按钮 -->
				<view class="chat-header">
					<view class="title">
						<text class="status-dot"></text>
						雪峰百事通
					</view>
					<view class="close-btn" @click="toggleChat">✕</view>
				</view>

				<!-- 内容区：滚动列表 -->
				<scroll-view 
					class="chat-body" 
					scroll-y 
					:scroll-into-view="lastMessageId" 
					scroll-with-animation
				>
					<view class="msg-item" v-for="(msg, index) in messages" :key="index" :id="'msg-' + index">
						<view :class="['bubble-container', msg.role === 'user' ? 'user-side' : 'ai-side']">
							<image v-if="msg.role === 'ai'" class="avatar" src="/static/logo.png"></image>
							<view class="bubble">{{ msg.content }}</view>
						</view>
					</view>
					
					<!-- 加载中状态 -->
					<view v-if="loading" class="msg-item" id="loading-node">
						<view class="bubble-container ai-side">
							<image class="avatar" src="/static/logo.png"></image>
							<view class="bubble loading">正在思考...</view>
						</view>
					</view>
					<!-- 底部占位，解决滚动不到底的问题 -->
					<view class="bottom-anchor" id="anchor"></view>
				</scroll-view>

				<!-- 底部：输入框 -->
				<view class="chat-footer">
					<input 
						class="input" 
						v-model="inputText" 
						placeholder="有问题尽管问我..." 
						confirm-type="send"
						@confirm="handleSend"
						:disabled="loading"
					/>
					<view class="send-icon" @click="handleSend" :class="{ 'active': inputText.trim() && !loading }">▲</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { api } from '@/api/request.js';

const showChat = ref(false);
const loading = ref(false);
const inputText = ref('');
const lastMessageId = ref('');
const messages = ref([
	{ role: 'ai', content: '您好！我是雪峰山专属导览助手，您可以问我任何关于景区、美食、路线的问题。' }
]);

const toggleChat = () => {
	showChat.value = !showChat.value;
	if (showChat.value) {
		scrollToBottom();
	}
};

const handleSend = async () => {
	const text = inputText.value.trim();
	if (!text || loading.value) return;

	messages.value.push({ role: 'user', content: text });
	inputText.value = '';
	loading.value = true;
	scrollToBottom();

	try {
		const history = messages.value
			.filter(m => m.content !== '您好！我是雪峰山专属导览助手，您可以问我任何关于景区、美食、路线的问题。')
			.map(m => ({ role: m.role, content: m.content }));
		const data = await api.aiChat(text, history);
		messages.value.push({ role: 'ai', content: data });
	} catch (e) {
		messages.value.push({ role: 'ai', content: e.msg || '连接失败，请稍后再试' });
	} finally {
		loading.value = false;
		scrollToBottom();
	}
};

const scrollToBottom = () => {
	nextTick(() => {
		setTimeout(() => {
			lastMessageId.value = 'anchor';
		}, 100);
	});
};

defineExpose({ toggleChat });
</script>

<style lang="scss">
.ai-assistant-wrapper {
	/* 悬浮按钮样式 */
	.float-btn {
		position: fixed;
		right: 30rpx;
		bottom: 160rpx; /* 进一步提高，避开所有可能的遮挡 */
		width: 110rpx;
		height: 110rpx;
		background: linear-gradient(135deg, #007aff, #00c6ff);
		border-radius: 50%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		box-shadow: 0 8rpx 20rpx rgba(0, 122, 255, 0.4);
		z-index: 9999; /* 极高优先级 */
		
		.icon-box { 
			font-size: 44rpx; 
			line-height: 1;
			margin-bottom: 4rpx;
		}
		.label-box { 
			font-size: 20rpx; 
			color: #fff; 
			font-weight: bold;
			line-height: 1;
		}
	}

	/* 弹出窗口样式 */
	.chat-modal {
		position: fixed;
		top: 0;
		left: 0;
		width: 100vw;
		height: 100vh;
		background: rgba(0, 0, 0, 0.4);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1000;
		padding: 40rpx;
		box-sizing: border-box;

		.chat-window {
			width: 100%;
			height: 70vh;
			background: #f8f9fa;
			border-radius: 30rpx;
			display: flex;
			flex-direction: column;
			overflow: hidden;
			animation: slideUp 0.3s ease-out;
		}
	}

	.chat-header {
		padding: 30rpx;
		background: #fff;
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 1px solid #eee;

		.title {
			font-size: 30rpx;
			font-weight: bold;
			display: flex;
			align-items: center;
			.status-dot {
				width: 12rpx;
				height: 12rpx;
				background: #52c41a;
				border-radius: 50%;
				margin-right: 12rpx;
			}
		}
		.close-btn { color: #999; padding: 10rpx; }
	}

	.chat-body {
		flex: 1;
		padding: 20rpx;
		box-sizing: border-box;

		.bubble-container {
			display: flex;
			margin-bottom: 30rpx;
			&.user-side { justify-content: flex-end; }
			
			.avatar {
				width: 60rpx;
				height: 60rpx;
				border-radius: 50%;
				background: #fff;
				margin-right: 16rpx;
			}
			
			.bubble {
				max-width: 70%;
				padding: 20rpx;
				font-size: 26rpx;
				line-height: 1.5;
				border-radius: 16rpx;
				word-break: break-all;
			}
		}

		.ai-side .bubble { background: #fff; color: #333; border-top-left-radius: 4rpx; }
		.user-side .bubble { background: #007aff; color: #fff; border-top-right-radius: 4rpx; }
		
		.loading { color: #999; font-style: italic; }
		.bottom-anchor { height: 20rpx; width: 100%; }
	}

	.chat-footer {
		padding: 20rpx;
		background: #fff;
		display: flex;
		align-items: center;
		border-top: 1px solid #eee;

		.input {
			flex: 1;
			height: 70rpx;
			background: #f1f3f5;
			border-radius: 35rpx;
			padding: 0 30rpx;
			font-size: 26rpx;
		}
		
		.send-icon {
			width: 60rpx;
			height: 60rpx;
			margin-left: 20rpx;
			background: #ddd;
			color: #fff;
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			transition: all 0.3s;
			&.active { background: #007aff; }
		}
	}
}

@keyframes slideUp {
	from { transform: translateY(50rpx); opacity: 0; }
	to { transform: translateY(0); opacity: 1; }
}
</style>
