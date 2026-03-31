# Nacos 配置说明

## Data ID 列表

以下文件对应 Nacos `配置管理 -> 配置列表` 中需要创建的 Data ID：

- `common.yaml`
- `datasource.yaml`
- `mybatis.yaml`
- `ai.yaml`
- `aiPush-gateway-service.yaml`
- `aiPush-archive-service.yaml`
- `aiPush-warnning-service.yaml`
- `aiPush-push-service.yaml`

默认建议：

- `Group`: `DEFAULT_GROUP`
- `Namespace`: `public`
- `配置格式`: `YAML`

## 页面改动

1. 在 Nacos 中新增上面 8 个配置。
2. 将 `nacos/` 目录下同名文件内容复制到对应 Data ID。
3. 将 `datasource.yaml` 中数据库账号密码改成实际值。
4. 将 `ai.yaml` 中 `spring.ai.dashscope.api-key` 改成实际值。
5. 将 `aiPush-push-service.yaml` 中 3 个 `webhook` 地址改成实际值。
6. 如果你的 Nacos 不是 `127.0.0.1:8848`、`public`、`DEFAULT_GROUP`，同步修改各服务的 `bootstrap.yml`。

## 当前项目的加载关系

- `aiPush-gateway-service`
  - 自动加载：`aiPush-gateway-service.yaml`
  - 共享加载：`common.yaml`
- `aiPush-archive-service`
  - 自动加载：`aiPush-archive-service.yaml`
  - 扩展加载：`datasource.yaml`、`mybatis.yaml`
  - 共享加载：`common.yaml`
- `aiPush-warnning-service`
  - 自动加载：`aiPush-warnning-service.yaml`
  - 扩展加载：`datasource.yaml`、`mybatis.yaml`、`ai.yaml`
  - 共享加载：`common.yaml`
- `aiPush-push-service`
  - 自动加载：`aiPush-push-service.yaml`
  - 扩展加载：`datasource.yaml`、`mybatis.yaml`、`ai.yaml`
  - 共享加载：`common.yaml`

## 风险说明

- 项目当前 `datasource` 和 `pagehelper` 配的是 MySQL。
- 但 [schema.sql](/D:/project/aiPushPlatformProject/aiPushPlatformProject/sql/schema.sql) 使用了 PostgreSQL/pgvector 语法。
- 如果你后续真的要跑 `schema.sql`，数据库方案需要统一，不能直接沿用当前 MySQL 配置。
