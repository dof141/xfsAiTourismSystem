<template>
  <div class="login-wrapper">
    <div class="login-box">
      <div class="login-header">
        <div class="logo">🏔️</div>
        <h2>雪峰智慧云 · 管理系统</h2>
        <p>SMART TOURISM COMMAND CENTER</p>
      </div>

      <el-form :model="form" class="login-form">
        <el-form-item>
          <el-input 
            v-model="form.username" 
            placeholder="管理员账号" 
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="登录密码" 
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-button 
          type="primary" 
          class="login-btn" 
          @click="handleLogin" 
          :loading="loading"
        >
          即 刻 登 录
        </el-button>
      </el-form>
      
      <div class="login-footer">
        © 2026 XFS AI Tourism System. All Rights Reserved.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    return ElMessage.warning('请填写完整的账号密码')
  }

  loading.value = true
  try {
    const res = await request.post('/admin/login', form.value)
    if (res.code === 200) {
      ElMessage.success('登录成功，欢迎回来')
      // 存储 Token
      localStorage.setItem('xfs_token', res.data.token)
      localStorage.setItem('admin_info', JSON.stringify(res.data.admin))
      router.push('/dashboard')
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  background-image: radial-gradient(circle at 10% 20%, rgba(99, 102, 241, 0.05) 0%, transparent 40%),
                    radial-gradient(circle at 90% 80%, rgba(14, 165, 233, 0.05) 0%, transparent 40%);
}

.login-box {
  width: 400px;
  background: #ffffff;
  padding: 50px 40px;
  border-radius: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  font-size: 40px;
  margin-bottom: 15px;
}

.login-header h2 {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  margin: 0;
  letter-spacing: -0.5px;
}

.login-header p {
  font-size: 10px;
  color: #94a3b8;
  letter-spacing: 2px;
  margin-top: 8px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 5px 15px;
  background-color: #f8fafc;
  box-shadow: none !important;
  border: 1px solid #e2e8f0;
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 15px;
  font-weight: 700;
  font-size: 16px;
  background: linear-gradient(135deg, #6366f1, #0ea5e9);
  border: none;
  box-shadow: 0 8rpx 16rpx rgba(99, 102, 241, 0.2);
  margin-top: 20px;
}

.login-footer {
  margin-top: 40px;
  text-align: center;
  font-size: 11px;
  color: #cbd5e1;
}
</style>
