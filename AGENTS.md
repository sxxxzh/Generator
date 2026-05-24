# AGENTS.md — Spring Boot CRUD 代码生成器

## 这是什么

一个**独立的 Java 代码生成工具**（本身不是 Spring Boot 应用），连接到 MySQL 数据库，读取表结构，自动生成完整的 Spring Boot 项目代码（Entity、Repository、Service、Controller、DTO、JWT 认证、Security 配置、Web 模板）。

## 入口点

`Main.java` — 唯一的 main 方法。执行流程：

1. 加载 4 个 Config（默认值硬编码，无外部配置文件）
2. 生成 ECC 密钥对（每次运行都重新生成）
3. 生成多个环境 YML 文件（每次运行都覆盖）
4. 从 `JarResource/` 动态加载 MySQL JDBC 驱动 JAR → 连接 MySQL → 读取所有表元数据
5. 检查 `user` 表是否存在 `role` 字段 → 决定是否启用权限验证
6. 生成公共类（ApiResponse）+ 启动类（`Application.java`，仅首次）
7. 生成 `pom.xml`（每次运行都覆盖）
8. 生成 JWT + Security + CORS 相关类 → 每表 CRUD 代码 → 静态 Web 模板

## 覆盖策略

| 文件 | 策略 |
|---|---|
| `pom.xml` | **每次覆盖** |
| `application*.yml` | **每次覆盖** |
| `Application.java` | 仅首次（已存在则跳过） |
| Entity / Repository / Service / Controller / DTO | **每次覆盖**（重新生成所有 CRUD 代码） |
| Web 模板 (HTML/CSS/JS) | **每次覆盖** |

## 必须先运行 Spring Boot 项目根目录

所有输出路径都是**相对的**（如 `src/main/java`、`src/main/resources/static/`），所以必须把工作目录设在 Spring Boot 项目根路径下编译运行。在 `Generator` 目录下运行会导致输出路径错误。

## 目录职责

| 目录 | 职责 |
|---|---|
| `config/` | 4 个配置 POJO，全部硬编码默认值（详见 `config/CONFIG.md`） |
| `model/` | `TableInfo`（表名+列列表）、`ColumnInfo`（列名、类型、是否可空、是否主键） |
| `metadata/` | `DatabaseMetadataReader` — 从 `JarResource/` 动态加载 MySQL 驱动，通过 JDBC 读取表/列信息 |
| `generator/` | 3 个生成器 — `CodeGenerator`（Entity/Repo/Service/Controller/DTO）、`JwtGenerator`（JWT+Security+CORS）、`WebTemplateGenerator`（HTML/CSS/JS） |
| `util/` | 工具类 — 蛇形转驼峰、MySQL 类型映射、文件写入、EC 密钥对生成 |
| `JarResource/` | 存放 MySQL JDBC 驱动 JAR 包，运行时动态加载 |

## 多环境 YML 生成

每次运行生成 4 个 YML 文件：

| 文件 | 内容 |
|---|---|
| `application.yml` | 公共配置：app 名称、`spring.profiles.active: dev`、web 资源 |
| `application-dev.yml` | 开发环境：原数据库名、`ddl-auto: update`、`show-sql: true`、端口 8080、JWT |
| `application-prod.yml` | 生产环境：`{dbName}_prod`、`ddl-auto: none`、`show-sql: false`、端口 80、JWT |
| `application-test.yml` | 测试环境：`{dbName}_test`、`ddl-auto: create-drop`、`show-sql: true`、端口 8081、JWT |

JWT 配置（密钥对、过期时间）已从 `application.yml` 迁移到各环境 YML 中。

## API 地址配置

`GeneratorConfig.apiBase` 默认为完整后端地址 `http://localhost:8080/api`。

- `getApiBase()` — 完整 URL，写入前端 `api.js` 的 `API_BASE` 常量
- `getApiPath()` — 自动提取路径部分（`/api`），用于 Controller `@RequestMapping` 和 Security 白名单

前端所有请求经由 `api.js` 的 `API_BASE` 统一拼接，不再依赖 `window.location.origin`。无论前端从哪里被访问（IntelliJ 预览、文件系统、独立服务器），请求都会正确发往后端。

## 全局 CORS

生成的 `SecurityConfig` 默认启用全局 CORS：
- 允许所有来源（`setAllowedOriginPatterns("*")`）
- 允许 `GET/POST/PUT/DELETE/OPTIONS` 方法
- 允许所有请求头
- `allowCredentials(true)`

## 自动时间戳字段

当表中有时间类型（DATETIME/TIMESTAMP/DATE）字段且列名以 `create_`/`created_` 或 `update_`/`updated_` 开头时：

- Entity 自动标注 `@CreationTimestamp` / `@UpdateTimestamp`（Hibernate 自动填充）
- Controller 的 update 方法自动跳过这些字段（不通过 `setXxx()` 写入）
- DTO 的 `toEntity()` 方法自动跳过这些字段
- DTO 的 `toDTO()` 方法保留这些字段（用于前端展示）

## 前端优化

生成的 Web 模板使用共享 JS 模块架构：

| 文件 | 职责 |
|---|---|
| `js/api.js` | 通用 API 请求（自动附加 JWT Token，401 跳转登录页） |
| `js/auth.js` | 登录/注册/登出/认证状态检测 |
| `js/toast.js` | Toast 通知系统（success/error/warning/info） |
| `js/table.js` | 共享 `TableManager` 类 — 所有表页面复用同一套 CRUD 逻辑（表格渲染、分页、搜索、编辑弹窗） |
| `css/style.css` | CSS 变量主题系统 + 响应式 + 动画 |

各表 HTML 仅通过 `data-api` / `data-columns` 属性传递配置，不再内联重复的 CRUD 逻辑。

## 权限验证的条件触发机制

只有当**同时满足**以下条件时，才会在生成的 Controller 中嵌入权限注解和验证逻辑：

1. 数据库中存在名为 `user` 的表（由 `AuthConfig.tableName` 控制）
2. 该表包含 `role` 列（由 `AuthConfig.roleField` 控制）
3. `AuthConfig.enableAllTableAuth = true`（默认开启）

不满足条件时，生成的 Controller 没有 `@PreAuthorize` 注解，也不会注入 `SecurityContextHolder`。

## 特殊表：`user`

如果存在 `user` 表，则：
- Repository 会额外生成按登录字段查询的方法（默认是 `findByUsername()`，可随 `AuthConfig.usernameField` 变化）
- Controller 的 update 方法会校验当前用户只能修改自己的信息（除非是 ADMIN）

## 用户所有权表

`AuthConfig.userOwnedTables`（默认包含 `"user_detail" → "user_id"`）定义哪些表的行属于特定用户。这些表的 Controller 在 update/delete 时会校验 `user_id` 是否匹配当前登录用户。

## 排除字段

`GeneratorConfig.excludedFields`（默认排除 `password`）——这些字段在 Controller update 时不会被 `setXxx()` 写入。同时，自动时间戳字段（`create_time`/`update_time`）也会被自动排除。

## 修改配置

所有默认值硬编码在 Config 类的字段初始化中。修改方式：
- **推荐**：在 `Main.main()` 开头对 Config 对象调用 setter（源码内配置）
- **无外部配置文件**：没有 application.properties、YAML、JSON 配置入口

## MySQL 强依赖

- JDBC 驱动硬编码：`com.mysql.cj.jdbc.Driver`
- 驱动 JAR 从 `JarResource/` 目录运行时动态加载，不依赖编译时 classpath
- 只支持 MySQL，不支持 PostgreSQL、SQLite 等
- `TypeUtils.mapJavaType()` 接受的 SQL 类型名按 MySQL 命名（如 `TINYINT`、`LONGTEXT`、`BLOB` 等）

## JWT

- 算法：**EC**（ES256），密钥长度 256-bit
- 每次运行自动生成密钥对 → Base64 编码 → 写入各环境 YML 的 `jwt.private-key` 和 `jwt.public-key`
- 默认过期时间：86400000ms（24 小时）
- 生成的 JWT 工具类依赖 `io.jsonwebtoken`（jjwt 库）

## Web 模板

生成静态 HTML 到 `src/main/resources/static/`：
- `index.html` — 首页，动态菜单（根据实际表名生成链接）
- `login.html` / `register.html` — 登录/注册页（使用 Toast 通知）
- 每个表生成一个 `<entityName>.html` — 带搜索、分页、CRUD 弹窗的管理页面
- `css/style.css` — CSS 变量主题 + 响应式 + Toast 动画
- `js/api.js` / `js/auth.js` / `js/toast.js` / `js/table.js` — 共享 JS 模块

## 代码生成约定

- 包名：`cn.example.entity`、`cn.example.service.impl` 等（由 `GeneratorConfig.basePackage` 控制）
- 实体类使用 `@Data @NoArgsConstructor @AllArgsConstructor`（Lombok）
- 主键均为 `@GeneratedValue(strategy = GenerationType.IDENTITY)`（自增）
- 时间戳字段自动标注 `@CreationTimestamp` / `@UpdateTimestamp`
- Repository 继承 `JpaRepository<Entity, Long>` + `JpaSpecificationExecutor`（支持动态查询）
- 实体类实现 `Serializable`，带 `serialVersionUID = 1L`
- 每一列标注 `@Column(name = "...")`，非主键非空列加 `nullable = false`
