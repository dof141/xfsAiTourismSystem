这是一份标准的开源项目 `README.md` 中文版格式，你可以直接复制并保存到项目根目录下。

***

# 🏔️ 雪峰山智慧文旅系统

<div align="center">

![Java](https://img.shields.io/badge/Java-Spring_Boot-green.svg)
![Vue](https://img.shields.io/badge/Vue-3.0-blue.svg)
![UniApp](https://img.shields.io/badge/UniApp-Mini_Program-42b983.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)
![Redis](https://img.shields.io/badge/Redis-Upcoming-red.svg)

**一个企业级 O2O (线上到线下) 智慧文旅平台，提供分时预约、电子核销与动态数据管理功能。**

**简体中文** | [English](./README.en.md)

</div>

## 📖 项目简介

传统景区在节假日往往面临人流拥挤、购票排队时间长、管理数据滞后等痛点。**雪峰山智慧文旅系统**旨在通过提供完整的数字化工作流来解决这些问题。

本系统为游客提供从线上浏览、分时段预约到二维码扫码入园的无缝体验；同时为景区管理者提供高效的数字化核销工具及数据驱动的决策支持。

## ✨ 核心特性

### ✅ 已完成功能

**📱 游客移动端小程序 (C端)**
* **动态首页大厅：** 基于真实浏览量与预约量实时生成的“热门推荐”排行榜及完整的景区列表。
* **智能分时预约：** 允许游客选择特定游玩日期及入园时段（如 08:00-10:00）锁定名额。
* **电子凭证系统：** 预约成功后，后端内存动态渲染生成高清 Base64 二维码凭证（不占用数据库存储空间）。
* **个人中心闭环：** “我的预约”票务列表，支持订单状态（待入园/已核销）的实时追踪。

**💻 后台管理端 (B端) & 核心服务端**
* **双通道核销工作台：** 提供专业的核销界面，支持扫码枪（长订单号）与人工键盘录入（8位防碰撞短码）两种模式。
* **健壮的数据安全：** 采用 UUID 生成唯一短核销码，并在 MySQL 底层加设 `UNIQUE INDEX`（唯一索引）实现物理级防重保障，杜绝一票多用。

### 🚀 演进路线图 (规划中)

* [ ] **高并发防超卖：** 引入 **Redis** 内存缓存与原子操作（`decrement`），应对“黄金周”万人抢票的并发洪峰，保护数据库并防止库存超卖。
* [ ] **智慧调度数据大屏：** 在 Vue 管理端集成 **ECharts**，可视化展示景区客流占比、近 7 日预约趋势及实时运营数据。
* [ ] **LBS 地图导航：** 利用 UniApp `<map>` 组件及地理位置 API，实现景区内的实时空间定位与路线规划。
* [ ] **认证与权限安全：** 接入微信原生登录（OpenID），并使用 Spring Security/Sa-Token 实现后台管理员的 RBAC（基于角色的权限控制）。

## 🛠️ 技术栈

### 后端 (Backend)
* **核心框架：** Java 8+, Spring Boot 2.x
* **持久层框架：** MyBatis-Plus
* **数据库：** MySQL 8.0
* **工具与中间件：** ZXing (二维码引擎), Lombok
* **缓存 (规划中)：** Redis

### 前端 (Frontend)
* **移动端 (微信小程序)：** UniApp, Vue 3 (Composition API)
* **Web 管理端：** Vue 3, Element Plus, Axios, ECharts (规划中)

## 🏗️ 架构设计

系统采用标准的**前后端分离**架构设计：
1. **状态机驱动：** 订单生命周期（待入园 -> 已核销）由后端状态机严格控制，确保数据一致性。
2. **O2O 业务闭环：** 实现了从线上资源分配到线下物理核销的完整业务链路。
3. **无状态 API：** 遵循 RESTful API 设计规范，确保跨平台（Web、iOS、Android、微信小程序）的良好兼容性。

## 🚀 快速开始

### 环境要求
* JDK 1.8 或更高版本
* MySQL 5.7+
* Node.js 16+
* 微信开发者工具 & HBuilderX

### 安装与运行

**1. 后端服务 (Spring Boot)**
```bash
# 克隆仓库
git clone https://github.com/yourusername/xuefeng-smart-tourism.git

# 将 /docs/sql/xfs_tourism.sql 脚本导入到你的 MySQL 数据库中

# 修改 application.yml 中的数据库配置信息
spring.datasource.username=root
spring.datasource.password=你的密码

# 启动 XfsBackendApplication.java
```

**2. Web 管理后台 (Vue 3)**
```bash
cd xfs-admin
npm install
npm run dev
```

**3. 微信小程序 (UniApp)**
* 在 **HBuilderX** 中打开 `xfs-miniapp` 文件夹。
* 点击顶部菜单栏的 `运行 -> 运行到小程序模拟器 -> 微信开发者工具`。

## 📄 开源协议

本项目基于 MIT 协议开源 - 详情请查看 [LICENSE](LICENSE) 文件。

***
