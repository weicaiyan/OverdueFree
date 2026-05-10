# OverdueFree 后端开发规范

> 本文件自动加载，指导所有后端 Java 代码的编写和修改。

---

## 0. 架构路线

**传统三层 + 吸收 DDD 关键原则**。不搞全量 DDD 四层（第一版过重），但核心原则必须遵守。

## 1. 包结构约定

```
com.overduefree
  OverdueFreeApplication.java
  common/           — 通用类：ApiResponse、ErrorCode、BusinessException、GlobalExceptionHandler、PageResult
  config/           — 配置类：WebConfig、UploadProperties、AuthProperties、SecurityConfig
  module/           — 业务模块（按领域分包）
    admin/          — 管理员模块（controller / service / mapper / entity）
    customer/       — 客户模块
    lead/           — 线索模块
    event/          — 行为事件模块
    asset/          — 素材模块
    article/        — 资讯模块
    caseitem/       — 成功案例模块
    export/         — 导出模块
    file/           — 文件上传模块
    configitem/     — 配置项模块
    operationlog/   — 操作日志模块
```

每个业务模块内部结构：

```
module/<name>/
  controller/   — 控制器
  service/      — 业务接口 + impl/
  mapper/       — MyBatis-Plus Mapper
  entity/       — 实体类
  dto/          — 请求/响应 DTO（如有）
```

## 2. 分层约束（严格单向依赖）

```
Controller → Service → Mapper
     ↓           ↓
   DTO         Entity
```

**禁止**：
- Controller 直接调用 Mapper
- Controller 包含业务逻辑
- 模块 A 的 Mapper 被模块 B 的 Service 直接调用（跨模块必须走 Service）

## 3. Controller 层规范

### 必须遵守
1. **只做三件事**：参数校验 → 调用 Service → 封装响应
2. 每个方法不超过 **30 行**
3. 用 `@Valid` 做参数校验，不手写 if-else 校验
4. 统一返回 `ApiResponse<T>`，不返回原始 String/int
5. **绝对不写业务逻辑**（if-else 业务判断、金额计算、状态流转等）

### 命名规则
- 客户 H5 接口：`/api/app/` + 资源名
- 后台管理接口：`/api/admin/` + 资源名
- 类名：`<模块>Controller`，例如 `CustomerAuthController`

### 示例
```java
@RestController
@RequestMapping("/api/app/auth")
public class CustomerAuthController {
    
    private final CustomerAuthService authService;
    
    @PostMapping("/send-code")
    public ApiResponse<SendCodeResult> sendCode(@Valid @RequestBody SendCodeRequest req) {
        return ApiResponse.ok(authService.sendCode(req.getPhone()));
    }
}
```

## 4. Service 层规范

### 必须遵守
1. **接口 + 实现分离**：`XxxService`（接口）+ `impl/XxxServiceImpl`（实现）
2. 业务规则校验放在 Service 方法开头，用 `BusinessException` 抛出
3. 跨模块调用只调 Service 接口，不调 Mapper
4. 需要事务的方法加 `@Transactional`
5. 每个 Service 只操作自己聚合内的实体

### 命名规则
- 接口：`<模块>Service`
- 实现：`impl/<模块>ServiceImpl`
- 查询方法：`getXxx()`、`listXxx()`、`pageXxx()`
- 变更方法：`createXxx()`、`updateXxx()`、`deleteXxx()`
- 业务方法：动词开头，如 `sendVerificationCode()`、`validateAndLogin()`

## 5. Entity 规范（贫血防治）

### 必须遵守
1. 用 MyBatis-Plus 注解：`@TableName`、`@TableId`、`@TableField`
2. 使用 `@Data`（Lombok）但**不要对所有字段开放 setter**
3. 状态判断方法放在实体上，不放 Service：
   ```java
   public boolean isActive() { return "ACTIVE".equals(this.status); }
   public boolean canBeDisabled() { return !"BOSS".equals(this.role); }
   ```
4. 简单业务规则（不依赖外部）放实体方法里
5. 复杂业务规则（依赖其他数据）放 Service 里

### 枚举字段
- 所有枚举值用 String 常量定义在实体类中，如：
  ```java
  public static final String ROLE_BOSS = "BOSS";
  public static final String ROLE_ADMIN = "ADMIN";
  ```

## 6. 异常处理规范

1. 业务异常统一用 `BusinessException(ErrorCode.XXX, "中文提示")` 抛出
2. 不要在 Controller 里 try-catch 业务异常，交给 `GlobalExceptionHandler`
3. 只在需要特殊处理的地方 try-catch（如文件清理）
4. 异常消息用中文，面向开发者可读

## 7. 代码质量硬性指标

| 层级 | 单文件最大行数 |
|------|-------------|
| Controller | ≤ 100 行 |
| Service 接口 | ≤ 30 行 |
| Service 实现 | ≤ 300 行 |
| Entity | ≤ 80 行 |
| Mapper | ≤ 30 行 |

## 8. 安全规范（绝对红线）

1. **密码必须 BCrypt 加密存储**，禁止明文或简单哈希
2. **Token 只保存 SHA-256 摘要**，不保存原始 token
3. **手机号不返回给前端展示**（`/api/app/me` 只返回 `loggedIn: true`）
4. **不硬编码密码**在 Java 代码中，从配置文件读取
5. 后台管理接口必须校验管理员权限（通过拦截器）

## 9. 注释规范

1. **不要写解释代码在干什么的注释** — 方法名和变量名已经说明
2. **只在 WHY 非显而易见时写注释** — 隐藏约束、微妙规则、临时方案
3. 不要写 Javadoc 多行注释块
4. 不要写"创建人"、"创建时间"等签名注释

## 10. 禁止事项速查

- [ ] 禁止 Controller 写业务逻辑
- [ ] 禁止跨模块直接调 Mapper
- [ ] 禁止硬编码密码/密钥
- [ ] 禁止返回手机号给客户端
- [ ] 禁止在代码里写"作者"、"创建日期"注释
- [ ] 禁止 try-catch 吞掉异常不处理
- [ ] 禁止写超过 300 行的 Service 实现
- [ ] 禁止在 Mapper 里写业务逻辑
- [ ] 禁止滥用 `@Data` 的 setter 破坏封装
