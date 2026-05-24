# CONFIG.md — 配置详解

所有配置类位于 `config/` 目录，默认值硬编码，无外部配置文件。在 `Main.main()` 中通过 setter 修改。

---

## GeneratorConfig — 代码生成配置

| 字段 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `basePackage` | String | `"cn.example"` | Java 根包名 |
| `outputPath` | String | `"src/main/java"` | **后端 Java 代码**输出目录（相对路径） |
| `frontendOutputPath` | String | `"generated-frontend"` | **前端独立部署代码**输出目录（相对路径） |
| `applicationName` | String | `"ExampleApplication"` | Spring Boot 启动类名 |
| `apiBase` | String | `"http://localhost:8080/api"` | **完整后端 API 地址**（写入前端 `api.js`） |
| `useDTO` | boolean | `true` | Controller 是否使用 DTO（false 则直接用 Entity） |
| `excludedFields` | Set\<String\> | `{"password"}` | 排除字段：Controller update 和 DTO 写入跳过这些字段。自动时间戳字段（`create_time`/`update_time`）也会被自动排除 |
| `selectedTables` | Set\<String\> | `{}`（空=全部表） | 只生成指定表的代码 |

### 方法说明

| 方法 | 返回值 | 说明 |
|---|---|---|
| `getApiBase()` | String | 完整 API 地址（`http://localhost:8080/api`），用于前端 JS |
| `getApiPath()` | String | 自动提取路径部分（`/api`），用于 Controller 注解和 Security 白名单 |
| `getEntityPackage()` | String | `{basePackage}.entity` |
| `getRepositoryPackage()` | String | `{basePackage}.repository` |
| `getServicePackage()` | String | `{basePackage}.service` |
| `getServiceImplPackage()` | String | `{basePackage}.service.impl` |
| `getControllerPackage()` | String | `{basePackage}.controller` |
| `getCommonPackage()` | String | `{basePackage}.common` |
| `getDtoPackage()` | String | `{basePackage}.dto` |

---

## DatabaseConfig — 数据库配置

| 字段 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `host` | String | `"localhost"` | MySQL 主机地址 |
| `port` | String | `"3306"` | MySQL 端口 |
| `name` | String | `"my_spring"` | 数据库名（dev 环境直接使用；prod/test 自动加后缀 `_prod`/`_test`） |
| `user` | String | `"root"` | 数据库用户名 |
| `password` | String | `"123456"` | 数据库密码 |

### 方法说明

| 方法 | 说明 |
|---|---|
| `getUrl()` | 返回 JDBC 连接 URL（`jdbc:mysql://{host}:{port}/{name}?...`） |

**注意**：MySQL JDBC 驱动 JAR 需放入 `JarResource/` 目录，运行时通过 `URLClassLoader` 动态加载，不依赖编译时 classpath。

---

## JwtConfig — JWT 配置

| 字段 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `autoGenerateKeyPair` | boolean | `true` | 每次运行是否自动生成 ECC 密钥对 |
| `privateKey` | String | `""` | 私钥 Base64（生成后自动填充） |
| `publicKey` | String | `""` | 公钥 Base64（生成后自动填充） |
| `expiration` | long | `86400000` | Token 过期时间（毫秒，默认 24 小时） |
| `tokenHeader` | String | `"Authorization"` | HTTP 请求头名称 |
| `tokenPrefix` | String | `"Bearer "` | Token 前缀 |
| `algorithm` | String | `"EC"` | 加密算法 |

JWT 配置写入各环境 YML 文件（非 `application.yml`），每个环境可独立配置。

---

## AuthConfig — 权限验证配置

| 字段 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `tableName` | String | `"user"` | 用户表名（用于检测是否启用权限验证） |
| `usernameField` | String | `"username"` | 用户登录名字段名 |
| `passwordField` | String | `"password"` | 用户密码字段名 |
| `roleField` | String | `"role"` | 角色字段名（存在于 `user` 表中才启用） |
| `roleAdmin` | String | `"ADMIN"` | 管理员角色标识 |
| `roleUser` | String | `"USER"` | 普通用户角色标识 |
| `roleGuest` | String | `"GUEST"` | 访客角色标识 |
| `enableAllTableAuth` | boolean | `true` | 是否为所有表启用权限注解 |
| `userOwnedTables` | Map\<String,String\> | `{"user_detail":"user_id"}` | 用户所有权表映射（表名 → 用户 ID 外键字段名） |

### 触发条件

只有**同时满足**以下条件，Controller 才会包含 `@PreAuthorize` 注解：

1. 数据库中存在名为 `user` 的表（`tableName`）
2. 该表包含角色列（`roleField`）
3. `enableAllTableAuth = true`

### 用户所有权机制

`userOwnedTables` 中的表，Controller 的 update/delete 会校验当前用户是否为数据所有者。非 ADMIN 且非所有者时抛出 403 异常。

### 角色字段类型说明

- 如果 `roleField` 在数据库里是 `VARCHAR/CHAR/TEXT`，可继续使用 `ADMIN/USER/GUEST`
- 如果 `roleField` 在数据库里是 `TINYINT/INT/BIGINT` 等数值类型，则 `roleAdmin`、`roleUser`、`roleGuest` 必须配置成可转换的实际数据库值，例如 `"1"`、`"2"`、`"3"`
- 生成器会按角色字段真实 Java 类型生成赋值转换逻辑，例如 `Byte.valueOf(...)`

### 方法说明

| 方法 | 说明 |
|---|---|
| `addUserOwnedTable(table, field)` | 添加用户所有权表映射 |
| `isUserOwnedTable(table)` | 判断指定表是否为用户所有权表 |
| `getUserOwnedField(table)` | 获取用户所有权表的用户 ID 字段名 |

---

## 修改配置示例

```java
public static void main(String[] args) {
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setName("my_project");          // 读取 my_project 数据库
    dbConfig.setPassword("mypassword");
    
    GeneratorConfig genConfig = new GeneratorConfig();
    genConfig.setBasePackage("com.myapp");    // 改用自定义包名
    genConfig.setFrontendOutputPath("frontend-admin"); // 生成独立部署前端目录
    genConfig.setApiBase("http://myhost:9090/api");  // 生产环境 API 地址
    genConfig.getSelectedTables().add("order");      // 只生成 order 表
    
    AuthConfig authConfig = new AuthConfig();
    authConfig.setTableName("sys_user");
    authConfig.setUsernameField("login_name");
    authConfig.setPasswordField("login_password");
    authConfig.setRoleField("user_role");
    authConfig.setEnableAllTableAuth(false);  // 关闭权限验证
    authConfig.addUserOwnedTable("post", "author_id");
    
    JwtConfig jwtConfig = new JwtConfig();
    jwtConfig.setExpiration(3600000L);        // Token 有效期 1 小时
    
    // ... 后续逻辑不变
}
```

## 前后端分离说明

- 生成器当前默认按**前后端分离**方式输出前端代码
- 前端页面、CSS、JS 会输出到 `frontendOutputPath` 指定目录，而不是 Spring Boot 的 `resources/static`
- 后端仅生成 API、JWT、安全配置等服务端代码，不再负责前端静态资源映射和页面放行
- 当前前端产物结构为：

```text
{frontendOutputPath}/
  index.html
  assets/
    css/style.css
    js/app.js
    js/api.js
    js/auth.js
    js/toast.js
    js/table.js
  pages/
    login.html
    register.html
    {entity}.html
```
