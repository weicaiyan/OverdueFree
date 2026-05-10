# OverdueFree（逾期上岸）

债务逾期处理咨询 H5 获客产品 + PC 后台管理系统。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 11 + Spring Boot 2.7.x + Maven + MyBatis-Plus + Flyway |
| 数据库 | MySQL 8 (Docker) |
| 客户 H5 | uni-app + Vue 3 + TypeScript + Pinia |
| 后台 Web | Vue 3 + Vite + TypeScript + Element Plus |

## 目录结构

```
OverdueFree/
  backend/          Spring Boot 后端
  frontend-h5/      uni-app 客户 H5
  admin-web/        Vue3 后台管理
  docker/           Docker MySQL
  docs/             文档
```

## 环境要求

- JDK 11
- Maven 3.6+
- Node.js 18+
- Docker Desktop

## 本地启动

### 1. 启动 MySQL

```bash
cd docker
docker compose up -d
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

后端启动后自动建表并初始化数据。

### 3. 启动客户 H5

```bash
cd frontend-h5
npm install
npm run dev:h5
```

### 4. 启动后台 Web

```bash
cd admin-web
npm install
npm run dev
```

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 老板 | boss | boss123456 |

## 端口

| 服务 | 端口 |
|------|------|
| 后端 | 8080 |
| 客户 H5 | 5173 |
| 后台 Web | 5174 |
| MySQL | 3306 |

## 上传目录

文件上传目录：`C:/OverdueFree/uploads/`

## 常见问题

**Q: 启动后端报数据库连接失败？**
A: 确保 Docker MySQL 已启动，执行 `docker ps` 确认 overduefree-mysql 容器运行中。

**Q: H5 页面打不开？**
A: uni-app H5 开发模式使用 hash 路由，访问 `http://localhost:5173/#/pages/home/index`。
