# 第一轮安全修复说明文档

> 分支: `fix/security-round1`
> 修复日期: 2026-05-09
> 修复范围: 后端安全漏洞 + 小程序兼容性问题

---

## 修复清单

### 1. 移除 touristId 硬编码回退

**问题**: `ReserveController` 和 `ReserveRecordService` 中，当 JWT 认证信息缺失时，`touristId` 会静默回退到 `1L`，导致数据归属到用户1，存在数据泄露风险。

**修复内容**:

| 文件 | 修改 |
|------|------|
| `ReserveController.java` `addReserve()` | JWT 缺失时返回错误 `"请先登录后再预约"`，不再静默通过 |
| `ReserveController.java` `getMyList()` | JWT 缺失时返回错误 `"请先登录"`，不再回退到用户1 |
| `ReserveRecordService.java` `doReserve()` | `touristId` 为 null 时返回错误信息，不再默认设为 `1L` |

**影响**: 所有预约相关接口现在严格要求有效的 JWT token。

---

### 2. JWT 密钥外部化到配置文件

**问题**: JWT 签名密钥硬编码在 `JwtUtils.java` 源码中 (`XFS_SECRET_2026_GRADUATION_PROJECT`)，密钥短且可预测。

**修复内容**:

| 文件 | 修改 |
|------|------|
| `application.yml` | 新增 `xfs.jwt.secret` 和 `xfs.jwt.expiration` 配置项 |
| `JwtUtils.java` | 改用 `@Value` 注入配置，不再使用硬编码常量 |

**配置示例**:
```yaml
xfs:
  jwt:
    secret: ${JWT_SECRET:XFS_SECRET_2026_GRADUATION_PROJECT_DEFAULT}
    expiration: 604800000  # 7天
```

**使用方式**: 通过环境变量 `JWT_SECRET` 覆盖默认密钥，生产环境必须设置。

---

### 3. 管理员密码 BCrypt 加密

**问题**: 管理员密码以明文存储在数据库中，登录时直接字符串比较。

**修复内容**:

| 文件 | 修改 |
|------|------|
| `pom.xml` | 新增 `spring-security-crypto` 依赖 |
| `AdminController.java` | 使用 `BCryptPasswordEncoder.matches()` 校验密码 |
| `SysAdmin.java` | 密码字段添加 `@JsonIgnore`，API 响应不再返回密码 |
| `sql/xfs_ai_tourism_system.sql` | 管理员密码更新为 BCrypt 哈希值 |

**重要**: 修改后的数据库中，管理员默认密码仍为 `123456`，但存储形式变为 BCrypt 哈希。

**如何为新管理员生成密码哈希**:
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode("your_password");
// 将 hash 存入数据库 password 字段
```

---

### 4. 角色权限区分与接口保护

**问题**: 管理员和游客共用同一套 JWT，无角色区分。游客可以调用删除景区、核销门票等管理接口。

**修复内容**:

| 文件 | 修改 |
|------|------|
| `JwtUtils.java` | 新增 `generateToken(id, username, role)` 重载方法 |
| `AdminController.java` | 登录时生成带 `role=admin` 的 token |
| `TouristAuthController.java` | 登录时生成带 `role=tourist` 的 token |
| `LoginInterceptor.java` | 解析 token 后将 `role` 存入 request attribute |
| `AdminRequired.java` | **新增** 自定义注解，标记需要管理员权限的方法 |
| `AdminRoleInterceptor.java` | **新增** 拦截器，检查带 `@AdminRequired` 注解的接口 |
| `WebMvcConfig.java` | 注册 `AdminRoleInterceptor` |

**受保护的接口** (仅管理员可访问):

| 接口 | 说明 |
|------|------|
| `POST /api/area/save` | 新增/编辑景区 |
| `DELETE /api/area/{id}` | 删除景区 |
| `POST /api/area/spot/save` | 新增/编辑子景点 |
| `DELETE /api/area/spot/{id}` | 删除子景点 |
| `POST /api/reserve/verify/{code}` | 核销门票 |
| `GET /api/reserve/initStock` | 初始化库存 |
| `GET /api/stats/cards` | 统计卡片 |
| `GET /api/stats/areaHeat` | 景区热度 |
| `GET /api/stats/trend` | 预约趋势 |

**公开接口** (无需登录):

| 接口 | 说明 |
|------|------|
| `POST /api/admin/login` | 管理员登录 |
| `POST /api/tourist/login` | 游客登录 |
| `GET /api/area/list` | 景区列表 |
| `GET /api/area/hot` | 热门景区 |
| `GET /api/area/{id}/spots` | 子景点列表 |
| `POST /api/ai/chat` | AI 问答 |

---

### 5. 全局异常处理修复

**问题**: `GlobalExceptionHandler` 将 `e.getMessage()` 返回给前端，可能泄露 SQL 错误、内部类名等敏感信息。

**修复内容**:

| 文件 | 修改 |
|------|------|
| `GlobalExceptionHandler.java` | 移除 `e.getMessage()` 拼接，仅返回通用友好提示 |

---

### 6. 小程序 Web 组件替换

**问题**: `detail.vue` 使用了 Element Plus 的 `<el-icon>` 和原生 HTML `<h1>` 标签，微信小程序不支持。

**修复内容**:

| 文件 | 修改 |
|------|------|
| `detail.vue` | `<el-icon><Location /></el-icon>` → `<text class="location-icon">📍</text>` |
| `detail.vue` | `<h1 class="area-name">` → `<text class="area-name">` |

---

## 测试覆盖

新增 4 个测试文件，共覆盖 22 个测试用例:

| 测试文件 | 用例数 | 测试内容 |
|----------|--------|----------|
| `JwtRoleTest.java` | 5 | JWT 角色生成、解析、向后兼容、过期时间 |
| `BcryptPasswordTest.java` | 4 | BCrypt 加密匹配、错误密码拒绝、种子数据验证 |
| `AdminRoleInterceptorTest.java` | 8 | 无 token 401、管理员放行、游客 403、公开接口放行 |
| `ReserveSecurityTest.java` | 5 | 预约接口认证、我的列表认证、核销/库存需管理员 |

运行测试:
```bash
cd xfs-backend
mvn test -Dtest="BcryptPasswordTest,JwtRoleTest"
```

---

## Git 提交记录

```
6242f7d fix(security): remove touristId hardcoded fallback to 1L
1935d77 fix(security): externalize JWT secret to application.yml
03ec7e1 fix(security): BCrypt password encryption + role-based JWT
8f46fea feat(security): role-based access control + fix error handler
0a0a0c5 fix(miniapp): replace Web-only components with uni-app natives
43e88bf test(security): add tests for all round1 security fixes
```

---

## 后续待修复 (第二轮)

- [ ] 小程序 URL 硬编码 `http://localhost:8080`
- [ ] 管理后台无退出登录按钮
- [ ] 前端空 catch 块未处理错误
- [ ] 管理员用户名/角色硬编码显示
- [ ] 统计数据造假 (硬编码 + Math.random)
- [ ] 小程序登录逻辑重复代码
