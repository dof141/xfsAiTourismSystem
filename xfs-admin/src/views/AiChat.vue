<template>
  <div class="chat-container">
    <el-card class="chat-card" shadow="hover">
      <template #header>
        <div class="chat-header">
          <span>🏔️ 雪峰山智慧文旅 AI 助手</span>
        </div>
      </template>

      <div class="chat-history" ref="chatHistoryRef">
        <div
            v-for="(msg, index) in messageList"
            :key="index"
            :class="['message-item', msg.role === 'user' ? 'message-user' : 'message-ai']"
        >
          <div class="message-bubble">{{ msg.content }}</div>
        </div>
        <div v-if="loading" class="message-item message-ai">
          <div class="message-bubble loading-dots">正在思考中...</div>
        </div>
      </div>

      <div class="chat-input-area">
        <el-input
            v-model="inputText"
            placeholder="问问我关于雪峰山五大景区的问题吧..."
            @keyup.enter="sendMessage"
            :disabled="loading"
            clearable
        >
          <template #append>
            <el-button type="primary" @click="sendMessage" :loading="loading">发送</el-button>
          </template>
        </el-input>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 输入框内容
const inputText = ref('')
// 聊天记录列表
const messageList = ref([
  { role: 'ai', content: '您好！我是雪峰山专属导览助手，您可以问我关于穿岩山、大花瑶等五大景区的问题。' }
])
// 是否正在请求中
const loading = ref(false)
// 聊天框的DOM引用，用于自动滚动
const chatHistoryRef = ref(null)

// 自动滚动到最底部的方法
const scrollToBottom = async () => {
  await nextTick()
  if (chatHistoryRef.value) {
    chatHistoryRef.value.scrollTop = chatHistoryRef.value.scrollHeight
  }
}

// 发送消息的方法
const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text) {
    ElMessage.warning('请输入您的问题！')
    return
  }

  // 1. 将用户的问题加入聊天列表
  messageList.value.push({ role: 'user', content: text })
  inputText.value = '' // 清空输入框
  loading.value = true
  scrollToBottom()

  try {
    // 2. 调用后端 AI 接口
    const response = await axios.post('/api/ai/chat', { text: text })

    // 3. 处理后端返回的数据
    const resData = response.data
    if (resData.code === 200) {
      // 将 AI 的回答加入聊天列表
      messageList.value.push({ role: 'ai', content: resData.data })
    } else {
      ElMessage.error(resData.msg || '获取回答失败，请稍后重试')
      messageList.value.push({ role: 'ai', content: '抱歉，系统开小差了，请稍后再试。' })
    }
  } catch (error) {
    console.error('AI请求出错:', error)
    ElMessage.error('网络请求失败，请检查后端是否启动')
    messageList.value.push({ role: 'ai', content: '网络错误，无法连接到服务器。' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
/* 整个页面的背景和居中 */
.chat-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
  box-sizing: border-box;
}

/* 聊天卡片的样式 */
.chat-card {
  width: 100%;
  max-width: 800px;
  height: 80vh;
  display: flex;
  flex-direction: column;
}

/* 因为 el-card 有默认的 body padding，我们需要覆盖它以便更好地控制布局 */
:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
}

.chat-header {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

/* 聊天历史区域 */
.chat-history {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #fafafa;
}

/* 单条消息的容器 */
.message-item {
  display: flex;
  margin-bottom: 20px;
}

/* 区分用户和 AI 消息的靠左/靠右布局 */
.message-user {
  justify-content: flex-end;
}

.message-ai {
  justify-content: flex-start;
}

/* 气泡样式 */
.message-bubble {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap; /* 保留后端返回的换行符 */
}

/* 用户气泡颜色 */
.message-user .message-bubble {
  background-color: #409EFF;
  color: white;
  border-bottom-right-radius: 2px;
}

/* AI 气泡颜色 */
.message-ai .message-bubble {
  background-color: white;
  color: #333;
  border: 1px solid #ebeef5;
  border-bottom-left-radius: 2px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 输入框区域 */
.chat-input-area {
  padding: 15px 20px;
  background-color: white;
  border-top: 1px solid #ebeef5;
}

.loading-dots {
  color: #909399;
  font-style: italic;
}
</style>