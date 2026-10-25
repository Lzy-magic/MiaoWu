# 喵呜（MiaoWu）流浪动物救助平台

[English](README.md) | **简体中文**

喵呜是一个流浪动物救助与领养平台：统一管理动物信息，用户可以发布所发现、领养动物的文章和动态，并支持关注用户、评论、点赞、收藏文章和收藏动物。

仓库包含两个部分：

- **MiaoWuServer**：基于 Spring Boot + MyBatis 的 RESTful 后端。
- **MiaoWuWeb**：基于 Vue 2 + Element UI 的后台风格前端（基于 vue-element-admin）。

---

## 目录

- [功能](#功能)
- [架构与技术栈](#架构与技术栈)
- [目录结构](#目录结构)
- [环境要求](#环境要求)
- [安装部署](#安装部署)
- [配置说明](#配置说明)
- [运行后端](#运行后端)
- [运行前端](#运行前端)
- [国际化（i18n）](#国际化i18n)
- [接口概览](#接口概览)
- [已知问题](#已知问题)
- [许可证](#许可证)

---

## 功能

以下模块均来自 `MiaoWuServer/src/main/java/com/apps/miaowu/web/controller`：

| 模块 | 控制器 | 功能 |
|---|---|---|
| 用户 | `UserController` | 注册、登录/退出（Token 存于 Redis）、获取用户信息、更新、删除、用户列表 |
| 文章 | `ArticleController` | 文章增删改查、分页、基于 Elasticsearch 的标题模糊搜索、按作者查询、关注用户的文章、带收藏状态的文章列表 |
| 动物 | `AnimalController` | 动物列表、按 id 查询、更新、查询用户收藏/发现的动物 |
| 评论 | `CommentController` | 新增、修改、删除文章评论 |
| 收藏文章 | `ClipController` | 收藏/取消收藏文章，查询用户收藏的文章 |
| 关注 | `FollowController` | 关注/取消关注（切换），查询关注列表和粉丝列表 |
| 收藏动物 | `SaveController` | 收藏/取消收藏动物（切换） |
| 点赞 | `ThumbController` | 文章点赞 |
| 标签 | `LabelController` | 为文章添加标签 |
| 图片 | `ImgController` | 上传图片（保存在 servlet context 路径下）、删除图片 |

通用机制：

- **认证**：`LoginInterceptor` 校验请求头 `Authorization` 中的 Token（与 Redis 比对），标注 `@NoneAuth` 的接口无需登录。
- **消息队列**：`AnimalServiceImpl` 通过 RabbitMQ 发送更新消息并监听 `update_queue` 队列；`common/utils/rabbitMQ` 中另有示例 `Producer`/`Consumer`。
- **搜索**：`ArticleServiceImpl` 使用 Elasticsearch `RestHighLevelClient` 进行文章模糊搜索。
- **接口文档**：`config/Swagger2.java` 配置了 Swagger 2（springfox 2.9.2），地址 `/swagger-ui.html`。
- **跨域**：`WebConfig` 允许所有来源。

## 架构与技术栈

```
浏览器 --> MiaoWuWeb (Vue 2, 9527 端口) --HTTP/JSON--> MiaoWuServer (Spring Boot, 8088 端口)
                                                         |-- MySQL / MariaDB (3306)  MyBatis 访问
                                                         |-- Redis (6379)            登录 Token / 缓存
                                                         |-- RabbitMQ (5672)         动物更新队列
                                                         '-- Elasticsearch (9200)    文章搜索（可选）
```

### 后端（`MiaoWuServer/pom.xml`）

| 组件 | 版本 |
|---|---|
| Java | 1.8 |
| Spring Boot（parent） | 2.2.4.RELEASE |
| Spring Cloud 依赖（BOM） | Hoxton.SR1 |
| Spring Web、Security、AMQP、Data Elasticsearch、DevTools | 由 Boot 2.2.4 管理 |
| MyBatis Spring Boot Starter | 2.1.1 |
| mysql-connector-java | 8.0.21 |
| spring-data-redis / Jedis | 2.2.5.RELEASE / 3.1.0 |
| elasticsearch-rest-client | 7.8.1 |
| springfox-swagger2 / swagger-ui | 2.9.2 |
| Lombok | 1.18.12 |
| fastjson | 1.2.58 |
| commons-lang3 / commons-io | 3.9 / 2.4 |
| 日志 | log4j2（`spring-boot-starter-log4j2`，配置文件 `log4j2.yml`） |

### 前端（`MiaoWuWeb/package.json`）

| 组件 | 版本 |
|---|---|
| Vue | 2.6.10 |
| Vue Router / Vuex | 3.0.2 / 3.1.0 |
| Element UI | 2.13.0 |
| axios | 0.18.1 |
| ECharts | 4.2.1 |
| @vue/cli-service | 3.5.3 |
| sass (dart-sass) / sass-loader | ~1.32.13 / ^10.4.1 |

## 目录结构

```
MiaoWu/
|-- README.md / README.zh-CN.md
|-- MiaoWuServer/                       Spring Boot 后端
|   |-- pom.xml
|   '-- src/main/
|       |-- java/com/apps/miaowu/
|       |   |-- MiaowuApplication.java  启动类
|       |   |-- annotation/             @NoneAuth
|       |   |-- bean/                   实体类与 MyBatis Example 类、extend/、result/（APIResult）
|       |   |-- common/                 异常、抽象 Service、工具类（Token、RabbitMQ）
|       |   |-- config/                 ElasticSearch、MyBatis、Spring、Swagger2、跨域
|       |   |-- constant/               NormalConstant
|       |   |-- dao/                    MyBatis Mapper 接口（含 extend/）
|       |   |-- service/（含 impl/）    业务逻辑
|       |   '-- web/controller、web/interceptor
|       '-- resources/
|           |-- application.yml
|           |-- log4j2.yml
|           |-- generatorConfig.xml     MyBatis Generator 配置
|           |-- mapper/（含 extend/）   MyBatis XML 映射
|           '-- sql/schema.sql          反推得到的数据库结构
'-- MiaoWuWeb/                          Vue 2 前端（基于 vue-element-admin）
    |-- package.json、vue.config.js、.env.*
    |-- mock/                           开发服务器使用的 mock 服务
    '-- src/
```

## 环境要求

| 工具 | 版本 | 说明 |
|---|---|---|
| JDK | 8 | 如 Temurin 8 或 OpenJDK 8 |
| Maven | 3.6.x | 仓库**没有** Maven Wrapper（缺少 `.mvn/`），请使用系统 `mvn` |
| MySQL / MariaDB | MySQL 5.7+ 或 MariaDB 10.x+ | 数据库 `miaowu`，utf8mb4 |
| Redis | 5+ | 存储登录 Token |
| RabbitMQ | 3.x | 启动时必需（存在 `@RabbitListener`） |
| Elasticsearch | 7.x，**可选** | 仅用于文章模糊搜索，不启动也能运行 |
| Node.js | 16（通过 nvm） | 使用 dart-sass，无需本地编译 |
| Lombok IDE 插件 | - | IDE 中编译需要 |

## 安装部署

```bash
git clone https://github.com/Lzy-magic/MiaoWu.git
cd MiaoWu
```

### 1. 数据库

```sql
CREATE DATABASE miaowu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER '<db_user>'@'localhost' IDENTIFIED BY '<db_password>';
CREATE USER '<db_user>'@'127.0.0.1' IDENTIFIED BY '<db_password>';
GRANT ALL ON miaowu.* TO '<db_user>'@'localhost';
GRANT ALL ON miaowu.* TO '<db_user>'@'127.0.0.1';
FLUSH PRIVILEGES;
```

导入表结构：

```bash
mysql -h127.0.0.1 -u<db_user> -p miaowu < MiaoWuServer/src/main/resources/sql/schema.sql
```

> **注意：** 项目原本没有提供建表 SQL。`schema.sql` 是根据 MyBatis Mapper XML 和实体类**反推**得到的：字段名与 Mapper 一致，但字段类型、长度和索引为合理推测。共 12 张表：`country`、`user`、`animal`、`article`、`label`、`article_label`、`comment_table`、`clip`、`follow`、`found_table`、`save`、`thumb_up`。

### 2. Redis 与 RabbitMQ

在 `127.0.0.1:6379` 启动 Redis；在 `127.0.0.1:5672` 启动 RabbitMQ，并创建 `application.yml` 中配置的用户：

```bash
sudo rabbitmqctl add_user <mq_user> '<mq_password>'
sudo rabbitmqctl set_permissions -p / <mq_user> ".*" ".*" ".*"
```

### 3. Elasticsearch（可选）

`ElasticSearchConfig` 从 `miaowu.elasticsearch.host` / `port` / `scheme` 读取 REST 地址（默认 `localhost` / `9200` / `http`，可用环境变量 `ES_HOST`、`ES_PORT`、`ES_SCHEME` 或 `--miaowu.elasticsearch.port=...` 覆盖）。请使用 HTTP 端口 9200，而不是 transport 端口 9300。未启动时只有模糊搜索接口会失败。

## 配置说明

`MiaoWuServer/src/main/resources/application.yml`（请填写自己的值，不要提交真实密码）：

| 配置项 | 仓库默认值 | 含义 |
|---|---|---|
| `server.port` | `8088` | HTTP 端口 |
| `spring.datasource.driver-class-name` | `com.mysql.jdbc.Driver` | JDBC 驱动 |
| `spring.datasource.url` | `jdbc:mysql://127.0.0.1:3306/miaowu?charactterEncoding=utf-8` | JDBC 地址（有拼写错误，见已知问题） |
| `spring.datasource.username` / `password` | `<db_user>` / `<db_password>` | 数据库账号 |
| `spring.security.user.name` / `password` | `<user>` / `<password>` | Spring Security 默认用户 |
| `spring.redis.host` / `port` / `database` / `timeout` | `127.0.0.1` / `6379` / `0` / `1000s` | Redis 连接 |
| `spring.redis.jedis.pool.*` | max-idle 500、min-idle 50、max-wait -1、max-active -1 | Jedis 连接池 |
| `spring.cache.redis.time-to-live` | `-1` | 缓存过期时间 |
| `spring.rabbitmq.host` / `port` / `username` / `password` | `127.0.0.1` / `5672` / `<mq_user>` / `<mq_password>` | RabbitMQ |
| `miaowu.elasticsearch.host` / `port` / `scheme` | `${ES_HOST:localhost}` / `${ES_PORT:9200}` / `${ES_SCHEME:http}` | Elasticsearch REST 地址 |
| `mybatis.mapper-locations` | `classpath:/mapper/**/*.xml` | Mapper XML 路径 |
| `eureka.*` | 客户端已禁用 | 遗留的 Eureka 配置 |
| `logging.config` | `classpath:log4j2.yml` | 日志配置 |

不修改文件也可以通过命令行覆盖任意配置，例如 `--spring.datasource.password=...`。

## 运行后端

仓库中没有 `mvnw` Wrapper，请直接使用 Maven：

```bash
cd MiaoWuServer
mvn -DskipTests package
mvn spring-boot:run
```

**新版 MySQL / MariaDB 提示：** 如果启动或首次查询时报 `Communications link failure` / `SSLHandshakeException: No appropriate protocol`，可以通过覆盖 URL 关闭 SSL（无需修改文件）：

```bash
mvn spring-boot:run \
  "-Dspring-boot.run.arguments=--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/miaowu?characterEncoding=utf-8&useSSL=false"
```

MySQL 8 可能还需要添加 `&serverTimezone=Asia/Shanghai`（或你所在的时区）。

验证：

```bash
curl http://localhost:8088/animals
curl http://localhost:8088/articles/1
```

Swagger UI：`http://localhost:8088/swagger-ui.html`。

### 运行后端测试

```bash
cd MiaoWuServer
mvn test
```

测试需要本地 MySQL `miaowu` 库、Redis 和 RabbitMQ（与运行应用相同），但不依赖已有数据：每个测试前由 `src/test/resources/sql/test-seed.sql` 插入 id 为 `900001` 的测试用户（占位手机号 `19900000000`）、动物和文章，测试后由 `test-cleanup.sql` 删除。Elasticsearch 不可用时，ES 测试会通过 JUnit `Assumptions` 自动跳过。

## 运行前端

```bash
cd MiaoWuWeb
nvm install 16 && nvm use 16
npm install
npm run dev        # http://localhost:9527
```

其他脚本：`npm run build:prod`、`npm run build:stage`、`npm run lint`、`npm run test:unit`。

**Sass：** 样式使用 dart-sass（`sass` + `sass-loader@10`）编译，Node 16 下直接 `npm install` 即可。深度选择器请使用 `::v-deep`（dart-sass 不支持 `/deep/` 和 `>>>`）。

**Lint：** `npm run lint` 会检查整个 `src/`（`.eslintignore` 中已不再排除）。

**与后端的连接（开发代理）：** 接口请求发往 `VUE_APP_BASE_API`（`.env.development` 中为 `/dev-api`），开发服务器把该前缀代理到后端并去掉前缀，例如 `http://localhost:9527/dev-api/animals` -> `http://localhost:8088/animals`。可在 `.env.development`（或 `.env.development.local` / 环境变量）中配置：

| 变量 | 默认值 | 说明 |
|---|---|---|
| `VUE_APP_PROXY_TARGET` | `http://localhost:8088` | 代理转发的后端地址 |
| `VUE_APP_USE_MOCK` | `true` | 设为 `false` 关闭 mock 服务；mock 路由位于 `/dev-api/vue-element-admin/...`，优先于代理匹配 |
| `VUE_APP_API_URL` | 空 | 设置后（如 `http://localhost:8088/`）前端直接请求后端，不走代理。`.env.production` / `.env.staging` 已设置，因为打包产物没有开发代理 |

示例：`VUE_APP_USE_MOCK=false npm run dev`。

## 国际化（i18n）

前端使用 [vue-i18n](https://kazupon.github.io/vue-i18n/) v8，默认语言为英文（`en`），同时提供简体中文（`zh-CN`）。

- 语言文件位于 `MiaoWuWeb/src/lang/`：`index.js` 创建 i18n 实例，`en.js` 与 `zh-CN.js` 按命名空间（`common`、`route`、`navbar`、`article`、`animal`、`user`、`login`、`profile` 等）存放文案。
- 初始语言优先取已保存的选择（cookie / `localStorage` 键 `language`），其次是浏览器语言，最后回退到 `en`。
- 通过导航栏的地球图标（`LangSelect`）切换语言，选择会被保存；Element UI 组件、路由标题、面包屑、标签页和页面标题都会随之切换。
- 路由 `meta.title` 为 `route.*` 下的键，由 `src/utils/i18n.js` 中的 `generateTitle` 翻译。

**新增文案键：**在 `en.js` 和 `zh-CN.js` 的对应命名空间中同时添加，然后在模板/组件中使用 `$t('namespace.key')`，在普通 JS 中引入 `@/lang` 使用 `i18n.t(...)`。

**新增语言：**创建 `src/lang/<code>.js`（键与现有文件一致），在 `src/lang/index.js` 中连同对应的 Element UI 语言包一起引入，加入 `messages`（`supportedLanguages` 由其自动生成），并为其添加 `langSelect.name`。

**规则：代码中不得出现中文。**源代码、注释、日志和接口消息必须使用英文，中文只允许出现在 `MiaoWuWeb/src/lang/zh-CN.js` 和 `README.zh-CN.md` 中。检查命令：

```bash
rg -n '[\x{4e00}-\x{9fff}]' -g '!node_modules' -g '!target' -g '!dist' -g '!*.log' \
  -g '!**/lang/zh-CN.js' -g '!**/README.zh-CN.md' -g '!**/README.ja.md'
```

## 接口概览

所有响应都包装为 `APIResult`（`{ code, message, data, success }`）。标记为“公开”的接口带有 `@NoneAuth`，其余接口需要携带 `/users/login` 返回的 `Authorization` Token。

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| POST | `/users` | 公开 | 注册 |
| POST | `/users/login` | 公开 | 登录（`phone`、`password`），返回 Token |
| POST | `/users/logout` | 公开 | 退出（`token`） |
| GET | `/users/info` | 公开 | 根据 `token` 获取用户信息 |
| GET | `/users/{id}` | 公开 | 按 id 查询用户 |
| GET | `/users` | Token | 用户列表 |
| PUT | `/users` | Token | 更新用户 |
| DELETE | `/users/{id}` | Token | 删除用户 |
| GET | `/articles` | 公开 | 全部文章 |
| GET | `/articles/{id}` | 公开 | 文章详情（含评论） |
| GET | `/articles/page={page}&count={count}` | 公开 | 分页 |
| GET | `/articles/key={key}&value={value}` | 公开 | 模糊搜索（Elasticsearch） |
| GET | `/articles/authorId={authorId}` | 公开 | 某作者的文章 |
| GET | `/articles/fansId={fansId}` | 公开 | `fansId` 所关注用户的文章 |
| GET | `/articles/with=clips&userId={userId}` | 公开 | 带用户收藏状态的文章 |
| PUT | `/articles` | Token | 新建文章 |
| POST | `/articles` | Token | 更新文章 |
| DELETE | `/articles/{id}` | Token | 删除文章 |
| GET | `/animals` | 公开 | 全部动物 |
| GET | `/animals/{id}` | 公开 | 按 id 查询动物 |
| GET | `/animals/userId={userId}` | 公开 | 用户的动物 |
| PUT | `/animals` | Token | 更新动物 |
| POST / PUT / DELETE | `/comments` | Token | 新增 / 修改 / 删除评论 |
| GET | `/clips/{userId}` | 公开 | 收藏的文章 |
| POST / DELETE | `/clips` | Token | 添加 / 取消收藏 |
| GET | `/follows/{userId}` | Token | 关注列表 |
| GET | `/follows/fans/{userId}` | Token | 粉丝列表 |
| POST | `/follows` | Token | 关注 / 取消关注 |
| POST | `/saves` | Token | 收藏 / 取消收藏动物 |
| POST | `/thumbs` | Token | 文章点赞 |
| POST | `/labels` | Token | 为文章添加标签 |
| PUT | `/imgs` | 公开 | 上传图片 |
| DELETE | `/imgs/url` | 公开 | 删除图片 |

## 已知问题

以下问题是在 develop 分支上搭建环境时发现的：

1. **缺少 Maven Wrapper**：提交了 `mvnw`，但没有 `.mvn/wrapper/`，所以 `./mvnw` 无法运行，请使用系统 Maven。
2. **缺少原始 SQL**：表结构是反推得到的（`sql/schema.sql`）。
3. **JDBC URL 拼写错误**：`charactterEncoding` 应为 `characterEncoding`；另外 connector 8.x 下 `com.mysql.jdbc.Driver` 已弃用（应使用 `com.mysql.cj.jdbc.Driver`）。
4. **新版数据库的 SSL 问题**：旧的 TLS 默认配置连接 MariaDB 11 / MySQL 8 会失败，请使用 `useSSL=false` 或正确配置 TLS。
5. **`application.yml` 中包含凭据**：仓库里提交了看起来是真实的账号密码，建议改用环境变量或不提交的 profile。
6. **路径变量路由**：`page={page}&count={count}` 会被当作单个路径段匹配，可能与 `/{id}` 冲突；实测 `GET /articles/page=1&count=10` 返回 HTTP 400（无法转换为 `Long`）。改用查询参数会更可靠。
7. **`mapper/extend/` 中的 Mapper XML 问题**：
   - `ArticleMapperExtend.xml` 有 8 处写成了 `#{id.jdbcType=BIGINT}`（逗号写成了点）。
   - `selectArticleWithLabelById` 引用了未定义的别名 `art`。
   - `selectCommentArticleByArticleId` 查询了不存在的列 `c.content`（应为 `comment_content`）。
   - `selectLastUpdate` 是空的 `SELECT`，`UserMapperExtend.selectUserWithFollowsAndFansById` 没有 SQL。
   - `commentResultMap` 把 `date` 列映射到了 `userId` 属性。
   - `UserMapperExtend.ArticleResultMap` 使用了 `thump_up` / `thumpUp`，应为 `thumb_up`。
8. **拦截器白名单**：`LoginInterceptor` 对 URI 做精确比较，`/webjars/**` 这类通配条目永远匹配不上。
9. **跨域配置**：`allowedOrigins("*")` 与 `allowCredentials(true)` 同时使用，会被浏览器以及新版 Spring 拒绝。
10. **图片上传**：图片写入 servlet context 的真实路径，在内嵌 Tomcat 下这是临时目录，重启后图片会丢失。

## 许可证

develop 分支根目录没有 `LICENSE` 文件。在作者补充许可证之前，代码默认保留所有权利。`MiaoWuWeb` 基于 MIT 许可的 vue-element-admin，具体见 `MiaoWuWeb/LICENSE`。
