CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `username` VARCHAR(32) DEFAULT NULL COMMENT '用户名',
  `real_name` VARCHAR(32) DEFAULT NULL COMMENT '真实名',
  `gender` TINYINT UNSIGNED DEFAULT NULL COMMENT '性别，1：男，2：女',
  `mobile` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(32) DEFAULT NULL COMMENT '邮箱',
  `password` VARCHAR(64) DEFAULT NULL COMMENT '密码',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `dept_id` BIGINT DEFAULT NULL COMMENT '主部门id',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态，0：冻结，1：正常',
  `remark` VARCHAR(32) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `role_name` VARCHAR(32) DEFAULT NULL COMMENT '角色名称',
  `role_code` VARCHAR(32) DEFAULT NULL COMMENT '角色编码',
  `remark` VARCHAR(32) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';

CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户id',
  `role_id` BIGINT DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色表';

CREATE TABLE `sys_manage_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父id',
  `menu_type` TINYINT DEFAULT NULL COMMENT '类型，1：一级菜单，2：二级菜单，3：三级菜单，4：按钮',
  `name` VARCHAR(32) DEFAULT NULL COMMENT '权限名称',
  `code` VARCHAR(32) DEFAULT NULL COMMENT '权限编码',
  `perm_path` VARCHAR(32) DEFAULT NULL COMMENT '前端菜单路径或者按钮权限编码',
  `icon` VARCHAR(32) DEFAULT NULL COMMENT '图标',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` VARCHAR(32) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='后台菜单权限表';

CREATE TABLE `sys_role_manage_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `role_id` BIGINT DEFAULT NULL COMMENT '角色id',
  `manage_permission_id` BIGINT DEFAULT NULL COMMENT '后台权限id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色后台权限表';

CREATE TABLE `sys_dept` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父部门',
  `name` VARCHAR(128) NOT NULL COMMENT '部门名称',
  `ancestors` VARCHAR(1000) DEFAULT NULL COMMENT '祖先部门id路径',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_dept_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限部门表';

CREATE TABLE `sys_scope_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `resource_code` VARCHAR(255) DEFAULT NULL COMMENT '资源编号',
  `scope_name` VARCHAR(255) DEFAULT NULL COMMENT '数据权限名称',
  `scope_field` VARCHAR(255) DEFAULT NULL COMMENT '数据权限返回字段',
  `scope_class` VARCHAR(500) DEFAULT NULL COMMENT 'Mapper方法全名',
  `scope_column` VARCHAR(255) DEFAULT NULL COMMENT '数据权限字段',
  `scope_type` INT DEFAULT NULL COMMENT '数据权限类型',
  `scope_value` TEXT COMMENT '数据权限值域',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '数据权限备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`),
  KEY `idx_scope_data_class` (`scope_class`),
  KEY `idx_scope_data_code` (`resource_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限规则表';

CREATE TABLE `sys_role_scope` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `role_id` BIGINT NOT NULL COMMENT '角色id',
  `scope_id` BIGINT NOT NULL COMMENT '数据权限规则id',
  `scope_category` INT NOT NULL DEFAULT 1 COMMENT '权限类别，1数据权限',
  `priority` INT NOT NULL DEFAULT 100 COMMENT '规则优先级，越小越优先',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_scope_category` (`role_id`, `scope_id`, `scope_category`),
  KEY `idx_role_scope_priority` (`role_id`, `scope_category`, `priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色数据权限关联表';

CREATE TABLE `sys_user_dept` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_id` BIGINT NOT NULL COMMENT '用户id',
  `dept_id` BIGINT NOT NULL COMMENT '部门id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_dept` (`user_id`, `dept_id`),
  KEY `idx_user_dept_dept` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户部门关联表';

CREATE TABLE `sys_dict` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父主键',
  `code` VARCHAR(255) DEFAULT NULL COMMENT '字典码',
  `dict_key` VARCHAR(255) DEFAULT NULL COMMENT '字典值',
  `dict_value` VARCHAR(255) DEFAULT NULL COMMENT '字典名称',
  `sort` INT DEFAULT NULL COMMENT '排序',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '字典备注',
  `is_sealed` INT DEFAULT NULL COMMENT '是否已封存',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_dict_code` (`code`),
  KEY `idx_sys_dict_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统字典表';

CREATE TABLE `sys_param` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `param_name` VARCHAR(255) DEFAULT NULL COMMENT '参数名',
  `param_key` VARCHAR(255) DEFAULT NULL COMMENT '参数键',
  `param_value` VARCHAR(255) DEFAULT NULL COMMENT '参数值',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_param_key` (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统参数表';

CREATE TABLE `sys_export_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` VARCHAR(12) DEFAULT NULL COMMENT '租户ID',
  `task_no` VARCHAR(64) NOT NULL COMMENT '任务编号',
  `export_biz_type` INT NOT NULL COMMENT '导出业务类型',
  `export_name` VARCHAR(100) DEFAULT NULL COMMENT '导出名称',
  `export_params` LONGTEXT COMMENT '序列化查询参数',
  `status` INT NOT NULL DEFAULT 0 COMMENT '状态[0待开始;1进行中;2已完成;3失败]',
  `total_count` BIGINT NOT NULL DEFAULT 0 COMMENT '总行数',
  `complete_count` BIGINT NOT NULL DEFAULT 0 COMMENT '已导出行数',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '生成的文件路径',
  `file_name` VARCHAR(200) DEFAULT NULL COMMENT '下载显示文件名',
  `error_msg` VARCHAR(1000) DEFAULT NULL COMMENT '失败原因',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_export_task_status` (`tenant_id`, `create_by`, `status`),
  KEY `idx_sys_export_task_no` (`tenant_id`, `task_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='异步导出任务表';

CREATE TABLE `sys_oss` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` VARCHAR(12) DEFAULT NULL COMMENT '租户ID',
  `category` INT DEFAULT NULL COMMENT '分类',
  `oss_code` VARCHAR(32) DEFAULT NULL COMMENT '资源编号',
  `enable_outside` TINYINT DEFAULT NULL COMMENT '是否启用内外地址',
  `endpoint` VARCHAR(255) DEFAULT NULL COMMENT '资源地址',
  `outside_endpoint` VARCHAR(255) DEFAULT NULL COMMENT '外部资源地址',
  `access_key` VARCHAR(255) DEFAULT NULL COMMENT '访问密钥',
  `secret_key` VARCHAR(255) DEFAULT NULL COMMENT '密钥',
  `bucket_name` VARCHAR(255) DEFAULT NULL COMMENT '空间名',
  `app_id` VARCHAR(255) DEFAULT NULL COMMENT '应用ID',
  `region` VARCHAR(255) DEFAULT NULL COMMENT '地域简称',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='对象存储配置表';

CREATE TABLE `sys_client` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `client_id` VARCHAR(48) NOT NULL COMMENT '客户端id',
  `client_secret` TEXT NOT NULL COMMENT '客户端密钥',
  `resource_ids` TEXT DEFAULT NULL COMMENT '资源集合',
  `scope` TEXT NOT NULL COMMENT '授权范围',
  `authorized_grant_types` TEXT NOT NULL COMMENT '授权类型',
  `web_server_redirect_uri` TEXT DEFAULT NULL COMMENT '回调地址',
  `authorities` TEXT DEFAULT NULL COMMENT '权限',
  `access_token_validity` INT NOT NULL COMMENT '令牌过期秒数',
  `refresh_token_validity` INT NOT NULL COMMENT '刷新令牌过期秒数',
  `additional_information` TEXT DEFAULT NULL COMMENT '附加说明',
  `autoapprove` TEXT DEFAULT NULL COMMENT '自动授权',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '是否已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='OAuth客户端表';
