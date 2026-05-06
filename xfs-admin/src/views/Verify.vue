<template>
  <div class="verify-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <h2>🎫 景区入园核销工作台</h2>
        </div>
      </template>

      <!-- 核心工作区：模拟扫码枪输入 -->
      <div class="scan-area">
        <el-input
            v-model="scanOrderNo"
            placeholder="请使用扫码枪扫描游客二维码，或手动输入订单号"
            size="large"
            clearable
            @keyup.enter="handleVerify"
            class="scan-input"
        >
          <template #prepend>订单号</template>
        </el-input>
        <el-button type="primary" size="large" @click="handleVerify" :loading="loading">
          确认核销
        </el-button>
      </div>

      <!-- 结果提示区 -->
      <div class="result-area" v-if="verifyResult">
        <el-alert
            :title="verifyResult.title"
            :type="verifyResult.type"
            :description="verifyResult.msg"
            show-icon
            :closable="false"
            class="custom-alert"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios' // 假设你用 axios，如果用自己封装的 request 请替换

const scanOrderNo = ref('')
const loading = ref(false)
const verifyResult = ref(null)

const handleVerify = async () => {
  if (!scanOrderNo.value.trim()) {
    return ElMessage.warning('请输入或扫描订单号')
  }

  loading.value = true
  try {
    // 调用 Java 后端的核销接口
    const res = await axios.post(`http://localhost:8080/api/reserve/verify/${scanOrderNo.value}`)

    if (res.data.code === 200) {
      verifyResult.value = { type: 'success', title: '核销通过', msg: res.data.data }
      ElMessage.success('核销成功')
    } else {
      verifyResult.value = { type: 'error', title: '核销拦截', msg: res.data.msg }
      ElMessage.error(res.data.msg)
    }
  } catch (error) {
    ElMessage.error('网络请求失败')
  } finally {
    loading.value = false
    scanOrderNo.value = '' // 核销完清空输入框，准备扫下一个
  }
}
</script>

<style scoped>
.verify-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}
.box-card {
  width: 100%;
  max-width: 800px;
  margin-top: 50px;
}
.card-header {
  text-align: center;
  color: #303133;
}
.scan-area {
  display: flex;
  gap: 20px;
  margin: 40px 0;
}
.scan-input {
  flex: 1;
}
.result-area {
  margin-top: 30px;
}
.custom-alert {
  padding: 20px;
}
</style>