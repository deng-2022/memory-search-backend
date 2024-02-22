# MemorySearch 忆搜阁

## 项目概述

这个项目是一个基于 **Spring Boot + Elastic Stack 技术栈 + Vue.js** 的`聚合搜索中台`。它不仅是一个强大的搜索引擎，更是一个内容丰富的社区平台。

这个项目的目标是提供一个**一站式的搜索、管理和互动体验**，满足各种用户需求。

## 使用场景

- 👥 企业内部多项目数据搜索：该平台能够满足企业内部多个项目的数据搜索需求，避免每个项目都单独开发搜索功能，提升开发效率并降低系统维护成本。
- 📚 多源内容聚合搜索：当需要聚合不同来源、不同类型的内容时，该平台可以提供一站式的搜索页面，便于用户快速查找所需信息，提高工作效率。
- 💼 企业级搜索需求：对于有大规模搜索需求的企业，该平台提供了稳定的、高效的搜索功能，满足企业的搜索需求，并支持数据源接入和管理。

## 🥣 核心功能与特点

-  **高效多元搜索** ：用户可以在搜索框中输入关键词，系统会提供快速、准确的搜索结果。搜索结果会根据内容类型（文本、图片、视频）进行分类展示，并提供关键词高亮和搜索建议，使用户能快速找到所需内容。

-  **互动创作平台** ：用户可以在这个模块中发布文章、上传图片，与其他用户互动。系统会自动推荐热门内容，引导用户发现更多优质内容。用户还可以对文章、图片进行点赞、评论和收藏，形成一个活跃的内容创作社区。

-  **流量统计分析** ：系统会自动统计每个关键词的搜索流量，并按照时间、关键词类型等维度进行分析。用户可以查看热搜词类别和搜索流量高峰，了解内容趋势和用户行为。

-  **个人中心管理** ：用户可以在个人中心查看和编辑个人信息，包括头像、昵称、简介等。用户还可以查看自己的点赞、评论和收藏的内容，以及自己创作的文章和下载的图片、视频等。

-  **资源全面管理** ：这个模块仅对管理员可见，管理员可以对全站资源（文章、图片、视频、用户等）进行全面管理。管理员可以对资源进行添加、删除、修改等操作，保证资源的准确性和完整性。

-  **图片预览分享** ：通过集成的图片预览功能，用户可以像浏览相册一样查看页面中的图片，并支持缩放和分享到社交媒体平台。

## 🍜 访问地址

暂未部署上线，点击跳转至：[MemorySearch 忆搜阁开发文档](https://deng-2022.gitee.io/blog/2023/08/26/Memory%20%E8%81%9A%E5%90%88%E6%90%9C%E7%B4%A2%E5%B9%B3%E5%8F%B0-%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/)

## 🍝 架构设计

原图链接：[项目架构图](https://www.yuque.com/huiyiruchu-7jxo6/ckx99i/lxw5okzip8hktuln?singleDoc#)

![image-20240221231907060](https://gitee.com/deng-2022/pictures/raw/master/images/image-20240221231907060.png)

## 🍺 技术选型

### 前端

- Vue 3
- Ant Design Vue 组件库
- 页面状态同步机制

### 后端

- Spring Boot 2.7 框架
- springboot-memory-init 项目模板
- MySQL 数据库
- Redis 缓存
- Elastic Stack

  - Elasticsearch 搜索引擎
  - Logstash 数据管道
  - Kibana 数据可视化

- 数据抓取

  - 离线和实时抓取
  - Jsoup 和 HttpClient 库

- 设计模式

  - 门面模式
  - 适配器模式
  - 注册器模式

- 数据同步

  - 同步双写
  - 异步双写
  - 基于 SQL 抽取
  - 基于 Binlog 实时同步

- Jmeter 压力测试

## 🍰 快速启动

<Badge text="beta" type="warning"/>

<Badge text="Vdoing主题"/>

拉取代码后， 如何`快速运行该项目`？

### 后端

- 配置 MySQL、Redis、Elasticsearch 为本机地址：

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

#### 额外安装

- 在本地安装 Elasticsearch、Kibana、Logstash
- 在 `ES 的 bin 目录`下执行以下命令，启动 ES：

```bash
Elasticsearch.bat
```

- 在 `Kibana 的 bin 目录`下执行以下命令，启动 Kibana：

```bash
Kibana.bat
```

- 在 `Logstash 的 config 目录`下新增 .conf 文件，编写配置文件，做好数据映射（以下配置信息可作为参考）

```bash
# Sample Logstash configuration for receiving
# UDP syslog messages over port 514

input {
  jdbc {
    jdbc_driver_library => "D:\softWare\logstash\logstash-7.17.9\config\mysql-connector-java-8.0.29.jar"
    jdbc_driver_class => "com.mysql.jdbc.Driver"
    jdbc_connection_string => "jdbc:mysql://localhost:3306/******"
    jdbc_user => "******"
    jdbc_password => "******"
    statement => "SELECT * from post where updateTime > :sql_last_value and updateTime < now() order by updateTime desc"
    use_column_value => true
    tracking_column_type => "timestamp"
    tracking_column => "updatetime"
    schedule => "*/5 * * * * *"
    jdbc_default_timezone => "Asia/Shanghai"
  }
}

filter {
  mutate {
    rename => {
      "updatetime" => "updateTime"
      "userid" => "userId"
      "createtime" => "createTime"
      "isdelete" => "isDelete"
    }
    remove_field => ["thumbnum", "favournum"]
  }
}

output {
  stdout { codec => rubydebug }

  elasticsearch {
    hosts => "127.0.0.1:9200"
    index => "******"
    document_id => "%{id}"
  }
}
```

- 在 Logstash 的`根目录`下执行以下命令，**加载配置文件并启动 Logstash**：

```bash
.\bin\logstash.bat -f .\config\myTask.conf
```

### 前端

::: warning 注意
确保本地 `Node.js 环境配置`完成，版本为 `v18.x.x`及以上
:::

- 根据`后端接口文档`，一键生成前端 HTTP 请求接口：

> 🍖 官方文档：[ferdikoomen/openapi-typescript-codegen (github.com)](https://github.com/ferdikoomen/openapi-typescript-codegen)

安装：

```bash
npm install openapi-typescript-codegen --save-dev
```

执行命令生成代码：

```bash
openapi --input http://localhost:8104/api/v2/api-docs?group=memory-search --output ./generated --client axios
```

- 执行成功后，在 OpenAPI.ts 文件下，修改请求的后端地址：

```ts
export const OpenAPI: OpenAPIConfig = {
  BASE: "http://localhost:8104",
  VERSION: "1.0",
  WITH_CREDENTIALS: true,
  CREDENTIALS: "include",
  TOKEN: undefined,
  USERNAME: undefined,
  PASSWORD: undefined,
  HEADERS: undefined,
  ENCODE_PATH: undefined,
};
```

- 执行以下命令，`一键启动`前端项目：

```bash
npm run serve
```

## 🥘 效果展示

### 用户登录

![image-20240222202351543](https://gitee.com/deng-2022/pictures/raw/master/images/image-20240222202351543.png)

### 图片搜索

![image-20240222203543310](https://gitee.com/deng-2022/pictures/raw/master/images/image-20240222203543310.png)

### 文章上传

![image-20240222203451920](https://gitee.com/deng-2022/pictures/raw/master/images/image-20240222203451920.png)

### 统计分析

![image-20240222213338497](https://gitee.com/deng-2022/pictures/raw/master/images/image-20240222213338497.png)
