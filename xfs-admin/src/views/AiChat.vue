<template>
  <div class="modern-page-container">
    <div class="chat-layout">
      <!-- 左侧：AI 助手信息 -->
      <div class="white-card chat-sidebar">
        <div class="sidebar-header">AI 导览助手</div>
        <div class="contact-list">
          <div class="contact-item active">
            <div class="avatar">🤖</div>
            <div class="info">
              <div class="name">雪峰百事通</div>
              <div class="last-msg">在线为您服务</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：聊天主窗口 -->
      <div class="white-card chat-main">
        <div class="chat-header">
          <div class="user-info">
            <span class="status-dot"></span>
            <span class="name">雪峰百事通 (AI 引擎)</span>
          </div>
          <div class="actions">
            <el-button link :icon="Delete" @click="clearMessages">清空记录</el-button>
          </div>
        </div>

        <div class="message-history" ref="chatHistoryRef">
          <div
            v-for="(msg, index) in messageList"
            :key="index"
            :class="['msg-wrapper', msg.role === 'user' ? 'user-side' : 'ai-side']"
          >
            <div class="bubble">
              <div class="bubble-content">{{ msg.content }}</div>
            </div>
          </div>
          <div v-if="loading" class="msg-wrapper ai-side">
            <div class="bubble loading">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </div>
          </div>
        </div>

        <div class="input-area">
          <el-input
            v-model="inputText"
            placeholder="在此输入您的问题..."
            @keyup.enter="sendMessage"
            :disabled="loading"
          >
            <template #append>
              <el-button type="primary" @click="sendMessage" :loading="loading">发送</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const inputText = ref('')
const messageList = ref([
  { role: 'ai', content: '您好！我是雪峰山专属导览助手，很高兴为您服务。请问有什么可以帮您的吗？' }
])
const loading = ref(false)
const chatHistoryRef = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatHistoryRef.value) {
    chatHistoryRef.value.scrollTop = chatHistoryRef.value.scrollHeight
  }
}

const clearMessages = () => {
  messageList.value = [
    { role: 'ai', content: '您好！我是雪峰山专属导览助手，很高兴为您服务。请问有什么可以帮您的吗？' }
  ]
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text) return

  messageList.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await request.post('/ai/chat', { text: text })
    messageList.value.push({ role: 'ai', content: res.data })
  } catch (error) {
    console.error(error)
    messageList.value.push({ role: 'ai', content: '抱歉，服务暂时不可用，请稍后重试' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
.modern-page-container {
  height: calc(100vh - 140px);
}

.chat-layout {
  display: flex;
  height: 100%;
  gap: 20px;
}

.white-card {
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0,0,0,0.02);
  display: flex;
  flex-direction: column;
}

.chat-sidebar {
  width: 280px;
}

.sidebar-header {
  padding: 24px;
  font-weight: 800;
  color: #1e293b;
  border-bottom: 1px solid #f1f5f9;
}

.contact-item {
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.contact-item.active {
  background: #f1f5f9;
}

.contact-item .avatar {
  width: 40px;
  height: 40px;
  background: #e2e8f0;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.contact-item .name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.contact-item .last-msg {
  font-size: 12px;
  color: #94a3b8;
}

.chat-main {
  flex: 1;
}

.chat-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
}

.user-info .name {
  font-weight: 700;
  color: #1e293b;
}

.message-history {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #fcfdfe;
}

.msg-wrapper {
  display: flex;
  margin-bottom: 24px;
}

.user-side { justify-content: flex-end; }

.bubble {
  max-width: 70%;
  padding: 14px 20px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
}

.ai-side .bubble {
  background: #ffffff;
  color: #1e293b;
  border: 1px solid #f1f5f9;
  border-top-left-radius: 2px;
}

.user-side .bubble {
  background: #6366f1;
  color: #ffffff;
  border-top-right-radius: 2px;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
}

.input-area {
  padding: 24px;
  border-top: 1px solid #f1f5f9;
}

:deep(.el-input-group__append) {
  background-color: #6366f1;
  color: white;
  border: none;
}

/* 加载动画 */
.loading .dot {
  display: inline-block;
  width: 4px;
  height: 4px;
  background: #94a3b8;
  border-radius: 50%;
  margin: 0 2px;
  animation: bounce 1.4s infinite ease-in-out both;
}
.loading .dot:nth-child(1) { animation-delay: -0.32s; }
.loading .dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1.0); }
}
</style>
