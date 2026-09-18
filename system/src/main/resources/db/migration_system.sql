-- spring-boot-base system capability incremental migration; no historical data backfill.
ALTER TABLE sys_user ADD COLUMN dept_id BIGINT NULL COMMENT '部门id';
ALTER TABLE sys_client ADD UNIQUE KEY uk_sys_client_client_id (client_id);
ALTER TABLE sys_oss ADD COLUMN provider_type VARCHAR(32) NOT NULL DEFAULT 'local' COMMENT '存储Provider类型';
ALTER TABLE sys_oss ADD COLUMN local_root VARCHAR(500) NULL COMMENT '本地存储根目录';
ALTER TABLE sys_oss ADD COLUMN public_base_url VARCHAR(1000) NULL COMMENT '公共访问地址前缀';
CREATE TABLE IF NOT EXISTS sys_dict_biz LIKE sys_dict;
CREATE TABLE IF NOT EXISTS sys_attach (
  id BIGINT NOT NULL AUTO_INCREMENT, create_by BIGINT NULL, create_dept BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  tenant_id VARCHAR(64) NULL, object_key VARCHAR(512) NOT NULL, url VARCHAR(1000) NOT NULL,
  file_name VARCHAR(255) NOT NULL, extension VARCHAR(32) NULL, content_type VARCHAR(128) NULL,
  file_size BIGINT NOT NULL DEFAULT 0, is_deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id),
  KEY idx_sys_attach_object_key (object_key), KEY idx_sys_attach_tenant (tenant_id)
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
