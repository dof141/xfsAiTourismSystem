# 第二轮代码质量修复文档

> 分支：`fix/round2-cleanup`（基于 `fix/security-round1`）
> 日期：2026-05-09

---

## 修复清单

### 1. 管理后台添加退出登录功能
**提交：** `56bbcf1`
**文件：** `xfs-admin/src/layout/index.vue`

**问题：** 管理员登录后无法主动退出，只能关闭浏览器或等待 token 过期。

**修复方案：**
- 在 layout 顶部 Header 右侧增加退出登录下拉菜单（`el-dropdown` + `SwitchButton` 图标）
- 点击后弹出确认对话框，防止误操作
- 确认后清除 `localStorage` 中的 `xfs_token` 和 `admin_info`，跳转到登录页
- 同时将硬编码的 `Admin` / `超级管理员` 改为从 `localStorage` 的 `admin_info` 动态读取

---

### 2. 修复子景点操作的空 catch 块
**提交：** `c51019b`
**文件：** `xfs-admin/src/views/AreaList.vue`

**问题：** `handleSaveSpot` 和 `handleDeleteSpot` 两个函数的 catch 块为空，操作失败时用户无任何反馈。

**修复方案：**
- `handleSaveSpot` 的 catch 块添加 `ElMessage.error('保存失败')`
- `handleDeleteSpot` 的 catch 块添加 `ElMessage.error('删除失败')`

---

### 3. Dashboard 统计失败提示 + 移除假数据
**提交：** `82f453f`
**文件：** `xfs-admin/src/views/Dashboard.vue`

**问题：**
- `fetchCardData` 的 catch 块为空，统计数据加载失败时静默显示 0
- 底部"智慧系统运行状态"区域全部是硬编码假数据（延迟 45ms、可用率 100%、负载均衡中）

**修复方案：**
- catch 块添加 `ElMessage.error('获取统计数据失败')`
- "运行状态"改为"技术架构说明"，展示真实技术栈信息（大语言模型、原子扣减防超卖、ZXing 动态生成）

---

### 4. 移除核销页和 AI 页的模拟假数据
**提交：** `8fba55e`
**文件：** `xfs-admin/src/views/Verify.vue`、`xfs-admin/src/views/AiChat.vue`

**问题：**
- Verify.vue 底部有一条写死的核销记录（时间 14:20:05、校验码 V-A3F8B2C9）
- AiChat.vue 左侧标注"对话列表 (模拟)"，显示虚假的对话列表

**修复方案：**
- Verify.vue 将模拟核销记录替换为操作提示说明
- AiChat.vue 移除"模拟"注释，改为正式的"AI 导览助手"标签

---

### 5. 小程序 API 地址环境变量化
**提交：** `d26a274`
**文件：** `xfs-miniapp/api/request.js`

**问题：** `BASE_URL` 硬编码为 `http://localhost:8080`，上线时需要手动修改。

**修复方案：**
- 使用 `process.env.NODE_ENV` 区分开发/生产环境
- 开发环境保持 `localhost:8080`，生产环境使用正式域名占位

---

### 6. 抽取小程序公共登录逻辑
**提交：** `9839e28`
**文件：** 新增 `xfs-miniapp/utils/auth.js`，修改 `pages/index/index.vue`、`pages/detail/detail.vue`

**问题：** 首页和详情页各有一套完整的登录流程（检查 token → 弹窗 → 调 API → 存 token），代码几乎完全相同。

**修复方案：**
- 新建 `utils/auth.js`，封装 `ensureLogin(onReady, options)` 函数
- 统一处理：token 检查、弹窗引导、手机号输入、API 调用、token 存储、错误提示
- 两个页面的重复代码（约 60 行）替换为一行 `ensureLogin()` 调用

---

### 7. 登录接口参数校验
**提交：** `72f59e2`
**文件：** `xfs-backend/src/main/java/com/xfs/xfsbackend/controller/AdminController.java`

**问题：** 登录接口未对 `username` 和 `password` 做 null 检查，当请求体异常时 `BCryptPasswordEncoder.matches()` 收到 null 参数抛出 `IllegalArgumentException`。

**修复方案：**
- 在查询数据库之前增加 `username` 和 `password` 的 null 检查
- 为 null 时直接返回"用户名和密码不能为空"错误提示

---

## 注意事项

### 数据库密码需要更新
SQL 种子文件中的密码已是 BCrypt 哈希，但如果数据库是旧数据，需要手动执行：

```sql
UPDATE sys_admin SET password = '$2a$10$Xi3R0HGIewq22zVaT4x3Ue.JH.hjCHbblLah.K75KV..JbUzN1aTK' WHERE username = 'admin';
```

此哈希对应的明文密码为 `123456`。

---

## 提交记录总览

| 提交 | 说明 |
|------|------|
| `56bbcf1` | 管理后台添加退出登录按钮 |
| `c51019b` | 修复子景点操作的空 catch 块 |
| `82f453f` | Dashboard 统计失败提示 + 移除假数据 |
| `8fba55e` | 移除核销页和 AI 页的模拟假数据 |
| `d26a274` | 小程序 API 地址从硬编码改为环境变量 |
| `9839e28` | 抽取公共登录逻辑到 utils/auth.js |
| `72f59e2` | 登录接口增加参数校验防止空指针 |
