package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Builder
@Schema(description = "当前认证用户")
public class AuthenticatedUser implements Serializable {
    @Schema(description = "客户端编码")
    private String clientCode;
    @Schema(description = "用户 ID")
    private Long userId;
    @Schema(description = "登录账号")
    private String account;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "昵称")
    private String nickName;
    @Schema(description = "部门 ID")
    private Long deptId;
    @Schema(description = "完整部门路径")
    private String fullDeptId;
    @Schema(description = "岗位 ID")
    private String postId;
    @Schema(description = "角色名称")
    private String roleName;
    @Builder.Default
    @Schema(description = "角色编码列表")
    private List<String> roleCodes = Collections.emptyList();
    @Builder.Default
    @Schema(description = "角色 ID 列表")
    private List<Long> roleIds = Collections.emptyList();
    @Builder.Default
    @Schema(description = "认证详情")
    private Map<String, Object> detail = Collections.emptyMap();
}
