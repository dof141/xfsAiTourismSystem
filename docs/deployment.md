# 雪峰山智慧文旅系统 — 部署文档

## 一、环境要求

| 组件 | 版本要求 |
|------|---------|
| JDK | 1.8+ |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Redis | 5.0+ |
| Node.js | 16+（管理后台/小程序构建） |

## 二、数据库初始化

```sql
-- 1. 创建数据库
CREATE DATABASE xfs_ai_tourism_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 2. 导入表结构和初始数据
mysql -u root -p xfs_ai_tourism_system < sql/xfs_ai_tourism_system.sql
```

## 三、后端启动

### 1. 配置环境变量（可选）

```bash
# Windows
set DB_PASSWORD=你的数据库密码
set JWT_SECRET=你的JWT密钥
set AI_API_KEY=你的DeepSeek API Key

# Linux/Mac
export DB_PASSWORD=你的数据库密码
export JWT_SECRET=你的JWT密钥
export AI_API_KEY=你的AI API Key
```

不设置环境变量时使用 application.yml 中的默认值。

### 2. 启动 Redis

```bash
redis-server
```

### 3. 启动后端

```bash
cd xfs-backend
mvn spring-boot:run
```

启动成功后：
- 后端 API：`http://localhost:8080`
- API 文档：`http://localhost:8080/doc.html`
- Druid 监控：`http://localhost:8080/druid`（账号 admin / admin123）

## 四、管理后台启动

```bash
cd xfs-admin
npm install
npm run dev
```

访问 `http://localhost:5173`，使用 admin / 123456 登录。

## 五、小程序启动

1. 用 HBuilderX 打开 `xfs-miniapp` 目录
2. 运行 → 运行到微信开发者工具
3. 如需真机预览，需在 `manifest.json` 中填写微信小程序 AppID

## 六、AI 功能配置

在 DeepSeek 开放平台申请 API Key：`https://platform.deepseek.com`

配置到 `application.yml` 或环境变量：
```yaml
xfs:
  ai:
    api-key: sk-xxxxxxxxxxxxxxxx
    api-url: https://api.deepseek.com/chat/completions
    model: deepseek-chat
```

## 七、常见问题

| 问题 | 解决方案 |
|------|---------|
| Redis 连接失败 | 确认 Redis 服务已启动，默认端口 6379 |
| 数据库连接失败 | 检查 MySQL 服务和 application.yml 中的密码 |
| AI 接口超时 | 检查 API Key 是否正确，网络是否通畅 |
| Knife4j 页面空白 | 确认 spring.mvc.pathmatch.matching-strategy 已配置 |
