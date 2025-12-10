# GitHub Repository Info API

一个基于Spring Boot + MyBatis的GitHub仓库信息查询REST API服务。

## 功能特点

- 🚀 通过GitHub API获取仓库信息
- 💾 自动缓存到H2内存数据库
- 🔄 二次查询直接从缓存读取，提升性能
- 🛡️ 完善的异常处理机制
- ✅ 包含端到端测试

## 技术栈

- **框架**: Spring Boot 2.2.7.RELEASE
- **持久层**: MyBatis 2.1.2
- **数据库**: H2 (内存数据库)
- **构建工具**: Maven
- **Java版本**: JDK 1.8

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.x

### 运行项目

#### Windows
```bash
mvnw.cmd spring-boot:run
```

#### Linux/Mac
```bash
./mvnw spring-boot:run
```

应用将在 `http://localhost:8080` 启动

## API接口

### 获取GitHub仓库信息

```
GET /repositories/{owner}/{repository-name}
```

**示例请求**:
```bash
curl http://localhost:8080/repositories/spring-projects/spring-boot
```

**成功响应** (200 OK):
```json
{
  "fullName": "spring-projects/spring-boot",
  "description": "Spring Boot helps you to create Spring-powered, production-grade applications...",
  "cloneUrl": "https://github.com/spring-projects/spring-boot.git",
  "stars": 79313,
  "createdAt": "2012-10-19T15:02:57"
}
```

**仓库不存在** (404 Not Found):
```json
{
  "timestamp": "2025-12-10T14:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Repository user123/repo123 not found on GitHub"
}
```

## H2 Console

访问 `http://localhost:8080/h2-console` 查看数据库

**连接信息**:
- JDBC URL: `jdbc:h2:mem:githubdb`
- Username: `sa`
- Password: (留空)

## 运行测试

```bash
# Windows
mvnw.cmd test

# Linux/Mac
./mvnw test
```

## 项目结构

```
src/main/java/com/example/
├── controller/          # REST控制器
├── dto/                # 数据传输对象
├── entity/             # 实体类
├── exception/          # 异常处理
├── mapper/             # MyBatis Mapper
└── service/            # 业务服务

src/main/resources/
├── mapper/             # MyBatis XML映射文件
├── application.properties
├── schema.sql          # 数据库表结构
└── data.sql            # 初始化数据
```

## 设计特点

### 缓存机制

1. 首次查询：从GitHub API获取 → 保存到数据库
2. 二次查询：直接从数据库读取（避免频繁调用API）

### 依赖注入

统一使用构造器注入，确保依赖不可变和清晰可见

### 异常处理

全局异常处理器统一处理：
- 404: 仓库不存在
- 502: GitHub API调用失败
- 500: 服务器内部错误

## 作者

阿灰

## 许可证

MIT License
