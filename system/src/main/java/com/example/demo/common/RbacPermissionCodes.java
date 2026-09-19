package com.example.demo.common;

/**
 * Permission expressions used by the built-in system controllers.
 *
 * <p>{@code permissionAll()} gives the built-in administrator role a safe
 * bootstrap path; other roles must be assigned the corresponding
 * {@code sys_manage_permission.code} through {@code sys_role_manage_permission}.</p>
 */
public final class RbacPermissionCodes {
    public static final String USER_READ = "permissionAll() || hasPermission('system:user:read')";
    public static final String USER_WRITE = "permissionAll() || hasPermission('system:user:write')";
    public static final String CLIENT_MANAGE = "permissionAll() || hasPermission('system:client:manage')";
    public static final String MQ_MANAGE = "permissionAll() || hasPermission('system:mq:manage')";
    public static final String API_LOG_READ = "permissionAll() || hasPermission('system:api-log:read')";
    public static final String BIZ_LOG_READ = "permissionAll() || hasPermission('system:biz-log:read')";
    public static final String OSS_READ = "permissionAll() || hasPermission('system:oss:read')";
    public static final String OSS_WRITE = "permissionAll() || hasPermission('system:oss:write')";
    public static final String DICT_READ = "permissionAll() || hasPermission('system:dict:read')";
    public static final String DICT_WRITE = "permissionAll() || hasPermission('system:dict:write')";
    public static final String PARAM_READ = "permissionAll() || hasPermission('system:param:read')";
    public static final String PARAM_WRITE = "permissionAll() || hasPermission('system:param:write')";
    public static final String BIZ_PARAM_READ = "permissionAll() || hasPermission('system:biz-param:read')";
    public static final String BIZ_PARAM_WRITE = "permissionAll() || hasPermission('system:biz-param:write')";
    public static final String POST_READ = "permissionAll() || hasPermission('system:post:read')";
    public static final String POST_WRITE = "permissionAll() || hasPermission('system:post:write')";
    public static final String NOTICE_READ = "permissionAll() || hasPermission('system:notice:read')";
    public static final String NOTICE_WRITE = "permissionAll() || hasPermission('system:notice:write')";
    public static final String REGION_READ = "permissionAll() || hasPermission('system:region:read')";
    public static final String REGION_WRITE = "permissionAll() || hasPermission('system:region:write')";
    public static final String DOCUMENT_WRITE = "permissionAll() || hasPermission('system:document:write')";
    public static final String RBAC_MANAGE = "permissionAll() || hasPermission('system:rbac:manage')";

    private RbacPermissionCodes() {
    }
}
