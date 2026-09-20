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
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态，0：冻结，1：正常',
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

CREATE TABLE `sys_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父id',
  `menu_type` TINYINT DEFAULT NULL COMMENT '类型，1：菜单，2：按钮',
  `name` VARCHAR(32) DEFAULT NULL COMMENT '菜单权限名称',
  `code` VARCHAR(32) DEFAULT NULL COMMENT '菜单权限编码',
  `ancestors` VARCHAR(1000) DEFAULT NULL COMMENT '祖先code路径',
  `path` VARCHAR(32) DEFAULT NULL COMMENT '前端菜单路径',
  `icon` VARCHAR(32) DEFAULT NULL COMMENT '图标',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` VARCHAR(32) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='后台菜单权限表';

CREATE TABLE `sys_role_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `role_id` BIGINT DEFAULT NULL COMMENT '角色id',
  `menu_id` BIGINT DEFAULT NULL COMMENT '菜单权限id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色菜单权限表';

CREATE TABLE `sys_dept` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父部门',
  `name` VARCHAR(128) NOT NULL COMMENT '部门名称',
  `code` VARCHAR(32) DEFAULT NULL COMMENT '部门编码',
  `ancestors` VARCHAR(1000) DEFAULT NULL COMMENT '祖先code路径',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构部门表';

CREATE TABLE `sys_user_dept` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_id` BIGINT NOT NULL COMMENT '用户id',
  `dept_id` BIGINT NOT NULL COMMENT '部门id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户部门表';


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
  `scope_value` TEXT COMMENT '数据权限值域,自定义规则值',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '数据权限备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`)
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
  `priority` INT NOT NULL DEFAULT 100 COMMENT '规则优先级，越小越优先',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色数据权限关联表';

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
  `status` INT DEFAULT NULL COMMENT '状态',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
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
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
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
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_export_task_status` (`create_by`, `status`),
  KEY `idx_sys_export_task_no` (`task_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='异步导出任务表';

CREATE TABLE `sys_oss` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
  `provider_type` VARCHAR(32) NOT NULL DEFAULT 'local' COMMENT '存储Provider类型',
  `local_root` VARCHAR(500) DEFAULT NULL COMMENT '本地存储根目录',
  `public_base_url` VARCHAR(1000) DEFAULT NULL COMMENT '公共访问地址前缀',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='对象存储配置表';

CREATE TABLE `sys_client` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `client_code` VARCHAR(48) NOT NULL COMMENT '客户端编码',
  `client_secret` TEXT NOT NULL COMMENT '客户端密钥',
  `resource_ids` TEXT DEFAULT NULL COMMENT '资源集合',
  `scope` TEXT NOT NULL COMMENT '授权范围',
  `authorized_grant_types` TEXT NOT NULL COMMENT '授权类型',
  `web_server_redirect_uri` TEXT DEFAULT NULL COMMENT '回调地址',
  `authorities` TEXT DEFAULT NULL COMMENT '权限',
  `access_token_validity` INT NOT NULL DEFAULT 900 COMMENT '令牌过期秒数',
  `refresh_token_validity` INT NOT NULL DEFAULT 604800 COMMENT '刷新令牌过期秒数',
  `additional_information` TEXT DEFAULT NULL COMMENT '附加说明',
  `auto_approve` TEXT DEFAULT NULL COMMENT '自动授权',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_client_client_code` (`client_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统客户端表';

CREATE TABLE `sys_dict_biz` LIKE `sys_dict`;
ALTER TABLE `sys_dict_biz` COMMENT = '业务字典表';

CREATE TABLE `sys_attach` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `object_key` VARCHAR(512) NOT NULL COMMENT '对象存储键',
  `url` VARCHAR(1000) NOT NULL COMMENT '文件访问地址',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
  `extension` VARCHAR(32) DEFAULT NULL COMMENT '文件扩展名',
  `content_type` VARCHAR(128) DEFAULT NULL COMMENT '文件内容类型',
  `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小，单位：字节',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`), KEY `idx_sys_attach_object_key` (`object_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='附件元数据表';

CREATE TABLE `sys_document` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `create_by` BIGINT DEFAULT NULL,
  `create_dept` BIGINT DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` BIGINT DEFAULT NULL,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `type`             tinyint               DEFAULT NULL COMMENT '文档类型，1：指南文档，2：接口文档',
  `code`             VARCHAR(128) NOT NULL COMMENT '文档编码',
  `sort`             INT          NOT NULL DEFAULT 0 COMMENT '排序',
  `language_code`    VARCHAR(16)           DEFAULT NULL COMMENT '语言编码，如 zh-CN、en-US',
  `title`            VARCHAR(128)          DEFAULT NULL COMMENT '文档标题',
  `subheading`       VARCHAR(128)          DEFAULT NULL COMMENT '副标题',
  `description`      VARCHAR(255)          DEFAULT NULL COMMENT '文档说明',
  `icon`             VARCHAR(128)          DEFAULT NULL COMMENT '图标',
  `link`             VARCHAR(255)          DEFAULT NULL COMMENT '跳转链接',
  `content`          longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文档内容,markdown',
  `document_version` VARCHAR(64)           DEFAULT NULL COMMENT '文档版本',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`), UNIQUE KEY `uk_sys_document_code` (`code`),
  KEY `idx_sys_document_type_sort` (`type`, `sort`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统文档表';

CREATE TABLE `sys_operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `biz_type` VARCHAR(128) DEFAULT NULL COMMENT '业务类型',
  `biz_id` VARCHAR(128) DEFAULT NULL COMMENT '业务 ID',
  `biz_name` VARCHAR(255) DEFAULT NULL COMMENT '业务名称',
  `operation_type` VARCHAR(64) DEFAULT NULL COMMENT '操作类型',
  `operator_name` VARCHAR(128) DEFAULT NULL COMMENT '操作人姓名',
  `department_name` VARCHAR(255) DEFAULT NULL COMMENT '部门名称',
  `role_name` VARCHAR(255) DEFAULT NULL COMMENT '角色名称',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT '客户端 IP',
  `device_type` VARCHAR(64) DEFAULT NULL COMMENT '设备类型',
  `request_path` VARCHAR(512) DEFAULT NULL COMMENT '请求路径',
  `http_method` VARCHAR(16) DEFAULT NULL COMMENT 'HTTP 请求方法',
  `method_class` VARCHAR(512) DEFAULT NULL COMMENT '方法所属类',
  `method_name` VARCHAR(255) DEFAULT NULL COMMENT '方法名',
  `request_params` LONGTEXT COMMENT '请求参数',
  `result_data` LONGTEXT COMMENT '返回结果',
  `error_message` LONGTEXT COMMENT '错误信息',
  `duration_ms` BIGINT DEFAULT NULL COMMENT '执行时间，单位：毫秒',
  `before_snapshot` LONGTEXT COMMENT '变更前快照',
  `after_snapshot` LONGTEXT COMMENT '变更后快照',
  `change_summary` LONGTEXT COMMENT '变更摘要',
  `flow_node` VARCHAR(128) DEFAULT NULL COMMENT '流程节点',
  `risk_flag` VARCHAR(64) DEFAULT NULL COMMENT '风险标记',
  `success` TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功：0否，1是',
  PRIMARY KEY (`id`),
  KEY `idx_sys_operation_log_biz` (`biz_type`, `biz_id`),
  KEY `idx_sys_operation_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务操作日志表';

CREATE TABLE `sys_biz_param` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `param_name` VARCHAR(255) DEFAULT NULL COMMENT '参数名',
  `param_key` VARCHAR(255) NOT NULL COMMENT '参数键',
  `param_value` LONGTEXT COMMENT '参数值',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态，0：禁用，1：启用',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_biz_param_key` (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务参数表';

CREATE TABLE `sys_log_api` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `service_name` VARCHAR(64) DEFAULT NULL COMMENT '服务名称',
  `server_host` VARCHAR(255) DEFAULT NULL COMMENT '服务器名',
  `server_ip` VARCHAR(64) DEFAULT NULL COMMENT '服务器IP地址',
  `env` VARCHAR(64) DEFAULT NULL COMMENT '运行环境',
  `type` VARCHAR(32) DEFAULT NULL COMMENT '日志类型',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '日志标题',
  `method` VARCHAR(16) DEFAULT NULL COMMENT 'HTTP请求方法',
  `request_uri` VARCHAR(1024) DEFAULT NULL COMMENT '请求URI',
  `user_agent` TEXT COMMENT '用户代理',
  `request_ip` VARCHAR(64) DEFAULT NULL COMMENT '客户端IP地址',
  `method_class` VARCHAR(512) DEFAULT NULL COMMENT '控制器类',
  `method_name` VARCHAR(255) DEFAULT NULL COMMENT '控制器方法',
  `request_params` LONGTEXT COMMENT '请求参数',
  `response_params` LONGTEXT COMMENT '响应参数',
  `duration_ms` BIGINT DEFAULT NULL COMMENT '执行时间，毫秒',
  `http_status` INT DEFAULT NULL COMMENT 'HTTP状态码',
  `success` TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功',
  `error_message` LONGTEXT COMMENT '错误信息',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_log_api_create_time` (`create_time`),
  KEY `idx_sys_log_api_uri` (`request_uri`(255)),
  KEY `idx_sys_log_api_status` (`http_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='接口访问日志表';

CREATE TABLE `sys_post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `category` INT DEFAULT NULL COMMENT '岗位类型',
  `post_code` VARCHAR(64) NOT NULL COMMENT '岗位编码',
  `post_name` VARCHAR(128) NOT NULL COMMENT '岗位名称',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '岗位排序',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '岗位描述',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态，0：禁用，1：启用',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_post_code` (`post_code`),
  KEY `idx_sys_post_name` (`post_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位表';

CREATE TABLE `sys_notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `title` VARCHAR(255) NOT NULL COMMENT '标题',
  `type` INT DEFAULT NULL COMMENT '公告类型',
  `release_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `content` LONGTEXT COMMENT '公告内容',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态，0：禁用，1：启用',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统公告表';

CREATE TABLE `sys_region` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `code` VARCHAR(32) NOT NULL COMMENT '区划编号',
  `parent_code` VARCHAR(32) DEFAULT NULL COMMENT '父区划编号',
  `ancestors` VARCHAR(1000) DEFAULT NULL COMMENT '祖区划编号',
  `name` VARCHAR(64) NOT NULL COMMENT '区划名称',
  `province_code` VARCHAR(32) DEFAULT NULL COMMENT '省级区划编号',
  `province_name` VARCHAR(64) DEFAULT NULL COMMENT '省级名称',
  `city_code` VARCHAR(32) DEFAULT NULL COMMENT '市级区划编号',
  `city_name` VARCHAR(64) DEFAULT NULL COMMENT '市级名称',
  `district_code` VARCHAR(32) DEFAULT NULL COMMENT '区级区划编号',
  `district_name` VARCHAR(64) DEFAULT NULL COMMENT '区级名称',
  `town_code` VARCHAR(32) DEFAULT NULL COMMENT '镇级区划编号',
  `town_name` VARCHAR(64) DEFAULT NULL COMMENT '镇级名称',
  `village_code` VARCHAR(32) DEFAULT NULL COMMENT '村级区划编号',
  `village_name` VARCHAR(64) DEFAULT NULL COMMENT '村级名称',
  `region_level` INT DEFAULT NULL COMMENT '区划层级',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态，0：禁用，1：启用',
  `deleted` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除，0：正常，1：已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_region_code` (`code`),
  KEY `idx_sys_region_parent_code` (`parent_code`),
  KEY `idx_sys_region_level_sort` (`region_level`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='行政区域表';

CREATE TABLE `mq_send_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message_id` VARCHAR(64) NOT NULL COMMENT '消息唯一标识',
  `provider` VARCHAR(32) NOT NULL COMMENT '消息队列提供者',
  `message_type` VARCHAR(16) NOT NULL COMMENT '消息类型：NORMAL/RELIABLE',
  `destination` VARCHAR(255) NOT NULL COMMENT '逻辑目标',
  `routing_key` VARCHAR(255) DEFAULT NULL COMMENT '路由键',
  `payload` LONGTEXT COMMENT '消息载荷',
  `headers` LONGTEXT COMMENT '消息请求头JSON',
  `status` INT NOT NULL COMMENT '状态：PENDING/SENDING/FAILED/SENT/MANUAL',
  `attempt_count` INT NOT NULL DEFAULT 0 COMMENT '发送尝试次数',
  `max_attempts` INT NOT NULL DEFAULT 10 COMMENT '最大自动发送次数',
  `next_retry_time` DATETIME DEFAULT NULL COMMENT '下次重试时间',
  `last_attempt_time` DATETIME DEFAULT NULL COMMENT '最近发送尝试时间',
  `sent_time` DATETIME DEFAULT NULL COMMENT '发送确认时间',
  `last_error` LONGTEXT COMMENT '最近错误信息',
  `handled_by` BIGINT DEFAULT NULL COMMENT '人工处理人',
  `handled_time` DATETIME DEFAULT NULL COMMENT '人工处理时间',
  `handle_remark` VARCHAR(1000) DEFAULT NULL COMMENT '人工处理备注',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mq_send_message_message_id` (`message_id`),
  KEY `idx_mq_send_message_retry` (`status`, `next_retry_time`),
  KEY `idx_mq_send_message_destination` (`destination`),
  KEY `idx_mq_send_message_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息发送记录';

CREATE TABLE `mq_consume_failure` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `provider` VARCHAR(32) NOT NULL COMMENT '消息队列提供者',
  `source_message_id` VARCHAR(64) DEFAULT NULL COMMENT '原消息唯一标识',
  `delivery_type` VARCHAR(16) DEFAULT NULL COMMENT '消息类型：NORMAL/RELIABLE',
  `destination` VARCHAR(255) NOT NULL COMMENT '逻辑目标',
  `routing_key` VARCHAR(255) DEFAULT NULL COMMENT '路由键',
  `consumer_name` VARCHAR(128) NOT NULL COMMENT '消费者名称',
  `payload` LONGTEXT COMMENT '消息载荷',
  `headers` LONGTEXT COMMENT '消息请求头JSON',
  `exception_type` VARCHAR(512) DEFAULT NULL COMMENT '异常类型',
  `error_message` VARCHAR(4000) DEFAULT NULL COMMENT '错误信息',
  `stack_trace` LONGTEXT COMMENT '异常堆栈',
  `status` INT NOT NULL COMMENT '状态：PENDING_MANUAL/RETRY_SUBMITTED/RESOLVED',
  `retry_count` INT NOT NULL DEFAULT 0 COMMENT '人工重试次数',
  `last_retry_message_id` VARCHAR(64) DEFAULT NULL COMMENT '最近重试消息ID',
  `handled_by` BIGINT DEFAULT NULL COMMENT '人工处理人',
  `handled_time` DATETIME DEFAULT NULL COMMENT '人工处理时间',
  `handle_remark` VARCHAR(1000) DEFAULT NULL COMMENT '人工处理备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_mq_consume_failure_status` (`status`),
  KEY `idx_mq_consume_failure_destination` (`destination`),
  KEY `idx_mq_consume_failure_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息消费失败记录';
