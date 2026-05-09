<template>
  <div class="app-wrapper">
    <!-- 1. 悬浮侧边栏 -->
    <aside class="modern-sidebar" :class="{ 'collapsed': isCollapsed }">
      <div class="logo-area">
        <div class="logo-icon">🏔️</div>
        <span v-if="!isCollapsed" class="logo-text">雪峰智慧云</span>
      </div>
      
      <nav class="nav-menu">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ 'active': $route.path === item.path }"
        >
          <el-icon class="icon"><component :is="item.icon" /></el-icon>
          <span v-if="!isCollapsed">{{ item.name }}</span>
          <div class="active-indicator" v-if="$route.path === item.path"></div>
        </router-link>
      </nav>

      <div class="collapse-toggle" @click="isCollapsed = !isCollapsed">
        <el-icon><Expand v-if="isCollapsed" /><Fold v-else /></el-icon>
      </div>
    </aside>

    <!-- 2. 主体区域 -->
    <main class="main-container">
      <!-- 顶部悬浮 Header -->
      <header class="modern-header">
        <div class="breadcrumb">
          <span class="root">工作台</span>
          <span class="separator">/</span>
          <span class="current">{{ $route.meta.title }}</span>
        </div>
        <div class="user-profile">
          <div class="info">
            <span class="name">{{ adminName }}</span>
            <span class="role">{{ adminRole }}</span>
          </div>
          <el-avatar :size="35" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
          <el-dropdown @command="handleCommand">
            <el-icon class="logout-icon" :size="18"><SwitchButton /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <section class="content-view">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { Monitor, Location, ChatDotRound, Ticket, Expand, Fold, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const isCollapsed = ref(false)

const menuItems = [
  { name: '数据看板', path: '/dashboard', icon: Monitor },
  { name: '景区管理', path: '/area', icon: Location },
  { name: 'AI 助手', path: '/ai', icon: ChatDotRound },
  { name: '核销中心', path: '/verify', icon: Ticket }
]

const adminName = computed(() => {
  try {
    const info = JSON.parse(localStorage.getItem('admin_info') || '{}')
    return info.username || 'Admin'
  } catch { return 'Admin' }
})

const adminRole = computed(() => {
  try {
    const info = JSON.parse(localStorage.getItem('admin_info') || '{}')
    return info.role === 'SUPER_ADMIN' ? '超级管理员' : '管理员'
  } catch { return '管理员' }
})

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定退出登录？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('xfs_token')
      localStorage.removeItem('admin_info')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  background-color: #f8fafc; /* 非常淡的蓝灰色背景 */
  padding: 16px;
  box-sizing: border-box;
}

/* 侧边栏重构：悬浮、毛玻璃、圆角 */
.modern-sidebar {
  width: 240px;
  background: #ffffff;
  border-radius: 20px;
  margin-right: 16px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 100;
}

.modern-sidebar.collapsed {
  width: 80px;
}

.logo-area {
  padding: 30px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #6366f1, #0ea5e9);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.logo-text {
  font-weight: 800;
  font-size: 18px;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.nav-menu {
  flex: 1;
  padding: 0 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  margin-bottom: 8px;
  border-radius: 12px;
  text-decoration: none;
  color: #64748b;
  transition: all 0.2s;
  position: relative;
  gap: 12px;
}

.nav-item .icon {
  font-size: 20px;
}

.nav-item:hover {
  background-color: #f1f5f9;
  color: #1e293b;
}

.nav-item.active {
  background: linear-gradient(to right, #6366f110, #0ea5e910);
  color: #6366f1;
  font-weight: 600;
}

.active-indicator {
  position: absolute;
  left: 0;
  width: 4px;
  height: 20px;
  background: #6366f1;
  border-radius: 0 4px 4px 0;
}

.collapse-toggle {
  padding: 20px;
  text-align: center;
  cursor: pointer;
  color: #94a3b8;
  border-top: 1px solid #f1f5f9;
}

/* 主容器 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Header：悬浮毛玻璃 */
.modern-header {
  height: 70px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb .root { color: #94a3b8; }
.breadcrumb .current { color: #1e293b; font-weight: 600; }
.breadcrumb .separator { color: #e2e8f0; }

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logout-icon {
  color: #94a3b8;
  cursor: pointer;
  transition: color 0.2s;
}

.logout-icon:hover {
  color: #ef4444;
}

.user-profile .info {
  text-align: right;
  line-height: 1.2;
}

.user-profile .name {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.user-profile .role {
  font-size: 12px;
  color: #94a3b8;
}

/* 内容区域滚动 */
.content-view {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px; /* 留出滚动条空间 */
}

/* 动画效果 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-15px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(15px);
}
</style>
