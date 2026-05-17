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

## 硬性约束

- 后端数据库访问必须使用 MyBatis-Plus，不使用 JPA/Hibernate 作为主要持久层。
- 整个项目统一使用北京时间 `Asia/Shanghai`。
- MySQL Docker 使用 `TZ=Asia/Shanghai` 和 `--default-time-zone=+08:00`。
- JDBC 连接使用 `serverTimezone=Asia/Shanghai`。
- 后端 JSON 时间格式使用 `yyyy-MM-dd HH:mm:ss`。

## 本地启动

### 双击脚本启动

Windows 本地演示可以直接双击以下脚本：

| 脚本 | 作用 |
|------|------|
| `start-mysql.bat` | 启动 Docker MySQL |
| `backend/start-backend.bat` | 启动后端；如果 8080 已被占用，只提示，不关闭也不重启 |
| `start-frontends.bat` | 启动并重启两个前端开发服务 |
| `start-local-demo.bat` | 依次启动 MySQL、后端、两个前端 |

`start-mysql.bat` 会等待 MySQL 真正可连接后再返回，避免第一次启动 Docker MySQL 时后端过早启动导致连接失败。

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

如果你已经手动启动了 `overduefree-mysql` 容器，也可以直接跳过 Docker 启动步骤。

如果后端代码有修改，需要手动重启后端后才会生效；本项目脚本不会自动关闭或重启已经运行的后端进程。

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

上传文件不放进代码仓库，后续由后端配置项控制访问路径。

## 常见问题

**Q: 启动后端报数据库连接失败？**
A: 确保 Docker MySQL 已启动，执行 `docker ps` 确认 overduefree-mysql 容器运行中。

**Q: H5 页面打不开？**
A: uni-app H5 开发模式使用 hash 路由，访问 `http://localhost:5173/#/pages/home/index`。

**Q: 后台登录提示无法连接后端？**
A: 先确认后端已启动并监听 `8080`，再确认后台开发服务已重启以加载 Vite 代理配置。
