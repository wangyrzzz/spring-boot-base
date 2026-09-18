# Spring Boot 3.5 多模块系统基础工程

根工程是 Maven 聚合工程，当前可运行模块为 `system`。Java 包名继续保持 `com.example.demo`，使用 Java 21、Spring Boot 3.5.16、MyBatis-Plus、Druid、Redis、RabbitMQ、Elasticsearch 和 Knife4j。当前模块承载系统基础能力，后续业务模块可直接在根 `pom.xml` 中追加。

目录结构：

```text
spring-boot-base/
├── pom.xml
├── system/
│   ├── pom.xml
│   └── src/
├── sy.sql
└── README.md
```

## 认证

启动前必须设置至少 32 字节的 `JWT_KEY` 环境变量。默认 token 有效期为 access token 15 分钟、refresh token 7 天，Redis 保存会话状态并支持多端登录。

接口：

- `POST /auth/login`：用户名、密码和 `clientId`，返回 `accessToken`、`refreshToken`、`tokenType` 和有效期。
- `POST /auth/refresh`：提交 `refreshToken`。刷新会原子轮换并撤销旧 refresh token。
- `POST /auth/logout`：携带当前 access token，可同时提交 refresh token，撤销当前会话。
- `POST /retail-auth/oauth/token`：兼容 `password` 和 `client_credentials`，同时接受 JSON 的 `clientId` 与表单的 `client_id`。
- `GET /retail-auth/oauth/logout`：OAuth 兼容注销。

客户端有效期从 `sys_client.access_token_validity` 和 `sys_client.refresh_token_validity` 动态读取。客户端被禁用或逻辑删除后不能登录或刷新；旧的明文客户端密钥首次校验成功后会升级为 BCrypt。

## 系统基础能力

- OSS：`/retail-resource/oss/**`，默认本地 Provider；上传使用 multipart 字段 `file`，附件元数据写入 `sys_attach`。
- 字典：`/retail-system/dict/**`、`/retail-system/dict-biz/**`，系统字典和业务字典使用独立缓存命名空间。
- 参数：`/retail-system/param/**`，支持参数值 Redis 缓存。
- 文档：`/retail-resource/document/**`，列表不返回 Markdown 正文，详情返回正文，普通读取开放、写操作要求管理员角色。
- 业务日志：`@BizOperationLog` 和 `/retail-system/bizLog/**`，统一写入 `sys_operation_log`，异常日志使用独立事务。

业务请求使用 `Authorization: Bearer <accessToken>`。认证用户在 Servlet 请求期间通过 `AuthUserContext` 获取，旧的按用户 ID 建立 Session 的登录接口已移除。明文密码首次登录成功后会迁移为 BCrypt；新增和修改密码也只保存 BCrypt 哈希。

## 数据权限

`common` 包内的 `DataPermissionInnerInterceptor` 在分页插件前改写 SELECT。它按 Mapper 方法全名匹配 `sys_scope_data.scope_class`，再按用户角色从 `sys_role_scope` 以 `priority`、规则 ID 选一条规则；没有数据库规则时才使用 `@DataAuth`。支持 ALL、本人、本人部门、部门及子部门和 CUSTOM。CUSTOM 只接受预定义用户字段占位符，并使用 `scope` 别名包装查询。

建表和迁移示例见 [`sy.sql`](sy.sql)。系统能力增量迁移脚本位于 [`migration_system.sql`](system/src/main/resources/db/migration_system.sql)，只变更结构，不回填历史数据。

## 启动与文档

```powershell
$env:JWT_KEY = "replace-with-a-random-secret-at-least-32-bytes"
./mvnw -pl system spring-boot:run
```

启动后访问 `/doc.html` 或 `/swagger-ui.html`。数据库、Redis、RabbitMQ 和 Elasticsearch 连接配置分别位于 `application-*.yml`。

## Actuator 与链路追踪

Actuator 默认监听独立管理端口 `8081`，可通过 `MANAGEMENT_PORT` 和 `MANAGEMENT_ADDRESS` 覆盖。生产环境建议只把该端口暴露给健康探针和监控网络。健康探针和指标端点如下：

- `/actuator/health`
- `/actuator/health/liveness`
- `/actuator/health/readiness`
- `/actuator/info`
- `/actuator/metrics`
- `/actuator/prometheus`

项目使用 Spring Boot Actuator 的 Micrometer Tracing 和 OpenTelemetry bridge。HTTP 请求遵循 W3C `traceparent`，不再读取或返回自定义的 `X-Trace-Id`。日志由原生 tracing 自动填充 `traceId` 和 `spanId`，请求结束后由框架清理上下文。

通过项目的 `asyncTaskExecutor` 提交的异步任务会继承当前 trace context；RabbitMQ 的 template 和 listener observation 会自动在消息头中传播 `traceparent`。新增 HTTP 客户端时，请使用 Spring Boot 提供的 `RestTemplateBuilder`、`RestClient.Builder` 或 `WebClient.Builder`，以获得自动传播能力。

默认采样率为 10%，可通过 `TRACE_SAMPLING_PROBABILITY` 调整。OTLP 导出默认关闭；需要导出时同时设置：

```powershell
$env:TRACE_OTLP_EXPORT_ENABLED = "true"
$env:MANAGEMENT_OTLP_TRACING_ENDPOINT = "http://otel-collector:4318/v1/traces"
```
