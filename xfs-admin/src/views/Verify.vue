<template>
  <div class="modern-page-container">
    <div class="verify-wrapper">
      <div class="white-card verify-card">
        <!-- 头部装饰 -->
        <div class="verify-header">
          <div class="icon-circle">
            <el-icon><Ticket /></el-icon>
          </div>
          <h2 class="title">核销工作台</h2>
          <p class="subtitle">请扫描游客预约二维码或手动输入校验码</p>
        </div>

        <!-- 输入区域 -->
        <div class="input-section">
          <div class="input-group">
            <el-input
              v-model="scanOrderNo"
              placeholder="输入 8 位校验码或订单号"
              size="large"
              @keyup.enter="handleVerify"
              class="modern-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button 
              type="primary" 
              class="verify-btn" 
              @click="handleVerify" 
              :loading="loading"
            >
              立即核销
            </el-button>
          </div>
        </div>

        <!-- 动态结果展示 -->
        <transition name="el-zoom-in-top">
          <div v-if="verifyResult" class="result-display" :class="verifyResult.type">
            <div class="result-icon">
              <el-icon v-if="verifyResult.type === 'success'"><Check /></el-icon>
              <el-icon v-else><Warning /></el-icon>
            </div>
            <div class="result-info">
              <div class="res-title">{{ verifyResult.title }}</div>
              <div class="res-msg">{{ verifyResult.msg }}</div>
            </div>
          </div>
        </transition>

        <!-- 核销提示 -->
        <div class="recent-verify">
          <div class="recent-title">操作提示</div>
          <div class="recent-list">
            <div class="recent-item">
              <span class="time">提示</span>
              <span class="code">输入订单号或扫描二维码即可完成核销</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Ticket, Search, Check, Warning } from '@element-plus/icons-vue'
import request from '../utils/request'

const scanOrderNo = ref('')
const loading = ref(false)
const verifyResult = ref(null)

const handleVerify = async () => {
  if (!scanOrderNo.value.trim()) {
    return ElMessage.warning('请输入或扫描订单号')
  }

  loading.value = true
  verifyResult.value = null 
  
  try {
    const res = await request.post(`/reserve/verify/${scanOrderNo.value}`)
    verifyResult.value = { type: 'success', title: '核销成功', msg: '订单已验证，准予入园' }
    ElMessage.success('核销成功')
  } catch (error) {
    if (error.response && error.response.data) {
        verifyResult.value = { type: 'error', title: '核销失败', msg: error.response.data.msg }
    }
  } finally {
    loading.value = false
    scanOrderNo.value = ''
  }
}
</script>

<style scoped>
.verify-wrapper {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}

.verify-card {
  width: 100%;
  max-width: 600px;
  text-align: center;
  padding: 50px 40px;
}

.icon-circle {
  width: 80px;
  height: 80px;
  background: #f1f5f9;
  color: #6366f1;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin: 0 auto 24px;
}

.title {
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
}

.subtitle {
  color: #94a3b8;
  font-size: 14px;
  margin-bottom: 40px;
}

.input-group {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
}

.modern-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  background: #f8fafc;
  box-shadow: none !important;
  border: 1px solid #e2e8f0;
}

.verify-btn {
  border-radius: 12px;
  padding: 0 30px;
  font-weight: 700;
  height: 45px;
}

/* 结果展示 */
.result-display {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 30px;
  text-align: left;
  gap: 16px;
}

.result-display.success {
  background: #ecfdf5;
  color: #059669;
}

.result-display.error {
  background: #fef2f2;
  color: #dc2626;
}

.result-icon {
  font-size: 24px;
}

.res-title {
  font-weight: 800;
  font-size: 16px;
}

.res-msg {
  font-size: 14px;
  opacity: 0.8;
}

/* 最近核销 */
.recent-verify {
  border-top: 1px solid #f1f5f9;
  padding-top: 30px;
  text-align: left;
}

.recent-title {
  font-size: 14px;
  font-weight: 700;
  color: #64748b;
  margin-bottom: 16px;
}

.recent-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  padding: 10px 0;
  color: #1e293b;
}

.recent-item .time { color: #94a3b8; }
.recent-item .status.success { color: #10b981; font-weight: 700; }
</style>
