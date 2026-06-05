# 校园运动场馆预约管理系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.5-green.svg)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue.js-3.4.0-4FC08D.svg)](https://vuejs.org/)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.3-blue.svg)](https://baomidou.com/)

## 项目简介

本项目是一套基于前后端分离架构的校园体育场馆预约与健康管理平台。旨在解决校园体育场馆资源分配不均、预约流程繁琐等问题。通过数字化管理手段，实现场馆资源的在线浏览、实时预约、智能排班，同时集成个人健康数据管理功能，以支持校园体育信息化建设。

- **Git 仓库地址**: [https://bdgit.educoder.net/gdlgxy2312402040306/School-PH-Platform.git](https://bdgit.educoder.net/gdlgxy2312402040306/School-PH-Platform.git)

## 技术架构

### 后端技术栈 (Backend)
- **开发语言**: Java 17
- **核心框架**: Spring Boot 3.0.5
- **ORM框架**: MyBatis-Plus 3.5.3.1
- **权限认证**: Sa-Token 1.34.0
- **API文档**: Knife4j 4.1.0 (OpenAPI 3)
- **数据库**: MySQL 8.0
- **构建工具**: Maven

### 前端技术栈 (Frontend)
- **核心框架**: Vue 3.4.0 (Composition API)
- **构建工具**: Vite 5.0.0
- **UI 组件库**: Element Plus 2.5.0
- **状态管理**: Pinia 2.1.0
- **路由管理**: Vue Router 4.2.0
- **HTTP客户端**: Axios 1.6.0
- **数据可视化**: ECharts 5.5.0

## 功能模块

### 用户模块 (User)
- **账户管理**: 支持用户注册、登录及个人信息维护。
- **场馆浏览**: 提供场馆列表查询，展示场馆详细信息、位置及当前状态。
- **在线预约**: 基于场馆排班信息，支持选择特定时段进行实时预约。
- **预约管理**: 提供历史预约记录查询，支持对未开始的预约进行取消操作。
- **健康档案**: 支持记录每日身高、体重等健康指标数据。
- **运动记录**: 支持录入跑步、游泳等运动类型的时长与卡路里消耗。
- **数据分析**: 通过图表形式展示个人运动趋势与健康状况变化。

### 管理模块 (Admin)
- **场馆管理**: 支持场馆信息的增删改查，以及场馆状态（开放/维护）的动态调整。
- **排班管理**: 支持对特定场馆设置每日开放时段及最大容纳人数。
- **预约审核**: 提供预约记录的全局视图，支持人工审核、强制取消或拒绝预约。
- **用户管理**: 支持查看注册用户列表及权限管理。
- **系统日志**: 记录系统关键操作日志，便于审计与追踪。

## 项目结构

```
CampusSportVenue/
├── backend/                # 后端工程根目录
│   ├── src/main/java/      # Java 源代码
│   │   └── com/campus/sport/
│   │       ├── config/     # 配置类 (Web, MyBatis, SaToken)
│   │       ├── controller/ # 控制层 (API 接口)
│   │       ├── service/    # 业务逻辑层
│   │       ├── mapper/     # 数据访问层
│   │       └── entity/     # 实体类
│   └── src/main/resources/ # 资源文件 (application.yml, mapper xml)
├── frontend/               # 前端工程根目录
│   ├── src/
│   │   ├── api/            # 接口封装
│   │   ├── views/          # 页面组件
│   │   ├── components/     # 公共组件
│   │   └── stores/         # 状态管理
│   └── vite.config.js      # Vite 配置
├── full_database.sql       # 数据库初始化脚本
└── README.md               # 项目说明文档
```

## 部署说明

### 1. 环境要求
部署前请确保本地环境满足以下要求：
- **Java JDK**: 17 或更高版本
- **Node.js**: 18 或更高版本
- **MySQL**: 8.0 或更高版本
- **Maven**: 3.6 或更高版本

### 2. 数据库初始化
1. 创建数据库 `campus_sport`。
2. 执行 SQL 初始化脚本：
   - 使用项目根目录下的 `full_database.sql` 文件进行导入。
3. 系统预置账号：
   - 管理员账号: `admin` / 密码: `123456`
   - 测试用户账号: `1001` / `123456`

### 3. 后端服务启动
```bash
cd backend
mvn spring-boot:run
```
- 后端服务默认端口: `8080`
- API 接口文档地址: `http://localhost:8080/doc.html`

### 4. 前端服务启动
```bash
cd frontend
npm install  # 或 yarn install
npm run dev
```
- 前端服务默认端口: `5173`
- 本地访问地址: `http://localhost:5173`
