# Spring Boot 3.5 多模块系统基础工程

根工程是 Maven 聚合工程，唯一可运行模块为 `bootstrap`。Java 包名继续保持 `com.example.demo`，使用 Java 21、Spring Boot 3.5.16、MyBatis-Plus、Druid、Redis、RabbitMQ、Elasticsearch 和 Knife4j。`system` 与 `search` 只依赖 `common`，由 `bootstrap` 负责最终组装和启动。

目录结构：

```text
spring-boot-base/
├── pom.xml
├── common/       # 公共注解、响应、异常、枚举、基础实体
├── system/
├── search/       # Elasticsearch 文档、Repository 和搜索接口
├── bootstrap/    # 启动类、运行配置和集成测试
│   ├── pom.xml
│   └── src/
├── sy.sql
└── README.md
```

## 认证

启动前必须设置至少 32 字节的 `JWT_KEY` 环境变量。默认 token 有效期为 access token 15 分钟、refresh token 7 天。基础设施默认按配置关闭，未启用 Redis 时认证使用无状态 JWT；启用 refresh token 后，Redis 可额外提供 refresh token 的一次性消费和撤销能力。

接口：

- `POST /auth/login`：用户名、密码和必填的 `clientCode`（表单使用 `client_code`），返回 `accessToken`、`refreshToken`、`tokenType` 和有效期。
- `POST /auth/refresh`：提交 `refreshToken`。只有开启 `sys.auth.refresh-token-enabled` 时才返回和接受 refresh token；启用 Redis 时刷新会原子轮换并撤销旧 refresh token，否则仅校验 JWT 签名、类型和有效期。
- `POST /auth/logout`：携带当前 access token，可同时提交 refresh token；Redis 开启时撤销 refresh token。access token 始终按无状态 JWT 校验。

客户端有效期从 `sys_client.access_token_validity` 和 `sys_client.refresh_token_validity` 动态读取。客户端被禁用或逻辑删除后不能登录或刷新；客户端密钥必须使用 BCrypt 哈希。

## 系统基础能力

- OSS：`/retail-resource/oss/**`，默认本地 Provider；上传使用 multipart 字段 `file`，附件元数据写入 `sys_attach`。
- 字典：`/retail-system/dict/**`、`/retail-system/dict-biz/**`，系统字典和业务字典使用独立缓存命名空间。
- 参数：`/retail-system/param/**`，支持参数值 Redis 缓存。
- 文档：`/retail-resource/document/**`，列表不返回 Markdown 正文，详情返回正文，普通读取开放、写操作要求管理员角色。
- 业务日志：`@BizOperationLog` 和 `/retail-system/bizLog/**`，统一写入 `sys_operation_log`，异常日志使用独立事务。

业务请求使用 `Authorization: Bearer <accessToken>`。认证用户在 Servlet 请求期间通过 `AuthUserContext` 获取；新增和修改密码只保存 BCrypt 哈希，认证只接受 BCrypt 密码。

## RBAC 注解鉴权

通用鉴权能力来自 `@PreAuth`，表达式在 Spring AOP 中统一执行，支持：

- `permitAll()`、`denyAll()`、`hasAuth()`；
- `hasRole(...)`、`hasAnyRole(...)`、`hasAllRole(...)`；
- `permissionAll()` 和 `hasPermission('system:user:read')`。

角色从 `sys_user_role -> sys_role` 加载，角色编码来自 `sys_role.role_code`，权限从 `sys_menu -> sys_role_menu` 判断。系统管理接口使用 `RbacPermissionCodes` 中的固定权限表达式，业务模块可以直接复用同一个注解和表达式根：

```java
@PreAuth("permissionAll() || hasPermission('order:print:submit')")
public Result<?> submit(...) { ... }
```

`sys.rbac.administrator-bypass=true` 时，角色编码为 `administrator` 的用户拥有全部权限；生产环境可以关闭该旁路，改为完全依赖角色权限关联。`@PreAuth("permitAll()")` 只表示 AOP 鉴权放行，不会自动绕过 `AuthenticationFilter`；真正匿名接口仍需配置到认证过滤器的公开路径中。

权限编码建议在 `sys_menu.code` 中保持唯一，并为 `code`、`role_id + menu_id` 建立索引。角色和菜单关联可通过 `/retail-system/rbac/role/grant` 管理。

## 数据权限

`system` 包内的 `DataPermissionInnerInterceptor` 在分页插件前改写 SELECT。它按 Mapper 方法全名匹配 `sys_scope_data.scope_class`，再按用户角色从 `sys_role_scope` 以 `priority`、规则 ID 选一条规则；没有数据库规则时才使用 `@DataAuth`。支持 ALL、本人、本人部门、部门及子部门和 CUSTOM。CUSTOM 只接受预定义用户字段占位符，并使用 `scope` 别名包装查询。

当前数据库结构以 [`sy.sql`](sy.sql) 为准。

## 消息队列

业务代码通过 `MessageQueueTemplate` 发送消息，消息传输类型和可靠性独立选择：`RABBITMQ` 或 `SPRING_EVENT`，以及 `NORMAL` 或 `RELIABLE`。Spring Event 仅支持普通消息，可靠消息必须使用 RabbitMQ。请求日志已改为 Spring Event 异步处理；其他消息默认使用 RabbitMQ。RabbitMQ 普通消息只在发布失败后写入 `mq_send_message`，可靠消息在当前事务中先落库，事务提交后发送。发送失败默认每 3 分钟重试一次，最多 10 次，之后进入人工处理状态。

消费者通过 `MessageConsumerRegistry` 注册，不直接依赖 RabbitMQ 的 `Channel`。处理成功后手动确认；处理异常会写入 `mq_consume_failure` 并丢弃消息，不重新入队。人工处理接口位于 `/retail-system/mq-send-message/**` 和 `/retail-system/mq-consume-failure/**`，支持分页、详情、重试和标记已处理。

消息配置位于 `application.yml` 的 `sys.mq` 节点，可通过 `SYS_MQ_RETRY_FIXED_DELAY_MS`、`SYS_MQ_RETRY_MAX_ATTEMPTS`、`SYS_MQ_RETRY_BATCH_SIZE` 和 `SYS_MQ_RETRY_STALE_TIMEOUT_MS` 覆盖重试参数。消息层保证至少一次投递，业务消费者需要自行保证幂等。Spring Event 是当前进程内事件，不提供跨实例投递和持久化能力。

## 可选基础设施开关

`sys.infra.redis.enabled`、`sys.infra.rabbitmq.enabled` 和 `sys.infra.elasticsearch.enabled` 分别控制 Redis、RabbitMQ 和 Elasticsearch。三个开关默认均为 `false`，也可使用环境变量 `SYS_INFRA_REDIS_ENABLED`、`SYS_INFRA_RABBITMQ_ENABLED` 和 `SYS_INFRA_ELASTICSEARCH_ENABLED` 覆盖。开关关闭时对应 Spring Boot 自动配置和相关组件不会加载；开关开启但缺少必要连接配置时应用会在启动阶段失败，而不是静默降级。

示例：

```yaml
sys:
  infra:
    redis:
      enabled: true
    rabbitmq:
      enabled: true
    elasticsearch:
      enabled: false
  auth:
    refresh-token-enabled: true
```

## 搜索模块

Elasticsearch 文档模型、Repository 和 `/test/**` 测试接口位于 `search` 模块。只有当 `sys.infra.elasticsearch.enabled=true` 且配置了 `spring.elasticsearch.uris` 时才加载 Repository 和接口；`system` 与 `search` 之间没有业务依赖。

## 启动与文档

```powershell
$env:JWT_KEY = "replace-with-a-random-secret-at-least-32-bytes"
./mvnw -pl bootstrap spring-boot:run
```

启动后访问 `/doc.html` 或 `/swagger-ui.html`。数据库以及 Redis、RabbitMQ、Elasticsearch 的连接配置分别位于 `application-*.yml`；是否创建对应客户端由 `infra` 开关决定。

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
