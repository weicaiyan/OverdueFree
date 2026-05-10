# 阿里巴巴Java开发手册 + ECC 统一规范

> 调用一次 `/java-standards`，全部生效。编写/修改/审查任何 Java 代码必须遵守。

---

## 一、编程规约

### 命名风格
- **【强制】** 类名 `UpperCamelCase`，方法名/变量名 `lowerCamelCase`，常量 `SCREAMING_SNAKE_CASE`
- **【强制】** 包名全小写，点分隔：`com.overduefree.module.lead`
- **【强制】** POJO 类布尔变量禁止加 `is` 前缀（MyBatis-Plus 解析问题）
- **【强制】** Service/DAO 层命名：`XxxController`、`XxxService`（接口）、`XxxServiceImpl`（实现）、`XxxMapper`
- **【推荐】** 抽象类以 `Abstract` 或 `Base` 开头，异常类以 `Exception` 结尾

### 代码格式
- **【强制】** 4 空格缩进，禁止 Tab
- **【强制】** `if`/`for`/`while` 即使单行也必须用大括号
- **【强制】** 单行 ≤ 120 字符，文件 ≤ 800 行，方法 ≤ 50 行
- **【强制】** 禁止魔法值，必须定义为常量或枚举

### OOP 规约
- **【强制】** 构造方法注入，禁止字段注入（`@Autowired` 字段）
- **【强制】** equals 调用方必须为非空对象：`"known".equals(unknown)`
- **【强制】** 禁止修改方法入参（不可变性原则）
- **【推荐】** 优先使用不可变对象，字段声明为 `final`
- **【推荐】** 返回防御性副本：`Collections.unmodifiableList()` 或 `new ArrayList<>(source)`

### 集合处理
- **【强制】** 集合转数组用 `list.toArray(new T[0])`
- **【强制】** `Arrays.asList()` 返回的集合不可修改
- **【强制】** 不要在 `foreach` 循环中 `remove`，用 `Iterator`
- **【推荐】** 集合初始化指定初始容量：`new ArrayList<>(expectedSize)`

### 控制语句
- **【强制】** switch 必须有 `default` 分支
- **【强制】** 嵌套不超过 4 层
- **【推荐】** 用卫语句减少嵌套

---

## 二、异常日志

### 异常处理
- **【强制】** 统一用 `BusinessException(ErrorCode.XXX, "中文消息")` 抛业务异常
- **【强制】** 禁止吞异常，不空的 catch 块必须处理或上抛
- **【强制】** Controller 不 try-catch 业务异常，交给 `GlobalExceptionHandler`
- **【推荐】** 错误码统一管理在 `ErrorCode.java` 常量类中

### 日志规约
- **【强制】** 使用 SLF4J：`private static final Logger log = LoggerFactory.getLogger(Xxx.class);`
- **【强制】** 严禁 `System.out.println`，生产禁止输出 debug
- **【强制】** 异常日志必须包含异常堆栈：`log.error("消息", e)`
- **【推荐】** 关键操作记录到 `operation_log` 表

---

## 三、MySQL 数据库

### 建表规约
- **【强制】** 表名/字段名小写+下划线：`lead_record`、`created_at`
- **【强制】** 必须有主键，`bigint unsigned auto_increment`
- **【强制】** 禁止外键，约束在应用层解决
- **【强制】** 字符集 `utf8mb4`，引擎 InnoDB
- **【强制】** 索引命名：唯一 `uk_`，普通 `idx_`

### SQL 编写
- **【强制】** 禁止 `SELECT *`，必须指定字段
- **【强制】** 用 PreparedStatement 或 MyBatis-Plus 参数绑定，防 SQL 注入
- **【强制】** `LIKE` 查询禁止左模糊：`LIKE 'keyword%'`
- **【推荐】** 分页查询必须有排序字段

### ORM 映射
- **【强制】** 用 MyBatis-Plus，不直接拼接 SQL 字符串
- **【强制】** 逻辑删除用 `@TableLogic` 配置
- **【强制】** `@TableName`、`@TableId` 必须标注

---

## 四、工程结构与分层

### 模块分包
```
com.overduefree.module.<name>/
  controller/   — 控制器（≤100行）
  service/      — 接口 + impl/
  mapper/       — MyBatis-Plus Mapper
  entity/       — 实体类（≤80行）
  dto/          — 请求/响应 DTO
```

### 分层约束
```
Controller → Service → Mapper
     ↓           ↓
   DTO        Entity
```
- **【强制】** Controller 不调 Mapper
- **【强制】** 模块 A 不直接调模块 B 的 Mapper，必须走 Service
- **【强制】** Service 接口 + 实现分离

### 代码行数硬上限

| 层级 | 最大行数 |
|------|---------|
| Controller | 100 |
| Service 接口 | 30 |
| Service 实现 | 300 |
| Entity | 80 |
| Mapper | 30 |

---

## 五、安全规约

### 认证授权
- **【强制】** 密码 BCrypt 加密存储，禁止明文或简单 MD5
- **【强制】** Token 只存 SHA-256 摘要，不存原始 token
- **【强制】** 权限在拦截器层校验，不在 Controller 里判断
- **【强制】** 后台管理接口必须验证管理员角色

### 数据安全
- **【强制】** 手机号不返回给 H5 客户端展示
- **【强制】** SQL 参数化查询，禁止拼接字符串
- **【强制】** 文件上传双重校验：扩展名 + Content-Type
- **【强制】** 禁止硬编码密码/密钥/Token 在 Java 代码中

### 输入校验
- **【强制】** Controller 层用 `@Valid` + Bean Validation 校验
- **【强制】** 手机号格式：中国大陆 11 位 `^1[3-9]\d{9}$`
- **【推荐】** 敏感配置从 `application.yml` 读取，用 `@ConfigurationProperties`

---

## 六、单元测试

### AIR 原则
- **【强制】** 自动化（Automatic）：全自动非交互式
- **【强制】** 独立性（Independent）：测试间不依赖，可单独运行
- **【强制】** 可重复（Repeatable）：任意环境多次运行结果一致

### 编写规范
- **【强制】** 必须断言，禁止用 `System.out` 验证
- **【强制】** 测试类命名：`XxxTest`，测试方法命名：`testXxx` 或 `should_Xxx_when_Yyy`
- **【推荐】** 单测覆盖率 ≥ 80%

---

## 七、Git 工作流

### 分支策略
- **【强制】** `master` 是稳定分支，不允许直接开发
- **【强制】** 功能从 `master` 拉分支：`feat/xxx`、`fix/xxx`、`chore/xxx`
- **【强制】** 合并到 `master` 前必须用户确认
- **【强制】** 禁止 force push 到 master

### 提交规范
```
<type>: <中文描述>
```
type: `feat`、`fix`、`chore`、`docs`、`test`、`refactor`

---

## 八、代码审查检查清单

- [ ] Controller 无业务逻辑（≤100行）
- [ ] Service 文件 ≤300 行，方法 ≤50 行
- [ ] 无硬编码密码/密钥/魔法值
- [ ] 无 SQL 字符串拼接
- [ ] 异常不吞掉，有日志
- [ ] 命名符合规范（camelCase / PascalCase / UPPER_SNAKE）
- [ ] 无死代码、无 `System.out`、无注释掉的大段代码
- [ ] 构造方法注入，非字段注入
- [ ] 跨模块调用走 Service 不走 Mapper

---

## 九、禁止事项速查

- [ ] 禁止 Controller 写业务逻辑
- [ ] 禁止字段注入 `@Autowired`
- [ ] 禁止 SQL 字符串拼接
- [ ] 禁止硬编码密码/密钥
- [ ] 禁止返回手机号给客户端
- [ ] 禁止 `SELECT *`
- [ ] 禁止 `System.out.println`
- [ ] 禁止吞异常
- [ ] 禁止跨模块直接调 Mapper
- [ ] 禁止在 master 上直接开发
- [ ] 禁止 force push 到 master
