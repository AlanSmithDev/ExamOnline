# ExamOnline 在线考试系统 / ExamOnline Online Examination System

<div align="center">

![Logo](docs_images/logo.png)

[![Star](https://img.shields.io/github/stars/AlanSmithDev/ExamOnline?style=social)](https://github.com/AlanSmithDev/ExamOnline/stargazers)
[![Fork](https://img.shields.io/github/forks/AlanSmithDev/ExamOnline?style=social)](https://github.com/AlanSmithDev/ExamOnline/network/members)
[![License](https://img.shields.io/github/license/AlanSmithDev/ExamOnline)](https://github.com/AlanSmithDev/ExamOnline/blob/main/LICENSE)

**简体中文** | [English](#english-version)

</div>

---

## 📖 目录 / Table of Contents

- [项目简介](#项目简介)
- [项目架构](#项目架构)
- [技术栈](#技术栈)
- [模块详解](#模块详解)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [核心功能](#核心功能)
- [数据库设计](#数据库设计)
- [API文档](#api文档)
- [部署指南](#部署指南)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

---

## 📝 项目简介

ExamOnline 是一套功能完善的**在线考试系统**，采用前后端分离架构，后端涵盖 Java 微服务版和 Python Django 版两套实现。系统支持多种题型（单选题、多选题、判断题、填空题、名词解释、完形填空、阅读理解、听力题、简答题），提供智能组卷与手动组卷两种组卷方式，具备自动阅卷、成绩分析、人脸识别等功能。

> **ExamOnline** is a comprehensive **online examination system** with a separated frontend and backend architecture. The backend includes both Java microservice version and Python Django version. The system supports multiple question types, intelligent and manual paper composition, automatic grading, score analysis, and face recognition.

---

## 🏗 项目架构 / Project Architecture

### 系统架构图 / System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ExamOnline 系统架构                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐      │
│  │   Web Client    │      │   Mobile App    │      │   WeChat Mini   │      │
│  │  (Vue 3 + TS)   │      │   (Native)      │      │    Program      │      │
│  └────────┬────────┘      └────────┬────────┘      └────────┬────────┘      │
│           │                        │                        │                │
│           └────────────────────────┼────────────────────────┘                │
│                                    ▼                                           │
│                    ┌───────────────────────────────┐                           │
│                    │        API Gateway            │                           │
│                    │   (Spring Cloud Gateway)      │                           │
│                    │   + Nacos Service Discovery   │                           │
│                    └───────────────────────────────┘                           │
│                                    │                                           │
│           ┌────────────────────────┼────────────────────────┐                  │
│           │                        │                        │                  │
│           ▼                        ▼                        ▼                  │
│  ┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐         │
│  │   Java Backend  │      │  Python Backend │      │     Nginx       │         │
│  │ (Spring Cloud)  │      │    (Django)     │      │   Load Balance │         │
│  └────────┬────────┘      └────────┬────────┘      └─────────────────┘         │
│           │                        │                                           │
│           │         ┌──────────────┴──────────────┐                            │
│           │         │                             │                            │
│           ▼         ▼                             ▼                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐              │
│  │   Nacos     │ │   Dubbo     │ │    Redis    │ │    MySQL    │              │
│  │ (Registry)  │ │ (RPC Call)  │ │  (Cache)    │ │ (Database)  │              │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘              │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 技术架构图 / Technology Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              技术架构分层                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────────────┐     │
│  │                      Frontend (表现层)                               │     │
│  │  Vue 3  +  TypeScript  +  Vite  +  Element Plus  +  Pinia       │     │
│  └─────────────────────────────────────────────────────────────────────┘     │
│                                    │                                           │
│                                    ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐     │
│  │                    API Gateway (网关层)                            │     │
│  │            Spring Cloud Gateway  +  Nacos  +  JWT                 │     │
│  └─────────────────────────────────────────────────────────────────────┘     │
│                                    │                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐     │
│  │                    Business Services (业务层)                       │     │
│  │                                                                      │     │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐              │     │
│  │  │Exam Service│ │Paper Svc │ │Question  │ │ User Svc │              │     │
│  │  │ (考试服务) │ │ (试卷)   │ │ (题目)   │ │ (用户)   │              │     │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘              │     │
│  │                                                                      │     │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐              │     │
│  │  │Message   │ │ Depart   │ │  File    │ │   Log    │              │     │
│  │  │ (消息)   │ │ (部门)   │ │ (文件)   │ │ (日志)   │              │     │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘              │     │
│  └─────────────────────────────────────────────────────────────────────┘     │
│                                    │                                           │
│                                    ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐     │
│  │                      Data Layer (数据层)                            │     │
│  │         MyBatis-Plus  +  Dubbo RPC  +  Redis Cache                 │     │
│  └─────────────────────────────────────────────────────────────────────┘     │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 🛠 技术栈 / Technology Stack

### 前端技术栈 / Frontend Stack

| 技术 / Technology | 版本 / Version | 说明 / Description |
|-------------------|----------------|-------------------|
| Vue | 3.4.38 | 渐进式JavaScript框架 |
| TypeScript | 5.2.2 | JavaScript超集，强类型 |
| Vite | 5.1.4 | 下一代前端构建工具 |
| Element Plus | 2.6.1 | Vue 3 组件库 |
| Pinia | 2.1.7 | Vue 状态管理 |
| Vue Router | 4.3.0 | Vue路由管理 |
| Axios | 1.6.7 | HTTP请求库 |
| Sass | 1.71.1 | CSS预处理器 |

### Java后端技术栈 / Java Backend Stack

| 技术 / Technology | 版本 / Version | 说明 / Description |
|-------------------|----------------|-------------------|
| Spring Boot | 2.2.5.RELEASE | 应用框架 |
| Spring Cloud | Hoxton.RELEASE | 微服务框架 |
| Nacos | 2.2.1.RELEASE | 服务注册与配置中心 |
| Dubbo | - | 分布式服务框架 |
| MyBatis-Plus | 3.5.1 | ORM框架 |
| Redis | - | 缓存数据库 |
| JWT | 0.9.1 | 身份认证 |
| SnowFlake | - | 分布式ID生成 |
| Swagger | 2.9.2 | API文档 |
| EasyExcel | 3.0.5 | Excel处理 |
| POI-TL | 1.10.1 | Word模板生成 |

### Python后端技术栈 / Python Backend Stack

| 技术 / Technology | 版本 / Version | 说明 / Description |
|-------------------|----------------|-------------------|
| Django | 4.2.8 | Web框架 |
| Django REST Framework | 3.14.0 | RESTful API |
| MySQLclient | 2.1.0 | MySQL驱动 |
| djangorestframework-simplejwt | 5.3.1 | JWT认证 |
| django-cors-headers | 4.3.1 | 跨域处理 |
| pandas | 2.0.3 | 数据处理 |
| openpyxl | 3.1.2 | Excel处理 |

---

## 📦 模块详解 / Module Details

### 一、前端模块 (ExamOnline-Front-master)

```
ExamOnline-Front-master/
├── src/
│   ├── api/                    # API接口层
│   │   ├── common.ts           # 通用API
│   │   ├── exam.ts             # 考试管理API
│   │   ├── examOnline.ts       # 在线考试API
│   │   ├── examResult.ts       # 考试成绩API
│   │   ├── login.ts            # 登录API
│   │   ├── menu.ts             # 菜单API
│   │   ├── paper.ts            # 试卷管理API
│   │   ├── questions.ts        # 题目管理API
│   │   └── user.ts             # 用户管理API
│   │
│   ├── components/            # 公共组件
│   │   ├── CommonPagination.vue    # 分页组件
│   │   ├── CommonResultView.vue    # 结果视图组件
│   │   └── Container.vue           # 容器组件
│   │
│   ├── router/                 # 路由配置
│   │   └── index.ts
│   │
│   ├── stores/                 # Pinia状态管理
│   │   ├── DrawerCommonStore.ts
│   │   ├── ExamOnlineCallbackStore.ts
│   │   ├── ExamResultDetailStore.ts
│   │   └── PaperViewStore.ts
│   │
│   ├── utils/                  # 工具函数
│   │   ├── cookie.ts           # Cookie工具
│   │   └── request.ts          # Axios封装
│   │
│   └── views/                  # 页面视图
│       ├── HomePage/           # 首页
│       ├── Login/               # 登录页
│       ├── ExamManagement/       # 考试管理
│       ├── ExamOnline/          # 在线考试
│       ├── ExamResult/          # 考试成绩
│       ├── QuestionManagement/   # 题库管理
│       ├── PaperManagement/      # 试卷管理
│       └── UserManagement/       # 用户管理
```

### 二、Java微服务模块 (ExamOnline-master)

```
ExamOnline-master/
├── eo-common/                  # 公共模块
│   └── src/main/java/com/sdut/examonline/common/
│       ├── constant/           # 常量定义
│       │   ├── ExamState.java         # 考试状态
│       │   ├── PaperJoinType.java      # 组卷方式
│       │   ├── PaperRecordState.java   # 答卷状态
│       │   ├── QuestionType.java       # 题目类型
│       │   └── RoleConstants.java       # 角色常量
│       │
│       ├── dto/                 # 数据传输对象
│       │   └── BaseDto.java
│       │
│       ├── exception/           # 异常定义
│       │   ├── ErrorCode.java
│       │   └── ServiceException.java
│       │
│       ├── util/               # 工具类
│       │   ├── SnowFlakeIdWorker.java  # 雪花ID算法
│       │   ├── JwtTokenUtils.java      # JWT工具
│       │   ├── AesUtils.java           # AES加密
│       │   └── Assert.java             # 断言工具
│       │
│       └── vo/                 # 视图对象
│           └── BaseVo.java
│
├── eo-dubbo/                   # Dubbo接口定义
│   └── src/main/java/com/sdut/examonline/dubbo/
│       ├── client/             # 服务接口
│       │   ├── ExamDubboService.java
│       │   ├── PaperDubboService.java
│       │   ├── QuestionDubboService.java
│       │   ├── UserDubboService.java
│       │   ├── DepartDubboService.java
│       │   ├── MessageDubboService.java
│       │   └── LogDubboService.java
│       │
│       └── dto/                # DTO定义
│           ├── ExamDto.java
│           ├── PaperDetailDto.java
│           ├── QuestionDto.java
│           └── ...
│
├── eo-gateway/                 # API网关
│   └── src/main/java/com/sdut/examonline/gateway/
│       ├── config/
│       │   ├── CrossConfig.java          # 跨域配置
│       │   ├── NacosDynamicRouteService.java
│       │   └── WebConfig.java
│       │
│       ├── filter/
│       │   └── TokenFilter.java          # JWT令牌过滤器
│       │
│       └── swagger/
│           ├── SwaggerProvider.java
│           └── SwaggerResourceController.java
│
├── eo-web/                     # Web公共层
│   └── src/main/java/com/sdut/examonline/web/
│       ├── annotation/        # 自定义注解
│       │   ├── AccessLogIgnored.java
│       │   ├── LogLogin.java
│       │   └── LogOperation.java
│       │
│       ├── aspect/            # 切面
│       │   ├── LogAccessAspect.java
│       │   ├── LogLoginAspect.java
│       │   └── LogOperationAspect.java
│       │
│       ├── config/            # 配置类
│       │   ├── AsyncConfig.java
│       │   ├── CacheConfig.java
│       │   ├── MybatisPlusConfig.java
│       │   ├── SecurityConfig.java
│       │   └── SwaggerConfig.java
│       │
│       ├── dto/
│       │   ├── AccessLogDto.java
│       │   ├── LoginLogDto.java
│       │   ├── OperationLogDto.java
│       │   ├── PagingDto.java
│       │   └── PagingParamDto.java
│       │
│       ├── security/
│       │   ├── JwtAuthenticationFilter.java
│       │   └── JwtAuthenticationHandler.java
│       │
│       ├── util/              # 工具类
│       │   ├── CacheUtils.java
│       │   ├── ExcelUtils.java
│       │   ├── HttpUtils.java
│       │   ├── JwtUtils.java
│       │   ├── MapperUtils.java
│       │   └── PoitlUtils.java
│       │
│       └── vo/
│           ├── PagingVo.java
│           └── ResponseVo.java
│
├── eo-service-exam/            # 考试服务
│   └── src/main/java/com/sdut/examonline/exam/
│       ├── controller/
│       │   └── ExamController.java    # 考试管理控制器
│       │
│       ├── service/            # 业务逻辑
│       │   ├── ExamService.java
│       │   ├── ExamQuestionIntelligentService.java  # 智能组卷
│       │   └── ExamQuestionManualService.java       # 手动组卷
│       │
│       ├── service/impl/
│       │   ├── ExamServiceImpl.java
│       │   ├── ExamQuestionIntelligentServiceImpl.java
│       │   └── ExamQuestionManualServiceImpl.java
│       │
│       ├── entity/             # 实体类
│       │   ├── Exam.java
│       │   ├── ExamQuestionIntelligent.java
│       │   └── ExamQuestionManual.java
│       │
│       ├── mapper/             # 数据访问
│       ├── redis/              # Redis配置
│       │   ├── RedisKeyConstants.java
│       │   └── RedisKeyExpirationListener.java
│       │
│       ├── dubbo/              # Dubbo实现
│       │   └── ExamDubboServiceImpl.java
│       │
│       ├── dto/                # 数据传输对象
│       │   ├── ExamCreateRequest.java
│       │   ├── ExamSaveRequest.java
│       │   └── ...
│       │
│       └── vo/                 # 视图对象
│           ├── ExamVo.java
│           ├── ExamDetailVo.java
│           ├── ExamAnalysisVo.java
│           └── FinishedExamVo.java
│
├── eo-service-paper/           # 试卷服务
│   └── src/main/java/com/sdut/examonline/paper/
│       ├── controller/
│       │   └── PaperController.java   # 试卷管理控制器
│       │
│       ├── service/
│       │   ├── PaperService.java
│       │   ├── PaperQuestionService.java
│       │   ├── PaperAnswerService.java
│       │   └── PaperRecordService.java
│       │
│       ├── service/impl/
│       │   ├── PaperServiceImpl.java
│       │   ├── PaperQuestionServiceImpl.java
│       │   ├── PaperAnswerServiceImpl.java
│       │   └── PaperRecordServiceImpl.java
│       │
│       ├── entity/
│       │   ├── Paper.java            # 试卷
│       │   ├── PaperQuestion.java    # 试卷题目
│       │   ├── PaperAnswer.java     # 答题记录
│       │   └── PaperRecord.java     # 考试记录
│       │
│       ├── corrector/          # 自动阅卷（策略模式）
│       │   ├── Corrector.java
│       │   ├── BaseCorrector.java
│       │   └── types/
│       │       ├── SingleChoiceCorrector.java    # 单选题批改
│       │       ├── MultipleChoiceCorrector.java   # 多选题批改
│       │       └── JudgementCorrector.java        # 判断题批改
│       │
│       ├── redis/
│       │   ├── RedisKeyConstants.java
│       │   └── RedisKeyExpirationListener.java
│       │
│       └── dubbo/
│           └── PaperDubboServiceImpl.java
│
├── eo-service-question/        # 题目服务
│   └── src/main/java/com/sdut/examonline/question/
│       ├── controller/
│       │   ├── QuestionController.java      # 题目管理
│       │   └── QuestionRepoController.java   # 题库管理
│       │
│       ├── service/
│       │   ├── QuestionService.java
│       │   ├── QuestionAnswerService.java
│       │   └── QuestionRepoService.java
│       │
│       ├── entity/
│       │   ├── Question.java           # 题目
│       │   ├── QuestionAnswer.java     # 预选答案
│       │   └── QuestionRepo.java       # 题库
│       │
│       └── constant/
│           └── QuestionRepoType.java  # 题库类型
│
├── eo-service-user/             # 用户服务
│   └── src/main/java/com/sdut/examonline/user/
│       ├── controller/
│       │   ├── UserController.java    # 用户管理
│       │   ├── FaceController.java   # 人脸识别
│       │   └── QrCodeController.java   # 二维码
│       │
│       ├── service/
│       │   ├── UserService.java
│       │   ├── FaceService.java
│       │   └── QrCodeService.java
│       │
│       ├── entity/
│       │   └── User.java
│       │
│       └── util/
│           ├── BaiduFaceUtils.java    # 百度人脸SDK封装
│           └── QrCodeUtils.java       # 二维码生成
│
├── eo-service-message/          # 消息服务
│   └── src/main/java/com/sdut/examonline/message/
│       ├── controller/
│       │   └── MessageController.java
│       │
│       ├── service/
│       │   ├── MessageService.java
│       │   └── MessageReadRecordService.java
│       │
│       ├── entity/
│       │   ├── Message.java
│       │   └── MessageReadRecord.java
│       │
│       ├── ws/                 # WebSocket实时通信
│       │   ├── WebSocketClient.java           # WebSocket客户端
│       │   └── WebSocketBroadcastListener.java # 广播监听
│       │
│       └── config/
│           └── WebSocketConfig.java
│
├── eo-service-depart/          # 部门服务
├── eo-service-file/            # 文件服务
├── eo-service-log/             # 日志服务
│   └── src/main/java/com/sdut/examonline/log/
│       ├── controller/
│       ├── service/
│       ├── entity/
│       │   ├── AccessLog.java         # 访问日志
│       │   ├── LoginLog.java          # 登录日志
│       │   └── OperationLog.java      # 操作日志
│       │
│       └── mq/                  # 消息队列异步处理
│           └── AbstractMessageQueueThread.java
```

### 三、Python后端模块 (ExamOnline-Server-master)

```
ExamOnline-Server-master/
├── ExamOnline/                 # Django项目配置
│   ├── settings.py             # 项目设置
│   ├── urls.py                 # URL路由
│   ├── wsgi.py
│   └── asgi.py
│
├── src/
│   ├── apps/
│   │   ├── Common/            # 公共模块
│   │   │   ├── models.py
│   │   │   └── views.py
│   │   │
│   │   ├── UserManagement/    # 用户管理
│   │   │   ├── models.py
│   │   │   ├── serializers.py
│   │   │   ├── views.py
│   │   │   └── urls.py
│   │   │
│   │   ├── ExamManagement/    # 考试管理
│   │   │   ├── models.py
│   │   │   ├── serializers.py
│   │   │   ├── views.py
│   │   │   └── urls.py
│   │   │
│   │   ├── ExamResult/        # 考试成绩
│   │   │   ├── models.py
│   │   │   ├── serializers.py
│   │   │   ├── views.py
│   │   │   └── urls.py
│   │   │
│   │   ├── QuestionManagement/ # 题库管理
│   │   │   ├── models.py
│   │   │   ├── serializers.py
│   │   │   ├── views.py
│   │   │   └── urls.py
│   │   │
│   │   ├── PaperManagement/    # 试卷管理
│   │   │   ├── models.py
│   │   │   ├── serializers.py
│   │   │   ├── views.py
│   │   │   └── urls.py
│   │   │
│   │   └── MenuManagement/     # 菜单管理
│   │       ├── models.py
│   │       ├── serializers.py
│   │       ├── views.py
│   │       └── urls.py
│   │
│   ├── middleware/            # 中间件
│   │   ├── authentication.py   # JWT认证
│   │   └── request_interceptor.py
│   │
│   └── utils/                 # 工具类
│       ├── logger_utils.py
│       ├── mapping_table.py
│       └── response_utils.py
│
├── TemplateFiles/              # 导入模板
├── ExamResultFiles/            # 成绩导出文件
└── docs_images/                # 文档图片
```

---

## 🚀 快速开始 / Quick Start

### 前置要求 / Prerequisites

| 环境 / Environment | 版本 / Version |
|-------------------|----------------|
| JDK | 8+ |
| Maven | 3.6+ |
| Node.js | 16+ |
| pnpm | 8+ |
| Python | 3.9+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Nacos | 2.2.1+ |

### 1. 克隆项目 / Clone the Project

```bash
git clone https://github.com/AlanSmithDev/ExamOnline.git
cd ExamOnline
```

### 2. MySQL数据库初始化 / Database Setup

```sql
-- 创建数据库
CREATE DATABASE examonline DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Java后端数据库导入
USE examonline;
SOURCE ExamOnline-master/eo.sql;

-- Python后端数据库导入（如果使用Python版本）
SOURCE ExamOnline-Server-master/initial_data.json;
```

### 3. Nacos配置 / Nacos Configuration

```bash
# 启动Nacos
# 访问 http://localhost:8848/nacos
# 默认账号密码: nacos / nacos

# 导入配置: ExamOnline-master/nacos.json
# 导入数据库: ExamOnline-master/nacos.sql
```

### 4. 后端启动 / Backend Startup

#### Java微服务版本 / Java Microservices Version

```bash
cd ExamOnline-master

# 依次启动以下服务（顺序很重要）：
# 1. 启动基础服务
mvn clean install
mvn spring-boot:run -pl eo-common
mvn spring-boot:run -pl eo-dubbo

# 2. 启动业务服务
mvn spring-boot:run -pl eo-service-user
mvn spring-boot:run -pl eo-service-question
mvn spring-boot:run -pl eo-service-paper
mvn spring-boot:run -pl eo-service-exam
mvn spring-boot:run -pl eo-service-message
mvn spring-boot:run -pl eo-service-depart
mvn spring-boot:run -pl eo-service-file
mvn spring-boot:run -pl eo-service-log

# 3. 启动网关
mvn spring-boot:run -pl eo-gateway
```

#### Python Django版本 / Python Django Version

```bash
cd ExamOnline-Server-master

# 创建虚拟环境
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# 安装依赖
pip install -r requirements.txt

# 数据库迁移
python manage.py migrate

# 创建超级用户
python manage.py createsuperuser

# 启动服务
python manage.py runserver 0.0.0.0:8000
```

### 5. 前端启动 / Frontend Startup

```bash
cd ExamOnline-Front-master

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev

# 构建生产版本
pnpm build
```

### 6. 访问系统 / Access the System

| 服务 / Service | 地址 / Address |
|----------------|----------------|
| 前端首页 | http://localhost:5173 |
| Java后端API | http://localhost:8080 |
| Python后端API | http://localhost:8000 |
| Nacos控制台 | http://localhost:8848/nacos |
| Swagger文档 | http://localhost:8080/doc.html |

### 默认账号 / Default Accounts

| 角色 / Role | 用户名 / Username | 密码 / Password |
|-------------|-------------------|-----------------|
| 管理员 / Admin | admin | 123456789a |
| 教师 / Teacher | teacher | 123456789a |
| 学生 / Student | student | 123456789a |

---

## 📁 项目结构 / Project Structure

```
ExamOnline/
│
├── ExamOnline-Front-master/           # Vue 3 前端项目
│   ├── docs_images/                    # 文档图片
│   ├── public/                         # 公共静态资源
│   ├── src/
│   │   ├── api/                        # API接口
│   │   ├── components/                 # 公共组件
│   │   ├── router/                     # 路由配置
│   │   ├── stores/                     # 状态管理
│   │   ├── utils/                      # 工具函数
│   │   ├── views/                      # 页面视图
│   │   ├── assets/                     # 资源文件
│   │   └── images/                     # 图片资源
│   ├── package.json
│   ├── vite.config.ts
│   └── tsconfig.json
│
├── ExamOnline-master/                  # Java 微服务后端
│   ├── eo-common/                      # 公共模块
│   ├── eo-dubbo/                       # Dubbo接口
│   ├── eo-gateway/                     # API网关
│   ├── eo-web/                         # Web公共层
│   ├── eo-service-exam/                # 考试服务
│   ├── eo-service-paper/               # 试卷服务
│   ├── eo-service-question/            # 题目服务
│   ├── eo-service-user/                 # 用户服务
│   ├── eo-service-message/             # 消息服务
│   ├── eo-service-depart/              # 部门服务
│   ├── eo-service-file/                 # 文件服务
│   ├── eo-service-log/                 # 日志服务
│   ├── eo.sql                          # 数据库脚本
│   ├── nacos.json                      # Nacos配置
│   └── nacos.sql                       # Nacos初始化数据
│
├── ExamOnline-Server-master/            # Python Django 后端
│   ├── ExamOnline/                     # Django项目配置
│   ├── src/
│   │   ├── apps/                       # 应用模块
│   │   ├── middleware/                 # 中间件
│   │   └── utils/                      # 工具类
│   ├── TemplateFiles/                  # 导入模板
│   ├── ExamResultFiles/                # 成绩导出
│   └── requirements.txt                 # Python依赖
│
├── docs_images/                         # 项目文档图片
│
└── README.md                           # 项目说明文档
```

---

## 🎯 核心功能 / Core Features

### 1. 考试管理 / Exam Management

| 功能 / Feature | 说明 / Description |
|----------------|-------------------|
| 创建考试 | 支持手动组卷和智能组卷两种方式 |
| 发布考试 | 设置考试时间、时长、参考班级 |
| 考试分析 | 统计参考人数、缺考人数、最高分、最低分、平均分、及格率 |
| 复制试卷 | 快速复制已有试卷创建新考试 |

### 2. 组卷方式 / Paper Composition

#### 手动组卷 / Manual Composition
```
教师手动选择题目添加到试卷
↓
设置每道题的分值
↓
生成试卷
```

#### 智能组卷 / Intelligent Composition
```
设置抽题规则：
- 题库范围
- 题目类型
- 抽取数量
- 每题分值
- 难度等级
↓
系统随机从题库抽取题目
↓
生成试卷
```

### 3. 题目类型 / Question Types

| 类型 / Type | 代码 / Code | 自动批改 / Auto Grade | 说明 / Description |
|-------------|-------------|----------------------|-------------------|
| 单选题 | SINGLE_CHOICE | ✅ | 标准单选题 |
| 多选题 | MULTIPLE_CHOICE | ✅ | 多选全对才得分 |
| 判断题 | JUDGEMENT | ✅ | True/False |
| 填空题 | COMPLETION | ❌ | 需要人工批改 |
| 名词解释 | NOUN_INTERPRETATION | ❌ | 需要人工批改 |
| 完形填空 | CLOZE | ✅ | 单选题集 |
| 阅读理解 | READING_COMPREHENSION | ✅ | 单选题集 |
| 听力题 | LISTENING | ✅ | 单选题集 |
| 简答题 | SHORT_ANSWER | ❌ | 需要人工批改 |

### 4. 自动阅卷 / Auto Grading

系统采用**策略模式**实现自动阅卷：

```java
@Component
public class Corrector {
    private Map<Integer, BaseCorrector> correctorMap;

    @PostConstruct
    public void init() {
        // 初始化各题型的批改策略
        correctorMap.put(QuestionType.SINGLE_CHOICE, new SingleChoiceCorrector());
        correctorMap.put(QuestionType.MULTIPLE_CHOICE, new MultipleChoiceCorrector());
        correctorMap.put(QuestionType.JUDGEMENT, new JudgementCorrector());
    }

    public boolean correct(PaperQuestion paperQuestion, PaperAnswer paperAnswer,
                          QuestionDto question, List<QuestionAnswerDto> correctAnswers) {
        BaseCorrector corrector = correctorMap.get(question.getType());
        if (corrector == null) {
            return false; // 需要人工批改
        }
        return corrector.correct(paperQuestion, paperAnswer, question, correctAnswers);
    }
}
```

### 5. 用户权限 / User Roles

| 角色 / Role | 权限 / Permissions |
|-------------|-------------------|
| 管理员 (Admin) | 全部权限 |
| 教师 (Teacher) | 考试管理、试卷管理、题目管理、成绩批改 |
| 学生 (Student) | 参加考试、查看成绩、错题本 |

### 6. 分布式ID / Distributed ID

系统使用**雪花算法**生成唯一ID：

```java
public class SnowFlakeIdWorker {
    // 起始时间戳：2022-03-10 00:00:00
    private final static long START_STMP = 1646841600000L;

    // 序列号位数：6位
    private final static long SEQUENCE_BIT = 6;

    // 机器标识位数：5位
    private final static long MACHINE_BIT = 5;

    // 数据中心位数：5位
    private final static long DATACENTER_BIT = 5;

    // 最大值
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    // 偏移量
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    // 生成ID
    public synchronized long next() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards");
        }
        if (currStmp == lastStmp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            sequence = 0L;
        }
        lastStmp = currStmp;
        return (currStmp - START_STMP) << TIMESTMP_LEFT
                | datacenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }
}
```

### 7. 实时通信 / Real-time Communication

系统使用WebSocket实现实时消息推送：

```java
@Component
@ServerEndpoint(value = "/ws/message/{token}")
public class WebSocketClient {
    private static final CopyOnWriteArraySet<WebSocketClient> CLIENTS = new CopyOnWriteArraySet<>();
    private Session session;
    private Long userId;

    @OnOpen
    public void onOpen(Session session) {
        // 验证token，建立连接
        CLIENTS.add(this);
    }

    @OnMessage
    public void onMessage(String message) {
        // 处理接收到的消息
    }

    public void sendMessage(WsMessageDto message) {
        this.session.getAsyncRemote().sendText(JsonUtils.toJson(message));
    }
}
```

---

## 🗃 数据库设计 / Database Design

### 核心表结构 / Core Tables

```sql
-- 用户表
CREATE TABLE eo_user (
    id BIGINT PRIMARY KEY,           -- 雪花ID
    user_name VARCHAR(50) NOT NULL UNIQUE,
    real_name VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,        -- admin/teacher/student
    avatar VARCHAR(255),
    birthday VARCHAR(20),
    gender INT,
    state INT DEFAULT 0,
    create_time DATETIME,
    update_time DATETIME
);

-- 考试表
CREATE TABLE eo_exam (
    id BIGINT PRIMARY KEY,
    join_type INT NOT NULL,           -- 0手动组卷 1智能组卷
    paper_id BIGINT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    state INT DEFAULT 0,               -- 0未发布 1已发布
    total_score INT,
    time_limit BOOLEAN,
    face_check BOOLEAN,                -- 人脸识别
    join_limit INT,                    -- 限时进入(分钟)
    submit_limit INT,                   -- 限时提交(分钟)
    duration INT,                      -- 考试时长
    depart_ids VARCHAR(255),           -- 考试班级
    creator BIGINT,
    start_time DATETIME,
    end_time DATETIME,
    create_time DATETIME,
    update_time DATETIME
);

-- 试卷表
CREATE TABLE eo_paper (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    total_score INT,
    time_limit BOOLEAN,
    face_check BOOLEAN,
    join_limit INT,
    submit_limit INT,
    state INT,                          -- PREPARED/PREPARING/CORRECTING/CORRECT_COMPLETED
    start_time DATETIME,
    end_time DATETIME,
    create_time DATETIME,
    update_time DATETIME
);

-- 题目表
CREATE TABLE eo_question (
    id BIGINT PRIMARY KEY,
    type INT NOT NULL,                 -- 题目类型
    difficulty INT,                    -- 难度等级 1-5
    content TEXT NOT NULL,
    voice_file VARCHAR(255),
    repo_id BIGINT,                    -- 题库ID
    parent_id BIGINT,                  -- 父题目ID（复合题）
    creator BIGINT,
    analysis TEXT,                     -- 解析
    analysis_video VARCHAR(255),
    create_time DATETIME,
    update_time DATETIME
);

-- 题目答案表
CREATE TABLE eo_question_answer (
    id BIGINT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    content VARCHAR(500),
    is_right BOOLEAN,
    create_time DATETIME,
    update_time DATETIME
);

-- 答题记录表
CREATE TABLE eo_paper_answer (
    id BIGINT PRIMARY KEY,
    paper_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT,                       -- 学生答案内容
    is_right BOOLEAN,
    score INT DEFAULT 0,
    corrector BIGINT,                  -- 批改人 0=系统
    create_time DATETIME,
    update_time DATETIME
);

-- 考试记录表
CREATE TABLE eo_paper_record (
    id BIGINT PRIMARY KEY,
    paper_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    score INT DEFAULT 0,
    state INT,                         -- PREPARED/ANSWERING/SUBMITTED/CORRECTED
    start_time DATETIME,
    end_time DATETIME,
    create_time DATETIME,
    update_time DATETIME
);
```

---

## 📚 API文档 / API Documentation

### 认证接口 / Authentication

| 接口 / API | 方法 / Method | 说明 / Description |
|-----------|--------------|-------------------|
| `/api/user/login` | POST | 用户登录 |
| `/api/user/register` | POST | 用户注册 |
| `/api/user/logout` | GET | 用户登出 |
| `/api/user/current` | GET | 获取当前用户信息 |

### 考试接口 / Exam APIs

| 接口 / API | 方法 / Method | 说明 / Description |
|-----------|--------------|-------------------|
| `/api/exam/my` | POST | 获取用户的考试列表 |
| `/api/exam/list` | POST | 获取用户创建的考试列表 |
| `/api/exam/create` | POST | 创建考试 |
| `/api/exam/save` | POST | 保存考试 |
| `/api/exam/info/{examId}` | GET | 获取考试信息 |
| `/api/exam/release/{examId}` | GET | 发布考试 |
| `/api/exam/delete/{examId}` | GET | 删除考试 |
| `/api/exam/paper/generate/{examId}` | GET | 生成考试试卷 |
| `/api/exam/analysis/{examId}` | GET | 考试分析 |

### 试卷接口 / Paper APIs

| 接口 / API | 方法 / Method | 说明 / Description |
|-----------|--------------|-------------------|
| `/api/paper/detail/{paperId}` | GET | 获取试卷详情 |
| `/api/paper/start/{paperId}` | GET | 开始答题 |
| `/api/paper/submit/{paperId}` | GET | 提交试卷 |
| `/api/paper/answer/save` | POST | 保存答题记录 |
| `/api/paper/correct` | POST | 批改试题 |
| `/api/paper/records/page` | POST | 分页查询成绩记录 |

### 题目接口 / Question APIs

| 接口 / API | 方法 / Method | 说明 / Description |
|-----------|--------------|-------------------|
| `/api/question/add` | POST | 添加题目 |
| `/api/question/save` | POST | 保存题目 |
| `/api/question/search` | POST | 分页搜索题目 |
| `/api/question/delete/{questionId}` | GET | 删除题目 |
| `/api/question/import/{repoId}` | POST | 从Excel导入试题 |

---

## 📋 部署指南 / Deployment Guide

### Docker部署 / Docker Deployment

```dockerfile
# Java服务Dockerfile
FROM maven:3.8-openjdk-8 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:8-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: examonline
    ports:
      - "3306:3306"

  redis:
    image: redis:6.2
    ports:
      - "6379:6379"

  nacos:
    image: nacos/nacos-server:v2.2.1
    environment:
      MODE: standalone
    ports:
      - "8848:8848"

  exam-service:
    build: ./ExamOnline-master
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
      - nacos

  exam-frontend:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./ExamOnline-Front-master/dist:/usr/share/nginx/html
```

### 环境变量配置 / Environment Variables

```bash
# Java后端
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/examonline
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root123
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
NACOS_SERVER_ADDR=localhost:8848

# 前端
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws/message
```

---

## 🤝 贡献指南 / Contributing

1. **Fork 本项目**
2. **创建分支** (`git checkout -b feature/AmazingFeature`)
3. **提交更改** (`git commit -m 'Add some AmazingFeature'`)
4. **推送到分支** (`git push origin feature/AmazingFeature`)
5. **创建 Pull Request**

---

## 📄 许可证 / License

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 作者 / Author

**Alan Smith (石文盛)**
- GitHub: [@AlanSmithDev](https://github.com/AlanSmithDev)

---

## 🙏 致谢 / Acknowledgments

感谢所有为这个项目做出贡献的开发者！

Thanks to all developers who have contributed to this project!

---

<div align="center">

**如果这个项目对您有帮助，请给我一个 ⭐**

**If this project is helpful to you, please give me a ⭐**

</div>

---

## English Version

# ExamOnline Online Examination System

<div align="center">

![Logo](docs_images/logo.png)

[![Star](https://img.shields.io/github/stars/AlanSmithDev/ExamOnline?style=social)](https://github.com/AlanSmithDev/ExamOnline/stargazers)
[![Fork](https://img.shields.io/github/forks/AlanSmithDev/ExamOnline?style=social)](https://github.com/AlanSmithDev/ExamOnline/network/members)
[![License](https://img.shields.io/github/license/AlanSmithDev/ExamOnline)](https://github.com/AlanSmithDev/ExamOnline/blob/main/LICENSE)

</div>

---

## Table of Contents

- [Project Introduction](#project-introduction)
- [Project Architecture](#project-architecture)
- [Technology Stack](#technology-stack)
- [Module Details](#module-details)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Core Features](#core-features)
- [Database Design](#database-design)
- [API Documentation](#api-documentation)
- [Deployment Guide](#deployment-guide)
- [Contributing](#contributing)
- [License](#license)

---

## Project Introduction

ExamOnline is a comprehensive online examination system featuring a separated frontend and backend architecture. The backend includes both Java microservice version and Python Django version implementations.

**Key Features:**
- Multiple question types support (Single choice, Multiple choice, True/False, Fill-in-blank, etc.)
- Intelligent and manual paper composition
- Automatic grading for objective questions
- Manual grading support for subjective questions
- Score analysis and statistics
- Face recognition for exam authentication
- Real-time messaging via WebSocket
- JWT-based authentication

---

## Project Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           ExamOnline System Architecture                     │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐                      │
│  │  Web Client │   │ Mobile App  │   │WeChat Mini  │                      │
│  │ (Vue 3+TS)  │   │  (Native)   │   │  Program    │                      │
│  └──────┬──────┘   └──────┬──────┘   └──────┬──────┘                      │
│         └─────────────────┼─────────────────┘                                │
│                           ▼                                                   │
│              ┌─────────────────────────────┐                                 │
│              │       API Gateway          │                                 │
│              │  (Spring Cloud Gateway)    │                                 │
│              └─────────────────────────────┘                                 │
│                           │                                                   │
│         ┌─────────────────┼─────────────────┐                              │
│         ▼                 ▼                 ▼                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                        │
│  │Java Backend │  │Python Backend│  │    Nginx    │                        │
│  │(Spring Cloud)│  │   (Django)  │  │Load Balance │                        │
│  └─────────────┘  └─────────────┘  └─────────────┘                        │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Technology Stack

### Frontend
| Technology | Version | Description |
|------------|---------|-------------|
| Vue | 3.4.38 | Progressive JS Framework |
| TypeScript | 5.2.2 | Typed JavaScript |
| Vite | 5.1.4 | Build Tool |
| Element Plus | 2.6.1 | UI Component Library |
| Pinia | 2.1.7 | State Management |
| Vue Router | 4.3.0 | Routing |

### Java Backend
| Technology | Version | Description |
|------------|---------|-------------|
| Spring Boot | 2.2.5.RELEASE | Application Framework |
| Spring Cloud | Hoxton.RELEASE | Microservices Framework |
| Nacos | 2.2.1.RELEASE | Service Registry |
| Dubbo | - | RPC Framework |
| MyBatis-Plus | 3.5.1 | ORM Framework |
| Redis | - | Cache |
| JWT | 0.9.1 | Authentication |

### Python Backend
| Technology | Version | Description |
|------------|---------|-------------|
| Django | 4.2.8 | Web Framework |
| DRF | 3.14.0 | REST API |
| MySQLclient | 2.1.0 | MySQL Driver |
| simplejwt | 5.3.1 | JWT Auth |

---

## Quick Start

### Prerequisites
- JDK 8+
- Maven 3.6+
- Node.js 16+
- Python 3.9+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.2.1+

### Installation

```bash
# Clone repository
git clone https://github.com/AlanSmithDev/ExamOnline.git
cd ExamOnline

# Database setup
mysql -u root -p < ExamOnline-master/eo.sql

# Start Nacos
# Access http://localhost:8848/nacos

# Backend (Java)
cd ExamOnline-master
mvn spring-boot:run

# Frontend
cd ExamOnline-Front-master
pnpm install
pnpm dev
```

### Default Accounts
| Role | Username | Password |
|------|----------|----------|
| Admin | admin | 123456789a |
| Teacher | teacher | 123456789a |
| Student | student | 123456789a |

---

## Core Features

### Question Types

| Type | Code | Auto Grade |
|------|------|------------|
| Single Choice | SINGLE_CHOICE | ✅ |
| Multiple Choice | MULTIPLE_CHOICE | ✅ |
| True/False | JUDGEMENT | ✅ |
| Fill-in-blank | COMPLETION | ❌ |
| Noun Interpretation | NOUN_INTERPRETATION | ❌ |
| Cloze | CLOZE | ✅ |
| Reading Comprehension | READING_COMPREHENSION | ✅ |
| Listening | LISTENING | ✅ |
| Short Answer | SHORT_ANSWER | ❌ |

### Paper Composition

**Manual Composition:** Teachers manually select questions and add them to the paper.

**Intelligent Composition:** Set extraction rules (question bank, type, quantity, difficulty) and the system randomly selects questions.

### Auto Grading

The system uses the **Strategy Pattern** for automatic grading:

```java
@Component
public class Corrector {
    private Map<Integer, BaseCorrector> correctorMap;

    public boolean correct(PaperQuestion paperQuestion, PaperAnswer paperAnswer,
                          QuestionDto question, List<QuestionAnswerDto> correctAnswers) {
        BaseCorrector corrector = correctorMap.get(question.getType());
        if (corrector == null) {
            return false; // Requires manual grading
        }
        return corrector.correct(paperQuestion, paperAnswer, question, correctAnswers);
    }
}
```

---

## API Documentation

| API | Method | Description |
|-----|--------|-------------|
| `/api/user/login` | POST | User login |
| `/api/exam/create` | POST | Create exam |
| `/api/exam/release/{examId}` | GET | Publish exam |
| `/api/paper/start/{paperId}` | GET | Start exam |
| `/api/paper/submit/{paperId}` | GET | Submit paper |
| `/api/question/add` | POST | Add question |

---

## Deployment Guide

### Docker Deployment

```bash
# Build and run with Docker
docker-compose up -d
```

### Environment Variables

```bash
# Java Backend
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/examonline
SPRING_REDIS_HOST=localhost

# Frontend
VITE_API_BASE_URL=http://localhost:8080
```

---

## Contributing

1. Fork the Project
2. Create Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit Changes (`git commit -m 'Add AmazingFeature'`)
4. Push to Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Author

**Alan Smith**
- GitHub: [@AlanSmithDev](https://github.com/AlanSmithDev)

---

<div align="center">

**If this project is helpful to you, please give me a ⭐**

</div>