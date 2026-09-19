-- spring-boot-base system capability incremental migration; no historical data backfill.
ALTER TABLE sys_export_task DROP COLUMN IF EXISTS tenant_id;
ALTER TABLE sys_oss DROP COLUMN IF EXISTS tenant_id;
ALTER TABLE sys_attach DROP COLUMN IF EXISTS tenant_id;
ALTER TABLE sys_user ADD COLUMN dept_id BIGINT NULL COMMENT '部门id';
ALTER TABLE sys_client ADD UNIQUE KEY uk_sys_client_client_id (client_id);
ALTER TABLE sys_role ADD KEY idx_sys_role_role_code (role_code);
ALTER TABLE sys_manage_permission ADD KEY idx_sys_manage_permission_code (code);
ALTER TABLE sys_role_manage_permission ADD KEY idx_sys_role_manage_permission_role_permission (role_id, manage_permission_id);
ALTER TABLE sys_oss ADD COLUMN provider_type VARCHAR(32) NOT NULL DEFAULT 'local' COMMENT '存储Provider类型';
ALTER TABLE sys_oss ADD COLUMN local_root VARCHAR(500) NULL COMMENT '本地存储根目录';
ALTER TABLE sys_oss ADD COLUMN public_base_url VARCHAR(1000) NULL COMMENT '公共访问地址前缀';

INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '用户查询', 'system:user:read', 'system:user:read', 10, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:user:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '用户维护', 'system:user:write', 'system:user:write', 20, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:user:write');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '客户端管理', 'system:client:manage', 'system:client:manage', 30, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:client:manage');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '消息管理', 'system:mq:manage', 'system:mq:manage', 40, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:mq:manage');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '接口日志查询', 'system:api-log:read', 'system:api-log:read', 50, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:api-log:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '业务日志查询', 'system:biz-log:read', 'system:biz-log:read', 60, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:biz-log:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '对象存储查询', 'system:oss:read', 'system:oss:read', 70, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:oss:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '对象存储维护', 'system:oss:write', 'system:oss:write', 80, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:oss:write');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '字典查询', 'system:dict:read', 'system:dict:read', 90, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:dict:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '字典维护', 'system:dict:write', 'system:dict:write', 100, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:dict:write');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '参数查询', 'system:param:read', 'system:param:read', 110, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:param:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '参数维护', 'system:param:write', 'system:param:write', 120, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:param:write');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '业务参数查询', 'system:biz-param:read', 'system:biz-param:read', 130, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:biz-param:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '业务参数维护', 'system:biz-param:write', 'system:biz-param:write', 140, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:biz-param:write');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '岗位查询', 'system:post:read', 'system:post:read', 150, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:post:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '岗位维护', 'system:post:write', 'system:post:write', 160, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:post:write');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '通知查询', 'system:notice:read', 'system:notice:read', 170, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:notice:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '通知维护', 'system:notice:write', 'system:notice:write', 180, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:notice:write');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '行政区划查询', 'system:region:read', 'system:region:read', 190, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:region:read');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '行政区划维护', 'system:region:write', 'system:region:write', 200, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:region:write');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, '文档维护', 'system:document:write', 'system:document:write', 210, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:document:write');
INSERT INTO sys_manage_permission (menu_type, name, code, perm_path, sort, deleted)
SELECT 2, 'RBAC管理', 'system:rbac:manage', 'system:rbac:manage', 220, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_manage_permission WHERE code = 'system:rbac:manage');

CREATE TABLE IF NOT EXISTS sys_dict_biz LIKE sys_dict;
CREATE TABLE IF NOT EXISTS sys_attach (
  id BIGINT NOT NULL AUTO_INCREMENT, create_by BIGINT NULL, create_dept BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  object_key VARCHAR(512) NOT NULL, url VARCHAR(1000) NOT NULL,
  file_name VARCHAR(255) NOT NULL, extension VARCHAR(32) NULL, content_type VARCHAR(128) NULL,
  file_size BIGINT NOT NULL DEFAULT 0, is_deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id),
  KEY idx_sys_attach_object_key (object_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS sys_biz_param (
  id BIGINT NOT NULL AUTO_INCREMENT, create_by BIGINT NULL, create_dept BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  param_name VARCHAR(255) NULL, biz_module INT NOT NULL, param_key VARCHAR(255) NOT NULL,
  param_value LONGTEXT NULL, remark VARCHAR(255) NULL, status TINYINT NOT NULL DEFAULT 1,
  is_deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id),
  UNIQUE KEY uk_sys_biz_param_module_key (biz_module, param_key),
  KEY idx_sys_biz_param_key (param_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS sys_log_api (
  id BIGINT NOT NULL AUTO_INCREMENT, create_by BIGINT NULL, create_dept BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  service_id VARCHAR(64) NULL, server_host VARCHAR(255) NULL, server_ip VARCHAR(64) NULL,
  env VARCHAR(64) NULL, type VARCHAR(32) NULL, title VARCHAR(255) NULL,
  method VARCHAR(16) NULL, request_uri VARCHAR(1024) NULL, user_agent TEXT NULL,
  remote_ip VARCHAR(64) NULL, method_class VARCHAR(512) NULL, method_name VARCHAR(255) NULL,
  request_params LONGTEXT NULL, response_params LONGTEXT NULL, duration_ms BIGINT NULL,
  http_status INT NULL, success TINYINT NOT NULL DEFAULT 1, error_message LONGTEXT NULL,
  is_deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id),
  KEY idx_sys_log_api_create_time (create_time), KEY idx_sys_log_api_uri (request_uri(255)),
  KEY idx_sys_log_api_status (http_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS sys_post (
  id BIGINT NOT NULL AUTO_INCREMENT, create_by BIGINT NULL, create_dept BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  category INT NULL, post_code VARCHAR(64) NOT NULL, post_name VARCHAR(128) NOT NULL,
  sort INT NOT NULL DEFAULT 0, remark VARCHAR(255) NULL, status TINYINT NOT NULL DEFAULT 1,
  is_deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id),
  UNIQUE KEY uk_sys_post_code (post_code), KEY idx_sys_post_name (post_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS sys_notice (
  id BIGINT NOT NULL AUTO_INCREMENT, create_by BIGINT NULL, create_dept BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  title VARCHAR(255) NOT NULL, category INT NULL, release_time DATETIME NULL,
  content LONGTEXT NULL, status TINYINT NOT NULL DEFAULT 1, is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id), KEY idx_sys_notice_status_release (status, release_time),
  KEY idx_sys_notice_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS sys_region (
  id BIGINT NOT NULL AUTO_INCREMENT, create_by BIGINT NULL, create_dept BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  code VARCHAR(32) NOT NULL, parent_code VARCHAR(32) NULL, ancestors VARCHAR(1000) NULL,
  name VARCHAR(64) NOT NULL, province_code VARCHAR(32) NULL, province_name VARCHAR(64) NULL,
  city_code VARCHAR(32) NULL, city_name VARCHAR(64) NULL, district_code VARCHAR(32) NULL,
  district_name VARCHAR(64) NULL, town_code VARCHAR(32) NULL, town_name VARCHAR(64) NULL,
  village_code VARCHAR(32) NULL, village_name VARCHAR(64) NULL, region_level INT NULL,
  sort INT NOT NULL DEFAULT 0, remark VARCHAR(255) NULL, status TINYINT NOT NULL DEFAULT 1,
  is_deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id), UNIQUE KEY uk_sys_region_code (code),
  KEY idx_sys_region_parent_code (parent_code), KEY idx_sys_region_level_sort (region_level, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS sys_document (
  id BIGINT NOT NULL AUTO_INCREMENT, create_by BIGINT NULL, create_dept BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  type VARCHAR(64) NOT NULL, code VARCHAR(128) NOT NULL, sort INT NOT NULL DEFAULT 0,
  language_code VARCHAR(32) NULL, title VARCHAR(255) NULL, subheading VARCHAR(255) NULL,
  description VARCHAR(1000) NULL, icon VARCHAR(255) NULL, link VARCHAR(1000) NULL,
  content LONGTEXT, document_version VARCHAR(64) NULL, is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id), UNIQUE KEY uk_sys_document_code (code), KEY idx_sys_document_type_sort (type, sort, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS sys_operation_log (
  id BIGINT NOT NULL AUTO_INCREMENT, create_by BIGINT NULL, create_dept BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  biz_type VARCHAR(128) NULL, biz_id VARCHAR(128) NULL, biz_name VARCHAR(255) NULL,
  operation_type VARCHAR(64) NULL, operator_name VARCHAR(128) NULL, department_name VARCHAR(255) NULL,
  role_name VARCHAR(255) NULL, ip VARCHAR(64) NULL, device_type VARCHAR(64) NULL,
  request_path VARCHAR(512) NULL, http_method VARCHAR(16) NULL, method_class VARCHAR(512) NULL,
  method_name VARCHAR(255) NULL, request_params LONGTEXT, result_data LONGTEXT, error_message LONGTEXT,
  duration_ms BIGINT NULL, before_snapshot LONGTEXT, after_snapshot LONGTEXT, change_summary LONGTEXT,
  related_bill_id VARCHAR(128) NULL, related_bill_no VARCHAR(255) NULL, flow_node VARCHAR(128) NULL,
  risk_flag VARCHAR(64) NULL, success TINYINT NOT NULL DEFAULT 1, PRIMARY KEY (id),
  KEY idx_sys_operation_log_biz (biz_type, biz_id), KEY idx_sys_operation_log_bill (related_bill_id, related_bill_no),
  KEY idx_sys_operation_log_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS mq_send_message (
  id BIGINT NOT NULL AUTO_INCREMENT,
  message_id VARCHAR(64) NOT NULL,
  provider VARCHAR(32) NOT NULL,
  message_type VARCHAR(16) NOT NULL,
  destination VARCHAR(255) NOT NULL,
  routing_key VARCHAR(255) NULL,
  payload LONGTEXT NULL,
  headers LONGTEXT NULL,
  status VARCHAR(32) NOT NULL,
  attempt_count INT NOT NULL DEFAULT 0,
  max_attempts INT NOT NULL DEFAULT 10,
  next_retry_time DATETIME NULL,
  last_attempt_time DATETIME NULL,
  sent_time DATETIME NULL,
  last_error LONGTEXT NULL,
  handled_by BIGINT NULL,
  handled_time DATETIME NULL,
  handle_remark VARCHAR(1000) NULL,
  version INT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_mq_send_message_message_id (message_id),
  KEY idx_mq_send_message_retry (status, next_retry_time),
  KEY idx_mq_send_message_destination (destination),
  KEY idx_mq_send_message_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS mq_consume_failure (
  id BIGINT NOT NULL AUTO_INCREMENT,
  provider VARCHAR(32) NOT NULL,
  source_message_id VARCHAR(64) NULL,
  delivery_type VARCHAR(16) NULL,
  destination VARCHAR(255) NOT NULL,
  routing_key VARCHAR(255) NULL,
  consumer_name VARCHAR(128) NOT NULL,
  payload LONGTEXT NULL,
  headers LONGTEXT NULL,
  exception_type VARCHAR(512) NULL,
  error_message VARCHAR(4000) NULL,
  stack_trace LONGTEXT NULL,
  status VARCHAR(32) NOT NULL,
  retry_count INT NOT NULL DEFAULT 0,
  last_retry_message_id VARCHAR(64) NULL,
  handled_by BIGINT NULL,
  handled_time DATETIME NULL,
  handle_remark VARCHAR(1000) NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_mq_consume_failure_status (status),
  KEY idx_mq_consume_failure_destination (destination),
  KEY idx_mq_consume_failure_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
