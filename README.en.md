## Memory 聚合搜索平台

## 项目概述

### 项目名称

### 使用场景

- 👥 企业内部多项目数据搜索：该平台能够满足企业内部多个项目的数据搜索需求，避免每个项目都单独开发搜索功能，提升开发效率并降低系统维护成本。
- 📚 多源内容聚合搜索：当需要聚合不同来源、不同类型的内容时，该平台可以提供一站式的搜索页面，便于用户快速查找所需信息，提高工作效率。
- 💼 企业级搜索需求：对于有大规模搜索需求的企业，该平台提供了稳定的、高效的搜索功能，满足企业的搜索需求，并支持数据源接入和管理。

### 主要功能

- 💡 企业级一站式聚合搜索平台，整合了多个数据源的内容，并提供统一的搜索页面和界面。
- 🚀 提供高效的检索功能，用户可以在同一页面中搜索不同来源、不同类型的内容，提升检索效率和搜索体验。
- 🌐 基于 Vue3 前端和 Spring Boot 后端，利用 Elastic Stack 实现全栈项目，确保稳定可靠的运行。

### 项目启动

- 拉取代码后，应该如何运行该项目？

#### 后端

- 配置MySQL、Redis、Elasticsearch 为本机地址：

```yaml
  # MySQL配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/xxx
    username: xxx
    password: xxx
```

```yaml
  # Redis 配置
  redis:
    database: 0
    host: localhost
    port: 6379
    timeout: 5000
    password: Dw990831
```

```yaml
  # ES 配置
 elasticsearch:
    uris: http://localhost:9200
    username: root
    password: 123456
```

#### 前端

- 确保本地 **Node.js 环境配置**完成，版本为 v18.xx
- 修改请求的**后端地址**：

```react
const instance = axios.create({
    baseURL: "http://localhost:8081/api",
    withCredentials: true,
    timeout: 10000,
});
```

- 执行以下命令，一键启动前端项目：

```
npm run serve
```

## 技术选型

### 前端

### 后端

## 项目结构

### 架构设计

### 功能模块

#### 用户中心

- 用户可以在用户中心进行个人资料设置，包括上传头像、编辑个人简介等
- 用户还可以搜索其他用户，并添加他们为好友
- 在用户中心，用户可以管理自己的好友列表，查看好友的动态和在线状态
- 用户还可以自由组队，结交志同道合的朋友。他们可以创建和加入兴趣小组，参加线上线下的活动，分享经验和爱好。

#### 聊天大厅

- 简单清新的聊天页面
- 快速编辑消息，在线聊天
- 聊天大厅是一个实时双向通信的地方，用户可以和在线好友进行即时聊天。
- 用户可以发送文本消息、表情、图片和语音消息，还可以进行群聊。
- 聊天大厅还提供在线状态显示和消息通知功能，让用户能够随时随地与好友保持联系。

#### 博文社区

- 在博文社区，用户可以浏览、发布和评论博文。
- 用户可以写长篇博客、发表心情随笔或分享图片等。
- 其他用户可以对博文进行点赞和评论，从而与作者进行互动和交流。
- 用户还可以根据标签或关键词搜索感兴趣的博文，以扩大知识和交友圈子。

## 效果展示

![image-20231001210412461](C:\Users\Lenovo\AppData\Roaming\Typora\typora-user-images\image-20231001210412461.png)

![image-20231001210420054](C:\Users\Lenovo\AppData\Roaming\Typora\typora-user-images\image-20231001210420054.png)

![image-20231001210426079](C:\Users\Lenovo\AppData\Roaming\Typora\typora-user-images\image-20231001210426079.png)