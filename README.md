# 雪峰山智慧文旅系统 (XFS Smart Tourism System)

#### 🌟 项目简介
本项目是专为怀化雪峰山五大景区定制开发的智慧文旅预约导览系统。通过科技手段整合雪峰山丰富的自然与文化资源，为游客提供沉浸式的 AI 伴游体验，为管理者提供精准的数字化监控。

---

#### 🚀 今日核心里程碑 (2026-05-09)

1.  **AI 伴游系统 (核心亮点)**
    *   **深度集成**：接入真实 LLM 大模型 (DeepSeek/OpenAI 兼容协议)，告别 if-else 模拟。
    *   **人设注入**：打造“雪峰百事通”人设，提供专业导览、美食推荐及路线规划。
    *   **全局悬浮窗**：小程序端实现全局悬浮 AI 助手，支持平滑滚动与思考状态反馈，不中断用户浏览。

2.  **管理后台视觉重构 (现代化升级)**
    *   **布局革命**：抛弃传统布局，改用“现代简约云端风格”，包含悬浮侧边栏、玻璃拟态 Header 及超大圆角设计。
    *   **数据大屏**：引入 ECharts 构建“智慧监控中心”，实时展示 7 日预约趋势、景区热度玫瑰图及系统运行状态监控。
    *   **二层管理模式**：实现“大景区 + 子景点”二级联动管理，支持通过侧滑抽屉精细化管理景区内容。

3.  **安全鉴权体系 (工业级保障)**
    *   **JWT 令牌**：实现基于 JSON Web Token 的全流程鉴权。
    *   **前后端联动**：后端 LoginInterceptor 全局拦截，前端 Axios 拦截器自动注入 Token，路由守卫锁死越权访问。

4.  **数据自动化资产**
    *   **提取手册**：编写自动化脚本从官方文章中精准匹配景点名称与高清图 URL，生成《雪峰山数据导入手册》。
    *   **自动化测试**：新增 `AiIntegrationTest`、`StatisticsApiTest`、`ScenicCrudApiTest` 及 `JwtSecurityTest` 四大测试套件。

---

#### 🛠️ 技术栈
-   **后端**：Spring Boot 2.7.18 + MyBatis-Plus + Redis + Druid + JJWT + Hutool
-   **管理端**：Vue 3 + Vite + Element Plus + ECharts + Axios
-   **游客端**：UniApp (Vue 3)

---

#### 📂 目录结构
-   `xfs-backend`: Spring Boot 后端核心源码
-   `xfs-admin`: Vue 3 管理后台前端
-   `xfs-miniapp`: UniApp 小程序源码
-   `sql`: 数据库初始化脚本
-   `src`: 雪峰山官方图文素材与导入手册

---

#### 🧪 运行与测试
1.  **后端**：在 IDEA 中配置环境变量 `AI_API_KEY`，启动 `XfsBackendApplication`。
2.  **测试**：运行 `src/test` 下的所有测试类，确保业务逻辑全绿。
3.  **前端**：进入 `xfs-admin` 目录执行 `npm run dev`。
4.  **小程序**：使用 HBuilderX 打开 `xfs-miniapp` 并运行到微信开发者工具。

---
© 2026 雪峰山智慧文旅项目组 | 毕业设计项目
