# 第二轮增强功能文档

> 分支：已合并到 `master`
> 日期：2026-05-09

---

## 一、Knife4j API 文档

**访问地址：** `http://localhost:8080/doc.html`

启动后端后，在浏览器打开上述地址即可查看所有 API 接口文档，支持在线调试。

**配置说明：**
- 引入 `knife4j-spring-boot-starter 3.0.3`
- 自动扫描 `com.xfs.xfsbackend.controller` 包下所有 Controller
- 已放行登录拦截，无需 token 即可访问文档页面

---

## 二、AOP 请求日志增强

**修改文件：** `LogAspect.java`

**记录内容：**
- HTTP 方法（GET/POST/PUT/DELETE）
- 请求 URL
- Controller 方法名
- 客户端 IP
- 请求参数（截断到 200 字符）
- 耗时（毫秒）
- 异常信息（仅在出错时记录）

**日志示例：**
```
INFO  [POST] /api/admin/login AdminController.login | IP: 127.0.0.1 | 参数: [SysAdmin(username=admin)] | 耗时: 45ms
ERROR [GET] /api/reserve/myList ReserveController.getMyList | IP: 192.168.1.5 | 参数: [] | 耗时: 12ms | 异常: Connection refused
```

---

## 三、AI 多轮对话

**修改文件：** `QuestionDTO.java`、`AiUtils.java`、`AiController.java`

**功能：**
- 前端发送 `text`（当前问题）+ `history`（对话历史数组）
- 后端保留最近 10 轮（20 条消息）作为上下文发送给大模型
- AI 可以理解上下文，如"刚才说的那个景区怎么去？"

**前端调用方式：**
```javascript
// 管理后台
const res = await request.post('/ai/chat', { text, history })

// 小程序
const data = await api.aiChat(text, history)
```

**history 格式：**
```json
[
  { "role": "user", "content": "雪峰山有什么好玩的？" },
  { "role": "ai", "content": "雪峰山有五大核心景区..." }
]
```

---

## 四、管理后台表单校验

**修改文件：** `AreaList.vue`

**校验规则：**

| 字段 | 规则 |
|------|------|
| 景区名称 | 必填 |
| 景区等级 | 必填 |
| 地理位置 | 必填 |
| 景区简介 | 必填 |
| 景点名称 | 必填 |
| 门票价格 | 必填 |
| 开放时间 | 必填 |
| 景点简介 | 必填 |

未填写必填字段时，表单会显示红色提示，无法提交。

---

## 五、全局异常处理增强

**修改文件：** `GlobalExceptionHandler.java`

**新增处理的异常类型：**

| 异常 | 返回消息 |
|------|---------|
| `MethodArgumentNotValidException` | 参数校验失败的具体字段和原因 |
| `HttpMessageNotReadableException` | 请求体格式错误，请传入合法JSON |
| `MissingServletRequestParameterException` | 缺少必填参数: xxx |
| `HttpRequestMethodNotSupportedException` | 不支持的请求方法: xxx |

---

## 六、个人预约修复

**问题：** 所有用户看到相同的预约记录
**原因：** 本地缓存 key 固定为 `my_reserve_cache`，不同用户共享同一份缓存
**修复：** 缓存 key 改为 `my_reserve_cache_{touristId}`，退出登录时清除用户专属缓存

---

## 测试指南

### 测试 API 文档
1. 启动后端
2. 浏览器打开 `http://localhost:8080/doc.html`
3. 应看到 Knife4j 界面，列出所有 API 分组

### 测试 AOP 日志
1. 启动后端
2. 在管理后台执行任意操作（登录、查看景区等）
3. 查看后端控制台日志，应包含 URL、IP、参数、耗时

### 测试 AI 多轮对话
1. 启动后端（需配置有效的 AI API Key）
2. 管理后台 → AI 助手 → 发送"雪峰山有什么好玩的？"
3. 追问"门票多少钱？" → AI 应能理解上下文回答

### 测试表单校验
1. 管理后台 → 景区管理 → 新增大景区
2. 不填写任何字段直接点保存 → 应显示红色校验提示
3. 填写所有必填字段后保存 → 应成功

### 测试个人预约隔离
1. 用手机号 A 登录小程序 → 预约一个景区
2. 退出登录 → 用手机号 B 登录
3. 查看"我的预约" → 应显示"暂无预约记录"（不应看到 A 的预约）
