<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">雪峰山文旅后台</div>
      <el-menu
          active-text-color="#ffd04b"
          background-color="#304156"
          text-color="#fff"
          :default-active="$route.path"
          router
      >
        <el-menu-item index="/area">
          <el-icon><Location /></el-icon>
          <span>景区信息管理</span>
        </el-menu-item>
        <el-menu-item index="/ai">
          <el-icon><ChatDotRound /></el-icon>
          <span>AI 智能文旅助手</span>
        </el-menu-item>
        <el-menu-item index="/verify">
          <el-icon><ChatDotRound /></el-icon>
          <span>二维码核销</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">{{ $route.meta.title }}</div>
        <div class="header-right">管理员：Admin</div>
      </el-header>

      <el-main class="main-content">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { Location, ChatDotRound } from '@element-plus/icons-vue'
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: #304156;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b3649;
}
.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>