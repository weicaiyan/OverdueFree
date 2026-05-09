# OverdueFree AI 开发实施规格书

> 目标读者：Claude Code、DeepSeek、其他代码生成/执行 Agent。
>
> 使用方式：拿到本文档后，可以直接按本文档进行 Git 初始化、数据库设计、后端开发、前端开发、后台开发、本地启动和验收。除非遇到本文档明确标记为“必须询问用户”的问题，否则不要反复询问产品细节，按本文档默认值执行。

## 0. 绝对约束

### 0.1 禁止事项

1. 未经用户明确允许，不要把功能分支合并进 `master`。
2. 不要直接在 `master` 上开发业务功能。
3. 不要引入 Redis，第一版不接入 Redis。
4. 不要接入真实短信服务，第一版使用模拟验证码。
5. 不要接入真实大模型问答，第一版 AI 咨询是固定流程留资。
6. 不要写“保证减免”“保证停催”“保证延期”等绝对承诺文案。
7. 不要使用对标 App 的备案号、邮箱、公司名、真实案例等识别信息。
8. 不要擅自改技术栈：后端必须 Java + Spring Boot + JDK 11 + Maven + MySQL。
9. 不要将上传文件放进代码目录内，必须使用外部可配置目录。
10. 不要把默认密码明文硬编码在 Java 代码里。

### 0.2 必须遵守

1. `master` 是稳定分支，只允许放测试通过、可演示、可上线的代码。
2. 每个功能从 `master` 拉分支开发。
3. 合并回 `master` 前必须向用户确认。
4. 管理员密码必须 BCrypt 加密存储。
5. 客户登录态和管理员登录态必须使用服务端生成 token。
6. 后台上传、导出、管理员管理接口必须做权限校验。
7. 前台用户首次进入 H5 必须手机号登录。
8. 用户只登录未提交资料，也必须能在后台看到并导出。
9. 用户查看企业微信二维码必须记录行为。
10. 后台 Excel 导出必须支持筛选和自定义字段。

## 1. 项目基本信息

- 项目名：`OverdueFree`
- 前端应用名：`逾期上岸`
- 后端包名：`com.overduefree`
- 目标：手机 H5 获客产品 + PC 后台管理
- 用户端：手机 H5
- 后台端：PC Web 管理后台
- 本地演示系统：Windows
- 数据库：Docker MySQL
- 第一版不购买服务器，全部在用户电脑本地演示

## 2. 推荐仓库结构

必须使用单仓库，建议结构如下：

```text
OverdueFree/
  backend/
    pom.xml
    src/main/java/com/overduefree/
    src/main/resources/
      application.yml
      application-dev.yml
      db/migration/
  frontend-h5/
    package.json
    src/
    pages.json
    manifest.json
  admin-web/
    package.json
    src/
  docker/
    docker-compose.yml
  docs/
    PRD-债务逾期处理H5.md
    AI-IMPLEMENTATION-SPEC.md
  README.md
```

说明：

1. `frontend-h5` 使用 `uni-app + Vue3`，用于客户手机 H5。
2. `admin-web` 使用 `Vue3 + Vite + Element Plus`，用于后台 PC 管理。
3. `backend` 使用 `Spring Boot 2.7.x + Maven + JDK 11`。
4. `docker` 只负责本地 MySQL。

## 3. Git 执行规范

### 3.1 初始化规则

执行前先检查：

```bash
git status
git branch --show-current
```

如果不是 Git 仓库：

```bash
git init
git checkout -b master
```

如果已经是 Git 仓库但当前分支不是 `master`，先确认是否需要切回 `master`。不要删除用户已有改动。

### 3.2 分支策略

所有功能从 `master` 拉分支：

```bash
git checkout master
git checkout -b chore/project-bootstrap
```

建议分支顺序：

1. `chore/project-bootstrap`：项目骨架、README、Docker MySQL、基础配置。
2. `feat/backend-foundation`：后端基础工程、数据库迁移、通用响应、异常处理。
3. `feat/backend-auth`：客户手机号登录、管理员登录、token。
4. `feat/backend-leads`：客户、线索、行为记录。
5. `feat/backend-content-assets`：素材、视频、资讯、案例、上传。
6. `feat/backend-export`：后台筛选和 Excel 导出。
7. `feat/frontend-h5-foundation`：H5 登录、首页、Tab、基础接口。
8. `feat/frontend-h5-leads`：AI 留资、规划评估、计算器、二维码行为。
9. `feat/admin-web-foundation`：后台登录、布局、权限。
10. `feat/admin-web-management`：线索、导出、素材、资讯、案例、管理员管理。
11. `feat/polish-local-demo`：视觉优化、联调、README、本地演示脚本。

### 3.3 提交规范

提交信息格式：

```text
<type>: <中文描述>
```

常用 type：

- `feat`：新功能
- `fix`：修复
- `chore`：工程配置
- `docs`：文档
- `test`：测试
- `refactor`：重构

示例：

```bash
git commit -m "feat: 新增客户手机号模拟验证码登录"
```

### 3.4 合并规则

功能分支测试通过后，不要自动合并。必须输出：

1. 当前分支完成了什么。
2. 修改了哪些关键文件。
3. 运行了哪些测试。
4. 是否建议合并到 `master`。

等待用户明确确认后，才能执行：

```bash
git checkout master
git merge --no-ff <branch-name>
```

## 4. 技术选型

### 4.1 后端

- Java：JDK 11
- Spring Boot：2.7.x
- 构建：Maven
- 数据库：MySQL 8
- ORM：MyBatis-Plus
- 数据库迁移：Flyway
- Excel：EasyExcel
- 密码加密：Spring Security Crypto BCrypt
- JSON：Jackson
- 参数校验：Hibernate Validator
- 文件上传：Spring Multipart
- Redis：第一版不接入

后端依赖建议：

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  <dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.5</version>
  </dependency>
  <dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
  </dependency>
  <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.4</version>
  </dependency>
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>
```

### 4.2 客户 H5 前端

- 框架：uni-app + Vue3
- 语言：TypeScript 优先
- 状态：轻量本地 store，可用 Pinia
- 样式：SCSS
- 目标：先发布 H5，保留未来小程序/App 扩展能力

### 4.3 后台 Web

- 框架：Vue3 + Vite + TypeScript
- UI：Element Plus
- Excel 导出：直接调用后端导出接口
- 目标：PC 浏览器管理后台

## 5. 本地环境与端口

默认端口：

| 服务 | 端口 |
| --- | --- |
| 后端 Spring Boot | 8080 |
| 客户 H5 dev server | 5173 |
| 后台 Web dev server | 5174 |
| MySQL Docker | 3306 |

Docker MySQL 建议配置：

```yaml
version: "3.8"

services:
  mysql:
    image: mysql:8.0
    container_name: overduefree-mysql
    restart: unless-stopped
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: overduefree_root
      MYSQL_DATABASE: overdue_free
      MYSQL_USER: overduefree
      MYSQL_PASSWORD: overduefree_pwd
      TZ: Asia/Shanghai
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
      - --default-time-zone=+08:00
    volumes:
      - overduefree_mysql_data:/var/lib/mysql

volumes:
  overduefree_mysql_data:
```

后端开发数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/overdue_free?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: overduefree
    password: overduefree_pwd
```

## 6. 后端工程设计

### 6.1 包结构

```text
com.overduefree
  OverdueFreeApplication.java
  common/
    ApiResponse.java
    ErrorCode.java
    BusinessException.java
    GlobalExceptionHandler.java
    PageResult.java
  config/
    WebConfig.java
    UploadProperties.java
    AuthProperties.java
  auth/
    AuthTokenService.java
    CustomerAuthInterceptor.java
    AdminAuthInterceptor.java
    CurrentCustomer.java
    CurrentAdmin.java
  module/
    admin/
    customer/
    lead/
    event/
    asset/
    article/
    caseitem/
    export/
    file/
    configitem/
    operationlog/
```

### 6.2 API 响应格式

所有 JSON API 使用统一格式：

```json
{
  "code": 0,
  "message": "OK",
  "data": {}
}
```

分页返回：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "list": [],
    "page": 1,
    "pageSize": 20,
    "total": 0
  }
}
```

错误示例：

```json
{
  "code": 40001,
  "message": "请先登录",
  "data": null
}
```

### 6.3 认证规则

客户 token：

1. 客户手机号验证码登录成功后生成随机 token。
2. 后端只保存 token 的 SHA-256 摘要，不保存明文 token。
3. 前端保存明文 token。
4. 请求头使用：`Authorization: Bearer <token>`。
5. 默认有效期 7 天，从数据库配置项读取。

管理员 token：

1. 管理员账号密码登录成功后生成随机 token。
2. 后端只保存 token SHA-256 摘要。
3. 默认有效期 8 小时。
4. 请求头使用：`Authorization: Bearer <token>`。

## 7. 数据库设计

### 7.1 数据库命名

- 数据库：`overdue_free`
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_unicode_ci`
- 所有表使用 InnoDB
- 时间字段使用 `datetime`
- 主键使用 `bigint unsigned auto_increment`

### 7.2 枚举约定

管理员角色：

- `BOSS`
- `ADMIN`

账号状态：

- `ACTIVE`
- `DISABLED`

债务类型：

- `ONLINE_LOAN`：网贷
- `CREDIT_CARD`：信用卡
- `CREDIT_LOAN`：信用贷
- `CAR_LOAN`：车贷
- `OTHER`：其他

线索来源：

- `AI_CHAT`：AI 咨询
- `PLAN_ASSESSMENT`：规划评估

行为类型：

- `VIEW_WECHAT_QR`
- `USE_CALCULATOR`
- `CLICK_HOME_ENTRY`
- `CASE_APPLY`
- `ARTICLE_CTA`

素材 key：

- `HOME_VIDEO`
- `HOME_VIDEO_COVER`
- `AI_CONSULT_BANNER`
- `LOAN_CALCULATOR_BANNER`
- `DEBT_PLAN_BANNER`
- `WECHAT_QR`

内容状态：

- `DRAFT`
- `PUBLISHED`
- `OFFLINE`

### 7.3 DDL 建议

将以下 SQL 放入：

```text
backend/src/main/resources/db/migration/V1__init_schema.sql
```

```sql
CREATE TABLE sys_config (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  config_key VARCHAR(100) NOT NULL,
  config_value VARCHAR(500) NOT NULL,
  description VARCHAR(255) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE admin_user (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  display_name VARCHAR(50) NOT NULL,
  role VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  last_login_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_user_username (username),
  KEY idx_admin_user_role (role),
  KEY idx_admin_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE admin_session (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  admin_id BIGINT UNSIGNED NOT NULL,
  token_hash CHAR(64) NOT NULL,
  expires_at DATETIME NOT NULL,
  revoked_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_session_token_hash (token_hash),
  KEY idx_admin_session_admin_id (admin_id),
  KEY idx_admin_session_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE customer (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  phone VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  first_login_at DATETIME NOT NULL,
  last_login_at DATETIME NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_customer_phone (phone),
  KEY idx_customer_last_login_at (last_login_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE customer_session (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  customer_id BIGINT UNSIGNED NOT NULL,
  token_hash CHAR(64) NOT NULL,
  expires_at DATETIME NOT NULL,
  revoked_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_customer_session_token_hash (token_hash),
  KEY idx_customer_session_customer_id (customer_id),
  KEY idx_customer_session_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mock_verification_code (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  phone VARCHAR(20) NOT NULL,
  code VARCHAR(10) NOT NULL,
  scene VARCHAR(30) NOT NULL DEFAULT 'CUSTOMER_LOGIN',
  consumed_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_mock_code_phone_scene (phone, scene),
  KEY idx_mock_code_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lead_record (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  customer_id BIGINT UNSIGNED NOT NULL,
  source VARCHAR(30) NOT NULL,
  surname VARCHAR(50) NOT NULL,
  region VARCHAR(100) NOT NULL,
  debt_amount DECIMAL(12,2) NOT NULL,
  debt_type VARCHAR(30) NOT NULL,
  debt_description TEXT DEFAULT NULL,
  age_range VARCHAR(30) DEFAULT NULL,
  job_status VARCHAR(50) DEFAULT NULL,
  credit_status VARCHAR(50) DEFAULT NULL,
  monthly_income_range VARCHAR(50) DEFAULT NULL,
  monthly_expense_range VARCHAR(50) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_lead_customer_id (customer_id),
  KEY idx_lead_source (source),
  KEY idx_lead_debt_type (debt_type),
  KEY idx_lead_region (region),
  KEY idx_lead_created_at (created_at),
  KEY idx_lead_debt_amount (debt_amount)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE customer_event (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  customer_id BIGINT UNSIGNED NOT NULL,
  event_type VARCHAR(50) NOT NULL,
  source_page VARCHAR(100) DEFAULT NULL,
  ref_type VARCHAR(50) DEFAULT NULL,
  ref_id BIGINT UNSIGNED DEFAULT NULL,
  metadata_json JSON DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_event_customer_id (customer_id),
  KEY idx_event_type (event_type),
  KEY idx_event_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE asset_resource (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  asset_key VARCHAR(80) NOT NULL,
  title VARCHAR(100) NOT NULL,
  file_url VARCHAR(500) DEFAULT NULL,
  original_file_name VARCHAR(255) DEFAULT NULL,
  mime_type VARCHAR(100) DEFAULT NULL,
  file_size BIGINT UNSIGNED DEFAULT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  updated_by BIGINT UNSIGNED DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_asset_key (asset_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE article (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  cover_url VARCHAR(500) DEFAULT NULL,
  summary VARCHAR(500) DEFAULT NULL,
  content_text TEXT DEFAULT NULL,
  publish_time DATETIME DEFAULT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  deleted TINYINT NOT NULL DEFAULT 0,
  created_by BIGINT UNSIGNED DEFAULT NULL,
  updated_by BIGINT UNSIGNED DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_article_status_deleted_sort (status, deleted, sort_order),
  KEY idx_article_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE success_case (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  display_name VARCHAR(50) NOT NULL,
  masked_phone VARCHAR(30) DEFAULT NULL,
  overdue_platforms VARCHAR(500) DEFAULT NULL,
  overdue_amount DECIMAL(12,2) DEFAULT NULL,
  handling_plan VARCHAR(500) DEFAULT NULL,
  avatar_url VARCHAR(500) DEFAULT NULL,
  detail_text TEXT DEFAULT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  deleted TINYINT NOT NULL DEFAULT 0,
  created_by BIGINT UNSIGNED DEFAULT NULL,
  updated_by BIGINT UNSIGNED DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_case_status_deleted_sort (status, deleted, sort_order),
  KEY idx_case_overdue_amount (overdue_amount)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE operation_log (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  admin_id BIGINT UNSIGNED DEFAULT NULL,
  action VARCHAR(80) NOT NULL,
  target_type VARCHAR(80) DEFAULT NULL,
  target_id BIGINT UNSIGNED DEFAULT NULL,
  ip VARCHAR(64) DEFAULT NULL,
  user_agent VARCHAR(500) DEFAULT NULL,
  detail_json JSON DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_operation_admin_id (admin_id),
  KEY idx_operation_action (action),
  KEY idx_operation_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 7.4 初始化数据规则

启动时用 Java 初始化器完成，不要在 SQL 里写死 BCrypt 密码。

启动初始化逻辑：

1. 如果 `sys_config.customer_login_expire_days` 不存在，插入 `7`。
2. 如果没有任何 `admin_user`，创建默认老板账号。
3. 如果 `asset_resource` 缺少固定 `asset_key`，插入占位记录。
4. 如果资讯表为空，插入 3-5 条演示资讯。
5. 如果成功案例表为空，插入 3-5 条演示案例。
6. 已存在数据时绝不覆盖。

默认老板账号建议：

- username：`boss`
- password：从配置读取，配置 key 建议为 `app.init.boss-password`
- displayName：`老板`
- role：`BOSS`

本地开发 `application-dev.yml` 可以给出默认密码，但不要提交真实生产密码。

## 8. 后端接口设计

### 8.1 客户端认证

#### 发送模拟验证码

`POST /api/app/auth/send-code`

请求：

```json
{
  "phone": "13800000000"
}
```

响应：

```json
{
  "mockCode": "123456"
}
```

要求：

1. 校验中国大陆 11 位手机号。
2. 生成 6 位数字验证码。
3. 保存到 `mock_verification_code`。
4. 直接返回验证码，方便演示。

#### 客户登录

`POST /api/app/auth/login`

请求：

```json
{
  "phone": "13800000000",
  "code": "123456"
}
```

响应：

```json
{
  "token": "raw-token",
  "expiresAt": "2026-05-17 10:00:00"
}
```

要求：

1. 校验最近一次未使用验证码。
2. 登录成功后创建或更新 `customer`。
3. 更新 `last_login_at`。
4. 创建 `customer_session`。
5. 返回 token。

#### 客户退出

`POST /api/app/auth/logout`

要求：当前 token 对应 session 写入 `revoked_at`。

#### 当前客户

`GET /api/app/me`

响应只需要：

```json
{
  "loggedIn": true
}
```

不要返回手机号给前端展示。

### 8.2 客户端内容接口

#### 首页数据

`GET /api/app/home`

返回：

```json
{
  "assets": {
    "homeVideo": {},
    "homeVideoCover": {},
    "aiConsultBanner": {},
    "loanCalculatorBanner": {},
    "debtPlanBanner": {},
    "wechatQr": {}
  },
  "serviceSteps": [
    {"title": "提交信息", "description": "信息绝对安全加密处理"},
    {"title": "初步评估", "description": "结合情况进行初步判断"},
    {"title": "人工沟通", "description": "专人回访进一步确认"},
    {"title": "制定建议", "description": "根据情况给出处理建议"}
  ]
}
```

#### 资讯列表

`GET /api/app/articles?page=1&pageSize=10`

只返回 `PUBLISHED` 且未删除内容。

#### 资讯详情

`GET /api/app/articles/{id}`

#### 成功案例列表

`GET /api/app/cases?page=1&pageSize=10`

只返回 `PUBLISHED` 且未删除内容。

#### 成功案例详情

`GET /api/app/cases/{id}`

### 8.3 客户端线索接口

#### 提交线索

`POST /api/app/leads`

请求：

```json
{
  "source": "AI_CHAT",
  "surname": "张先生",
  "region": "重庆",
  "debtAmount": 50000,
  "debtType": "ONLINE_LOAN",
  "debtDescription": "多个网贷平台逾期，希望协商延期",
  "ageRange": "26-35岁",
  "jobStatus": "稳定工作",
  "creditStatus": "一般",
  "monthlyIncomeRange": "5000-8000",
  "monthlyExpenseRange": "2000-3000"
}
```

必填：

1. `source`
2. `surname`
3. `region`
4. `debtAmount`
5. `debtType`

手机号从当前登录客户读取，不从请求体读取。

响应：

```json
{
  "leadId": 10001
}
```

### 8.4 客户端行为接口

`POST /api/app/events`

请求：

```json
{
  "eventType": "VIEW_WECHAT_QR",
  "sourcePage": "HOME",
  "refType": "ASSET",
  "refId": null,
  "metadata": {
    "buttonText": "领取债务减免延期方案"
  }
}
```

要求：

1. 必须登录。
2. 记录到 `customer_event`。
3. `metadata` 可以序列化为 JSON。

### 8.5 后台认证

#### 管理员登录

`POST /api/admin/auth/login`

请求：

```json
{
  "username": "boss",
  "password": "password"
}
```

响应：

```json
{
  "token": "raw-token",
  "role": "BOSS",
  "displayName": "老板",
  "expiresAt": "2026-05-10 18:00:00"
}
```

#### 管理员退出

`POST /api/admin/auth/logout`

#### 当前管理员

`GET /api/admin/me`

### 8.6 后台管理员管理

只有 `BOSS` 可用。

#### 管理员列表

`GET /api/admin/admin-users`

#### 新增管理员

`POST /api/admin/admin-users`

请求：

```json
{
  "username": "admin1",
  "password": "123456",
  "displayName": "管理员1",
  "role": "ADMIN"
}
```

第一版只允许创建 `ADMIN`，不要通过接口创建第二个 `BOSS`，除非以后明确需要。

#### 禁用管理员

`PATCH /api/admin/admin-users/{id}/disable`

#### 启用管理员

`PATCH /api/admin/admin-users/{id}/enable`

#### 重置密码

`PATCH /api/admin/admin-users/{id}/password`

请求：

```json
{
  "newPassword": "123456"
}
```

### 8.7 后台线索管理

#### 线索列表

`GET /api/admin/leads`

查询参数：

| 参数 | 说明 |
| --- | --- |
| keyword | 手机号或称呼 |
| region | 地区 |
| leadType | `LOGIN_ONLY` 或 `SUBMITTED` |
| debtType | 债务类型 |
| minDebtAmount | 最小债务金额 |
| maxDebtAmount | 最大债务金额 |
| viewedWechatQr | true/false |
| startTime | 开始时间 |
| endTime | 结束时间 |
| page | 页码 |
| pageSize | 每页数量 |

返回列表字段：

```json
{
  "rowType": "SUBMITTED",
  "customerId": 1,
  "leadId": 10,
  "phone": "13800000000",
  "surname": "张先生",
  "region": "重庆",
  "debtAmount": 50000,
  "debtType": "ONLINE_LOAN",
  "source": "AI_CHAT",
  "firstLoginAt": "2026-05-10 10:00:00",
  "leadCreatedAt": "2026-05-10 10:05:00",
  "viewedWechatQr": true,
  "lastWechatQrViewAt": "2026-05-10 10:06:00",
  "latestEventType": "VIEW_WECHAT_QR",
  "latestEventAt": "2026-05-10 10:06:00",
  "historyCount": 2
}
```

仅登录客户：

```json
{
  "rowType": "LOGIN_ONLY",
  "customerId": 1,
  "leadId": null,
  "phone": "13800000000",
  "surname": null,
  "firstLoginAt": "2026-05-10 10:00:00"
}
```

实现建议：

1. `LOGIN_ONLY` 行来自 `customer` 表中没有 lead 的客户。
2. `SUBMITTED` 行来自 `lead_record`。
3. 列表可用 SQL union 或服务层组装。

#### 客户历史

`GET /api/admin/customers/{customerId}/history`

返回该客户：

1. 基本登录信息。
2. 历史线索列表。
3. 行为记录列表。

### 8.8 Excel 导出

`POST /api/admin/leads/export`

请求：

```json
{
  "filters": {
    "keyword": "138",
    "leadType": "SUBMITTED",
    "viewedWechatQr": true
  },
  "fields": [
    "phone",
    "surname",
    "region",
    "debtAmount",
    "debtType",
    "debtDescription",
    "leadCreatedAt",
    "firstLoginAt",
    "latestEventType",
    "viewedWechatQr",
    "lastWechatQrViewAt"
  ]
}
```

响应：Excel 文件流。

文件名：

```text
线索导出-yyyyMMdd.xlsx
```

要求：

1. 导出前按 filters 筛选。
2. fields 为空时默认全字段。
3. 导出操作写入 `operation_log`。

### 8.9 文件上传

`POST /api/admin/files/upload`

请求：`multipart/form-data`

参数：

| 参数 | 说明 |
| --- | --- |
| file | 文件 |
| category | `IMAGE` 或 `VIDEO` |

限制：

1. 图片：jpg、jpeg、png、webp，最大 20MB。
2. 视频：mp4，最大 500MB。
3. 通过扩展名和 content-type 双重校验。
4. 保存到外部上传目录。
5. 返回可访问 URL。

响应：

```json
{
  "url": "/uploads/2026/05/10/uuid.mp4",
  "originalFileName": "demo.mp4",
  "mimeType": "video/mp4",
  "fileSize": 123456
}
```

### 8.10 素材管理

#### 获取素材

`GET /api/admin/assets`

#### 更新素材

`PUT /api/admin/assets/{assetKey}`

请求：

```json
{
  "title": "AI债务咨询师",
  "fileUrl": "/uploads/xxx.png",
  "originalFileName": "ai.png",
  "mimeType": "image/png",
  "fileSize": 1000
}
```

固定 assetKey：

1. `HOME_VIDEO`
2. `HOME_VIDEO_COVER`
3. `AI_CONSULT_BANNER`
4. `LOAN_CALCULATOR_BANNER`
5. `DEBT_PLAN_BANNER`
6. `WECHAT_QR`

替换素材时旧文件先保留，不自动删除。

### 8.11 资讯管理

后台接口：

1. `GET /api/admin/articles`
2. `POST /api/admin/articles`
3. `PUT /api/admin/articles/{id}`
4. `DELETE /api/admin/articles/{id}`
5. `PATCH /api/admin/articles/{id}/publish`
6. `PATCH /api/admin/articles/{id}/offline`

删除为逻辑删除。

### 8.12 成功案例管理

后台接口：

1. `GET /api/admin/cases`
2. `POST /api/admin/cases`
3. `PUT /api/admin/cases/{id}`
4. `DELETE /api/admin/cases/{id}`
5. `PATCH /api/admin/cases/{id}/publish`
6. `PATCH /api/admin/cases/{id}/offline`

删除为逻辑删除。

## 9. 前台 H5 页面规格

### 9.1 路由建议

```text
pages/login/index
pages/home/index
pages/cases/index
pages/cases/detail
pages/articles/index
pages/articles/detail
pages/profile/index
pages/ai-chat/index
pages/plan-form/index
pages/calculator/index
```

### 9.2 登录页

流程：

1. 输入手机号。
2. 点击获取验证码。
3. 页面显示“本次验证码：xxxxxx”。
4. 输入验证码。
5. 点击登录。
6. 登录成功进入主页。

样式：

1. 手机 H5 居中布局。
2. 红色主按钮。
3. 页面不要过度营销。

### 9.3 全局登录拦截

1. 除登录页外，所有页面进入前检查本地 token。
2. 本地没有 token，跳登录页。
3. 本地有 token，调用 `/api/app/me` 校验。
4. token 失效时清理本地 token 并跳登录页。

### 9.4 首页

按照对标截图结构：

1. 顶部标题：`逾期上岸`
2. 顶部视频卡片。
3. 两个横向入口：AI 债务咨询师、网贷利率计算器。
4. 一个大图入口：规划优化债务。
5. 服务流程 4 步。
6. 底部固定按钮：`领取债务减免延期方案`。

点击规则：

1. 视频卡片：有视频则弹窗播放，无视频则提示。
2. AI 债务咨询师：进入聊天页。
3. 网贷利率计算器：进入计算器页。
4. 规划优化债务：进入结构化评估页。
5. 底部按钮：弹企业微信二维码，记录 `VIEW_WECHAT_QR`。

### 9.5 AI 留资聊天页

固定步骤：

1. 系统欢迎。
2. 用户输入姓氏。
3. 用户输入地区。
4. 用户输入债务金额。
5. 用户选择债务类型。
6. 用户输入补充描述。
7. 提交 `/api/app/leads`。
8. 显示提交成功。
9. 弹企业微信二维码并记录行为。

债务类型：

1. 网贷：`ONLINE_LOAN`
2. 信用卡：`CREDIT_CARD`
3. 信用贷：`CREDIT_LOAN`
4. 车贷：`CAR_LOAN`
5. 其他：`OTHER`

### 9.6 规划评估页

必填：

1. 姓氏
2. 地区
3. 债务金额
4. 债务类型

选填：

1. 年龄段：`18-25岁`、`26-35岁`、`36-45岁`、`45以上`
2. 工作情况：`稳定工作`、`无业`、`临时工`、`灵活就业`、`退休`
3. 征信情况：`良好`、`一般`、`较差`
4. 月收入：`小于2000`、`2000-5000`、`5000-8000`、`8000以上`
5. 月开销：`小于1000`、`1000-2000`、`2000-3000`、`3000以上`
6. 补充描述

提交后：

1. 创建来源为 `PLAN_ASSESSMENT` 的线索。
2. 提示“已收到，人工将结合情况评估”。
3. 弹企业微信二维码。
4. 记录二维码查看行为。

### 9.7 网贷利率计算器

输入：

1. 分期金额 principal
2. 分期期数 periods
3. 一次性手续费 upfrontFeePercent
4. 每期手续费/利率 monthlyFeePercent

第一版计算要求：

1. 给出估算结果即可。
2. 结果文案必须包含“仅供参考”。
3. 点击计算时调用事件接口记录 `USE_CALCULATOR`。
4. 不保存具体计算数据，metadata 可记录是否使用，不记录用户敏感贷款计算明细。

### 9.8 企业微信二维码弹窗

统一组件：

1. 显示后台配置的 `WECHAT_QR`。
2. 文案：`扫码添加顾问，获取免费初评回访`。
3. 展示弹窗时调用 `/api/app/events` 记录 `VIEW_WECHAT_QR`。
4. 如果未配置二维码，显示占位提示：`顾问二维码待配置`。

### 9.9 底部 Tab

Tab：

1. 成功案例
2. 主页
3. 资讯
4. 个人中心

默认 active：主页。

## 10. 后台 Web 页面规格

### 10.1 路由建议

```text
/admin/login
/admin/leads
/admin/assets
/admin/articles
/admin/cases
/admin/admin-users
```

### 10.2 布局

PC 后台使用 Element Plus：

1. 左侧菜单。
2. 顶部显示当前管理员名和退出按钮。
3. 默认进入线索管理。
4. 普通管理员不显示管理员管理菜单。

### 10.3 线索管理页

功能：

1. 筛选。
2. 表格展示。
3. 查看客户历史。
4. Excel 导出。
5. 自定义导出字段。

筛选项：

1. 手机号/称呼关键词
2. 地区
3. 线索类型
4. 债务类型
5. 债务金额区间
6. 时间范围
7. 是否查看企微二维码

表格字段：

1. 线索类型
2. 手机号
3. 称呼
4. 地区
5. 债务金额
6. 债务类型
7. 来源
8. 首次登录时间
9. 提交时间
10. 是否查看企微二维码
11. 最近行为
12. 操作：查看历史

### 10.4 素材管理页

固定素材位：

1. 顶部视频
2. 视频封面
3. AI 债务咨询图
4. 网贷利率计算器图
5. 规划优化债务图
6. 企业微信二维码

每个素材位：

1. 显示当前预览。
2. 上传新文件。
3. 保存替换。

### 10.5 资讯管理页

功能：

1. 列表。
2. 新增。
3. 编辑。
4. 上架。
5. 下架。
6. 删除。

字段：

1. 标题
2. 封面
3. 摘要
4. 正文文本
5. 发布时间
6. 排序值
7. 状态

### 10.6 成功案例管理页

功能同资讯。

字段：

1. 称呼
2. 脱敏手机号
3. 逾期平台
4. 逾期金额
5. 处理方案
6. 头像/封面
7. 详情文本
8. 排序值
9. 状态

### 10.7 管理员管理页

仅 `BOSS` 可见。

功能：

1. 管理员列表。
2. 新增管理员。
3. 禁用/启用管理员。
4. 重置密码。

第一版不做删除。

## 11. 文件上传与静态资源

### 11.1 配置

后端配置项：

```yaml
app:
  upload:
    base-dir: C:/OverdueFree/uploads
    public-prefix: /uploads
```

Spring Multipart：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 520MB
```

### 11.2 目录规则

保存路径：

```text
C:/OverdueFree/uploads/yyyy/MM/dd/uuid.ext
```

访问路径：

```text
/uploads/yyyy/MM/dd/uuid.ext
```

### 11.3 校验

图片：

- 扩展名：jpg、jpeg、png、webp
- MIME：image/jpeg、image/png、image/webp
- 最大：20MB

视频：

- 扩展名：mp4
- MIME：video/mp4、application/octet-stream 可兼容部分浏览器上传
- 最大：500MB

## 12. 文案默认值

### 12.1 合规提示

可用于页面底部或弹窗：

```text
服务效果需结合个人情况评估，信息仅用于回访沟通和初步评估。
```

### 12.2 二维码弹窗

```text
扫码添加顾问，获取免费初评回访
```

### 12.3 提交成功

```text
信息已收到，人工顾问将结合您的情况进行初步评估。
```

### 12.4 计算器提示

```text
以上计算结果仅供参考，实际费用以合同和平台规则为准。
```

### 12.5 演示协议占位

```text
当前为演示版本，正式使用条款和隐私政策将由运营方后续提供。
```

## 13. 前后端联调约定

### 13.1 开发环境跨域

后端允许：

1. `http://localhost:5173`
2. `http://127.0.0.1:5173`
3. `http://localhost:5174`
4. `http://127.0.0.1:5174`

### 13.2 Token 保存

客户 H5：

```text
localStorage key: overduefree_customer_token
```

后台：

```text
localStorage key: overduefree_admin_token
```

### 13.3 请求头

```text
Authorization: Bearer <token>
```

## 14. 实施顺序细化

### 14.1 第一阶段：工程骨架

分支：`chore/project-bootstrap`

要做：

1. 初始化 `backend` Maven 项目。
2. 初始化 `frontend-h5` uni-app 项目。
3. 初始化 `admin-web` Vue3 Vite 项目。
4. 添加 `docker/docker-compose.yml`。
5. 添加根目录 `README.md`。
6. 不实现业务。

验收：

1. Docker MySQL 可启动。
2. 后端空项目可启动。
3. H5 空项目可启动。
4. 后台空项目可启动。

### 14.2 第二阶段：后端基础

分支：`feat/backend-foundation`

要做：

1. 添加 Flyway。
2. 添加数据库 DDL。
3. 添加统一响应。
4. 添加全局异常。
5. 添加 MyBatis-Plus。
6. 添加初始化数据逻辑。

验收：

1. 后端启动后自动建表。
2. 默认老板账号创建成功。
3. 默认配置和素材位创建成功。

### 14.3 第三阶段：认证

分支：`feat/backend-auth`

要做：

1. 客户发送模拟验证码。
2. 客户手机号登录。
3. 客户退出。
4. 管理员账号密码登录。
5. 管理员退出。
6. token 拦截器。

验收：

1. 客户登录后数据库有 customer 和 customer_session。
2. 管理员登录后数据库有 admin_session。
3. 未登录访问受保护接口返回 401 类错误。

### 14.4 第四阶段：线索和行为

分支：`feat/backend-leads`

要做：

1. 线索提交接口。
2. 行为记录接口。
3. 后台线索列表。
4. 客户历史接口。

验收：

1. 只登录客户后台可见。
2. AI/规划提交后后台可见。
3. 查看二维码行为后台可见。

### 14.5 第五阶段：内容和素材

分支：`feat/backend-content-assets`

要做：

1. 文件上传。
2. 素材管理。
3. 资讯 CRUD。
4. 成功案例 CRUD。
5. 客户端内容接口。

验收：

1. 后台可上传图片和 mp4。
2. 前台能读取素材、资讯、案例。

### 14.6 第六阶段：导出

分支：`feat/backend-export`

要做：

1. 线索筛选。
2. 自定义字段导出 Excel。
3. 导出操作日志。

验收：

1. 可筛选导出。
2. 可选择字段。
3. Excel 打开无乱码。

### 14.7 第七阶段：客户 H5

分支：`feat/frontend-h5`

要做：

1. 登录页。
2. 首页。
3. 底部 Tab。
4. 企业微信二维码弹窗。
5. AI 留资聊天。
6. 规划评估页。
7. 计算器。
8. 案例和资讯列表/详情。
9. 个人中心退出登录。

验收：

1. 手机宽度下视觉接近对标图。
2. 所有 CTA 可用。
3. 数据能写入后台。

### 14.8 第八阶段：后台 Web

分支：`feat/admin-web`

要做：

1. 后台登录。
2. 线索管理。
3. Excel 导出。
4. 素材管理。
5. 资讯管理。
6. 案例管理。
7. 管理员管理。

验收：

1. 老板账号可管理管理员。
2. 普通管理员看不到管理员管理。
3. 后台可完成所有第一版管理动作。

### 14.9 第九阶段：联调和演示

分支：`feat/local-demo-polish`

要做：

1. README 完善。
2. 本地启动说明。
3. 默认账号说明。
4. 移动端视觉修正。
5. 后台体验修正。
6. 简单测试清单。

验收：

1. 用户电脑可按 README 启动。
2. H5 和后台都能演示完整流程。

## 15. 测试要求

### 15.1 后端测试

至少覆盖：

1. 手机号格式校验。
2. 模拟验证码登录。
3. 管理员密码 BCrypt 校验。
4. 未登录接口拦截。
5. 线索提交必填校验。
6. 文件类型和大小校验。
7. Excel 导出字段选择。

### 15.2 前端手工测试

H5：

1. 首次打开跳登录。
2. 输入手机号获取验证码。
3. 登录后进主页。
4. 关闭刷新后 7 天内免登录。
5. 退出登录后重新登录。
6. AI 留资能提交。
7. 规划评估能提交。
8. 查看二维码能记录。
9. 计算器能展示结果。

后台：

1. 老板登录成功。
2. 新增管理员成功。
3. 管理员登录成功。
4. 普通管理员无管理员管理菜单。
5. 线索筛选可用。
6. Excel 导出可用。
7. 素材替换后前台生效。
8. 资讯和案例上下架后前台列表变化。

### 15.3 视觉验收

客户 H5：

1. 使用 390px 宽度测试。
2. 底部 Tab 不遮挡主要内容。
3. 红色主按钮不溢出。
4. 卡片文字不重叠。
5. 弹窗在手机屏幕居中。
6. 视频弹窗可关闭。

## 16. README 必须包含

根目录 `README.md` 至少包含：

1. 项目说明。
2. 技术栈。
3. 目录结构。
4. 环境要求：JDK 11、Maven、Node、Docker。
5. 启动 MySQL 命令。
6. 启动后端命令。
7. 启动 H5 命令。
8. 启动后台命令。
9. 默认老板账号说明。
10. 上传目录说明。
11. 常见问题。

示例启动命令：

```bash
cd docker
docker compose up -d

cd ../backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

cd ../frontend-h5
npm install
npm run dev:h5

cd ../admin-web
npm install
npm run dev
```

## 17. 完成定义

某个功能分支完成必须满足：

1. 代码可编译。
2. 服务可启动。
3. 关键功能手工跑通。
4. 没有明显控制台错误。
5. 没有把敏感密码写死在代码中。
6. 没有破坏已有功能。
7. 更新必要 README 或文档。
8. 输出测试结果。
9. 等用户确认后再合并到 `master`。

## 18. 遇到不确定时的默认策略

1. 产品细节不明确时，优先选择“可上线、可维护、后续好扩展”的方案。
2. 页面视觉不明确时，贴近对标截图，但做更专业的间距、层级、字体和按钮状态。
3. 数据库字段不明确时，保留必要字段，不做过度复杂设计。
4. 后台功能不明确时，先做简单可用，不做复杂工作流。
5. 安全策略不明确时，按正式产品最低标准实现。
6. 只有会影响方向、成本、合规或大量返工的问题，才必须询问用户。

## 19. 必须询问用户的情况

出现以下情况，先停下询问用户：

1. 需要接入真实短信服务。
2. 需要接入真实企业微信跳转或企微 API。
3. 需要购买服务器、域名、云存储。
4. 需要改后端技术栈。
5. 需要改数据库类型。
6. 需要把功能分支合并进 `master`。
7. 需要删除已有数据或上传文件。
8. 需要写正式公司名称、备案号、隐私协议。
9. 需要处理真实用户隐私数据导出到外部平台。

## 20. 最小演示闭环

最终 Demo 必须能演示这条路径：

1. 客户打开 H5。
2. 输入手机号。
3. 获取并看到模拟验证码。
4. 登录进入主页。
5. 点击 AI 债务咨询。
6. 按聊天流程提交债务情况。
7. 页面提示提交成功并展示企业微信二维码。
8. 老板登录后台。
9. 老板看到该手机号、线索资料、二维码查看行为。
10. 老板按筛选导出 Excel。

如果这条路径未跑通，不允许称为第一版完成。
