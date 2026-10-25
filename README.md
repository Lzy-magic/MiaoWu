# MiaoWu - Stray Animal Rescue Platform

**English** | [简体中文](README.zh-CN.md)

MiaoWu is a stray-animal rescue and adoption platform. It keeps animal records in one place and lets users publish articles and updates about animals they found or adopted, follow other users, comment, like, bookmark articles and save animals.

The repository has two parts:

- **MiaoWuServer**: a RESTful backend built with Spring Boot and MyBatis.
- **MiaoWuWeb**: an admin-style web frontend built with Vue 2 and Element UI (based on vue-element-admin).

---

## Table of Contents

- [Features](#features)
- [Architecture and Tech Stack](#architecture-and-tech-stack)
- [Repository Layout](#repository-layout)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Configuration](#configuration)
- [Running the Backend](#running-the-backend)
- [Running the Frontend](#running-the-frontend)
- [API Overview](#api-overview)
- [Known Issues](#known-issues)
- [License](#license)

---

## Features

These are the modules that exist in `MiaoWuServer/src/main/java/com/apps/miaowu/web/controller`:

| Module | Controller | What it does |
|---|---|---|
| Users | `UserController` | Register, log in or out with a token stored in Redis, get user info, update, delete, list users |
| Articles | `ArticleController` | Create, update, delete and list articles; pagination; fuzzy title search through Elasticsearch; articles by author; articles from users you follow; articles with your bookmark state |
| Animals | `AnimalController` | List animals, get one by id, update one, list a user's saved or found animals |
| Comments | `CommentController` | Add, update and delete comments on articles |
| Clips (bookmarks) | `ClipController` | Bookmark or un-bookmark articles, list a user's bookmarked articles |
| Follows | `FollowController` | Follow or unfollow a user (toggle), list the users someone follows, list their fans |
| Saves | `SaveController` | Save or unsave an animal (toggle) |
| Thumbs | `ThumbController` | Like an article |
| Labels | `LabelController` | Attach labels (tags) to an article |
| Images | `ImgController` | Upload an image (stored under the servlet context path) and delete one |

Cross-cutting pieces:

- **Authentication**: `LoginInterceptor` checks the `Authorization` header token against Redis. Handlers marked with `@NoneAuth` are public.
- **Messaging**: `AnimalServiceImpl` sends updates through RabbitMQ and listens on the `update_queue` queue. There is also a sample `Producer`/`Consumer` in `common/utils/rabbitMQ`.
- **Search**: `ArticleServiceImpl` uses an Elasticsearch `RestHighLevelClient` for fuzzy article search.
- **API docs**: Swagger 2 (springfox 2.9.2) is configured in `config/Swagger2.java`, served at `/swagger-ui.html`.
- **CORS**: `WebConfig` allows all origins.

## Architecture and Tech Stack

```
Browser --> MiaoWuWeb (Vue 2, port 9527) --HTTP/JSON--> MiaoWuServer (Spring Boot, port 8088)
                                                           |-- MySQL / MariaDB (3306)  via MyBatis
                                                           |-- Redis (6379)            login tokens / cache
                                                           |-- RabbitMQ (5672)         animal update queue
                                                           '-- Elasticsearch (9200)    article search (optional)
```

### Backend (`MiaoWuServer/pom.xml`)

| Component | Version |
|---|---|
| Java | 1.8 |
| Spring Boot (parent) | 2.2.4.RELEASE |
| Spring Cloud dependencies (BOM) | Hoxton.SR1 |
| Spring Web, Security, AMQP, Data Elasticsearch, DevTools | managed by Boot 2.2.4 |
| MyBatis Spring Boot Starter | 2.1.1 |
| mysql-connector-java | 8.0.21 |
| spring-data-redis / Jedis | 2.2.5.RELEASE / 3.1.0 |
| elasticsearch-rest-client | 7.8.1 |
| springfox-swagger2 / swagger-ui | 2.9.2 |
| Lombok | 1.18.12 |
| fastjson | 1.2.58 |
| commons-lang3 / commons-io | 3.9 / 2.4 |
| Logging | log4j2 (`spring-boot-starter-log4j2`, config in `log4j2.yml`) |

### Frontend (`MiaoWuWeb/package.json`)

| Component | Version |
|---|---|
| Vue | 2.6.10 |
| Vue Router / Vuex | 3.0.2 / 3.1.0 |
| Element UI | 2.13.0 |
| axios | 0.18.1 |
| ECharts | 4.2.1 |
| @vue/cli-service | 3.5.3 |
| node-sass / sass-loader | ^4.9.0 / ^7.1.0 |

## Repository Layout

```
MiaoWu/
|-- README.md / README.zh-CN.md
|-- MiaoWuServer/                       Spring Boot backend
|   |-- pom.xml
|   '-- src/main/
|       |-- java/com/apps/miaowu/
|       |   |-- MiaowuApplication.java  entry point
|       |   |-- annotation/             @NoneAuth
|       |   |-- bean/                   entities + MyBatis Example classes, extend/, result/ (APIResult)
|       |   |-- common/                 exceptions, abstract service, utils (token, RabbitMQ)
|       |   |-- config/                 ElasticSearch, MyBatis, Spring, Swagger2, CORS
|       |   |-- constant/               NormalConstant
|       |   |-- dao/                    MyBatis mapper interfaces (+ extend/)
|       |   |-- service/ (+ impl/)      business logic
|       |   '-- web/controller, web/interceptor
|       '-- resources/
|           |-- application.yml
|           |-- log4j2.yml
|           |-- generatorConfig.xml     MyBatis Generator config
|           |-- mapper/ (+ extend/)     MyBatis XML mappers
|           '-- sql/schema.sql          reverse-engineered DB schema
'-- MiaoWuWeb/                          Vue 2 frontend (vue-element-admin based)
    |-- package.json, vue.config.js, .env.*
    |-- mock/                           mock server used by the dev server
    '-- src/
```

## Prerequisites

| Tool | Version | Notes |
|---|---|---|
| JDK | 8 | e.g. Temurin 8 or OpenJDK 8 |
| Maven | 3.6.x | The repo has **no** Maven wrapper (`.mvn/` is missing), so use a system `mvn` |
| MySQL / MariaDB | MySQL 5.7+ or MariaDB 10.x+ | Database `miaowu`, utf8mb4 |
| Redis | 5+ | Stores login tokens |
| RabbitMQ | 3.x | Needed at startup because of the `@RabbitListener` |
| Elasticsearch | 7.x, **optional** | Only used by the fuzzy article search; the app starts without it |
| Node.js | 16 (via nvm) | See the node-sass note below |
| Lombok IDE plugin | - | Needed for IDE compilation |

## Setup

```bash
git clone https://github.com/Lzy-magic/MiaoWu.git
cd MiaoWu
```

### 1. Database

```sql
CREATE DATABASE miaowu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER '<db_user>'@'localhost' IDENTIFIED BY '<db_password>';
CREATE USER '<db_user>'@'127.0.0.1' IDENTIFIED BY '<db_password>';
GRANT ALL ON miaowu.* TO '<db_user>'@'localhost';
GRANT ALL ON miaowu.* TO '<db_user>'@'127.0.0.1';
FLUSH PRIVILEGES;
```

Import the schema:

```bash
mysql -h127.0.0.1 -u<db_user> -p miaowu < MiaoWuServer/src/main/resources/sql/schema.sql
```

> **Note:** the project never shipped its original DDL. `schema.sql` was **reverse-engineered** from the MyBatis mapper XMLs and entity classes. Column names match the mappers, but types, lengths and indexes are best guesses. It creates 12 tables: `country`, `user`, `animal`, `article`, `label`, `article_label`, `comment_table`, `clip`, `follow`, `found_table`, `save`, `thumb_up`.

### 2. Redis and RabbitMQ

Start Redis on `127.0.0.1:6379`. Start RabbitMQ on `127.0.0.1:5672` and create the user from `application.yml`:

```bash
sudo rabbitmqctl add_user <mq_user> '<mq_password>'
sudo rabbitmqctl set_permissions -p / <mq_user> ".*" ".*" ".*"
```

### 3. Elasticsearch (optional)

`ElasticSearchConfig` connects to `http://127.0.0.1:9200` (hard-coded). Without it, only the fuzzy search endpoint fails.

## Configuration

`MiaoWuServer/src/main/resources/application.yml` (use your own values; never commit real secrets):

| Key | Default in repo | Meaning |
|---|---|---|
| `server.port` | `8088` | HTTP port |
| `spring.datasource.driver-class-name` | `com.mysql.jdbc.Driver` | JDBC driver |
| `spring.datasource.url` | `jdbc:mysql://127.0.0.1:3306/miaowu?charactterEncoding=utf-8` | JDBC URL (note the typo, see Known Issues) |
| `spring.datasource.username` / `password` | `<db_user>` / `<db_password>` | DB credentials |
| `spring.security.user.name` / `password` | `<user>` / `<password>` | Spring Security default user |
| `spring.redis.host` / `port` / `database` / `timeout` | `127.0.0.1` / `6379` / `0` / `1000s` | Redis connection |
| `spring.redis.jedis.pool.*` | max-idle 500, min-idle 50, max-wait -1, max-active -1 | Jedis pool |
| `spring.cache.redis.time-to-live` | `-1` | Cache TTL |
| `spring.rabbitmq.host` / `port` / `username` / `password` | `127.0.0.1` / `5672` / `<mq_user>` / `<mq_password>` | RabbitMQ |
| `spring.data.elasticsearch.cluster-name` / `cluster-nodes` | `my-cluster` / `localhost:9300` | ES transport settings (the REST client in code uses 9200) |
| `mybatis.mapper-locations` | `classpath:/mapper/**/*.xml` | Mapper XML location |
| `eureka.*` | client disabled | Leftover Eureka settings |
| `logging.config` | `classpath:log4j2.yml` | Logging config |

You can override any key on the command line instead of editing the file, e.g. `--spring.datasource.password=...`.

## Running the Backend

There is no `mvnw` wrapper in the repo, so use Maven directly:

```bash
cd MiaoWuServer
mvn -DskipTests package
mvn spring-boot:run
```

**Tip for newer MySQL / MariaDB:** if startup or the first query fails with `Communications link failure` / `SSLHandshakeException: No appropriate protocol`, turn off SSL with a URL override (no file edit needed):

```bash
mvn spring-boot:run \
  "-Dspring-boot.run.arguments=--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/miaowu?characterEncoding=utf-8&useSSL=false"
```

With MySQL 8 you may also need `&serverTimezone=Asia/Shanghai` (or your own zone).

Check that it is up:

```bash
curl http://localhost:8088/animals
curl http://localhost:8088/articles/1
```

Swagger UI: `http://localhost:8088/swagger-ui.html`.

## Running the Frontend

```bash
cd MiaoWuWeb
nvm install 16 && nvm use 16
npm install
npm run dev        # http://localhost:9527
```

Other scripts: `npm run build:prod`, `npm run build:stage`, `npm run lint`, `npm run test:unit`.

**node-sass note:** `node-sass@^4` has no binary for Node 16 and fails to build. Switching to dart-sass does not help either, because the styles use the `/deep/` selector. A working local setup is:

```bash
npm install --ignore-scripts
npm install --no-save --legacy-peer-deps node-sass@6.0.1 sass-loader@10.4.1
```

(The other option is to use Node 14 with the original versions.)

**Backend connection:** `.env.development` sets `VUE_APP_BASE_API = '/dev-api'`, and the dev server mounts the mock server from `mock/mock-server.js`. `vue.config.js` has **no proxy** to the backend on 8088, so the dev UI runs against mock data unless you add a `devServer.proxy` entry or change the base API.

## API Overview

All responses are wrapped in `APIResult` (`{ code, message, data, success }`). Endpoints marked "public" use `@NoneAuth`. All others need an `Authorization` token from `/users/login`.

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/users` | public | Register |
| POST | `/users/login` | public | Log in (`phone`, `password`) and get a token |
| POST | `/users/logout` | public | Log out (`token`) |
| GET | `/users/info` | public | User info by `token` |
| GET | `/users/{id}` | public | User by id |
| GET | `/users` | token | List users |
| PUT | `/users` | token | Update user |
| DELETE | `/users/{id}` | token | Delete user |
| GET | `/articles` | public | All articles |
| GET | `/articles/{id}` | public | Article with comments |
| GET | `/articles/page={page}&count={count}` | public | Paginated list |
| GET | `/articles/key={key}&value={value}` | public | Fuzzy search (Elasticsearch) |
| GET | `/articles/authorId={authorId}` | public | Articles by author |
| GET | `/articles/fansId={fansId}` | public | Articles from users that `fansId` follows |
| GET | `/articles/with=clips&userId={userId}` | public | Articles with bookmark state for a user |
| PUT | `/articles` | token | Create article |
| POST | `/articles` | token | Update article |
| DELETE | `/articles/{id}` | token | Delete article |
| GET | `/animals` | public | All animals |
| GET | `/animals/{id}` | public | Animal by id |
| GET | `/animals/userId={userId}` | public | A user's animals |
| PUT | `/animals` | token | Update animal |
| POST / PUT / DELETE | `/comments` | token | Add / update / delete comment |
| GET | `/clips/{userId}` | public | Bookmarked articles |
| POST / DELETE | `/clips` | token | Add / remove bookmark |
| GET | `/follows/{userId}` | token | Users that someone follows |
| GET | `/follows/fans/{userId}` | token | Fans |
| POST | `/follows` | token | Toggle follow |
| POST | `/saves` | token | Toggle saved animal |
| POST | `/thumbs` | token | Like an article |
| POST | `/labels` | token | Add labels to an article |
| PUT | `/imgs` | public | Upload image |
| DELETE | `/imgs/url` | public | Delete image |

## Known Issues

These were found while setting up the develop branch:

1. **No Maven wrapper:** `mvnw` is committed but `.mvn/wrapper/` is not, so `./mvnw` fails. Use a system Maven.
2. **No original SQL:** the schema had to be reverse-engineered (`sql/schema.sql`).
3. **JDBC URL typo:** `charactterEncoding` should be `characterEncoding`. Also, `com.mysql.jdbc.Driver` is deprecated with connector 8.x (use `com.mysql.cj.jdbc.Driver`).
4. **SSL with newer databases:** the old TLS defaults fail against MariaDB 11 / MySQL 8. Use `useSSL=false` or configure TLS.
5. **Credentials in `application.yml`:** real-looking credentials are committed. Move them to environment variables or a profile that is not committed.
6. **Elasticsearch config mismatch:** `application.yml` points at transport port 9300, but `ElasticSearchConfig` hard-codes REST `127.0.0.1:9200`.
7. **Path-variable routes:** `page={page}&count={count}` is matched as a single path segment and can collide with `/{id}`. `GET /articles/page=1&count=10` returned HTTP 400 (type conversion to `Long`). Query parameters would be more reliable.
8. **Mapper XML bugs** in `mapper/extend/`:
   - `ArticleMapperExtend.xml` uses `#{id.jdbcType=BIGINT}` (dot instead of comma) in 8 places.
   - `selectArticleWithLabelById` refers to alias `art`, which is never defined.
   - `selectCommentArticleByArticleId` selects `c.content`, a column that does not exist (it should be `comment_content`).
   - `selectLastUpdate` is an empty `SELECT`, and `UserMapperExtend.selectUserWithFollowsAndFansById` has no SQL.
   - `commentResultMap` maps column `date` to property `userId`.
   - `UserMapperExtend.ArticleResultMap` uses `thump_up` / `thumpUp` instead of `thumb_up`.
9. **Interceptor whitelist:** `LoginInterceptor` compares the URI exactly, so wildcard entries such as `/webjars/**` never match.
10. **CORS:** `allowedOrigins("*")` together with `allowCredentials(true)` is rejected by browsers and by newer Spring versions.
11. **Frontend build:** `node-sass@^4` does not build on Node 16+ (see above). The dev server uses mock data and does not proxy to the backend.
12. **Image upload** writes into the servlet context real path, which is a temporary directory with embedded Tomcat, so uploads are lost on restart.

## License

The develop branch has no `LICENSE` file at the repository root. Until the author adds one, the code is under default copyright (all rights reserved). `MiaoWuWeb` is derived from vue-element-admin, which is MIT-licensed; see `MiaoWuWeb/LICENSE`.
