package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_client")
@Schema(description = "系统客户端")
public class SysClient extends BaseEntity {
    @TableField("client_code")
    @Schema(description = "客户端编码")
    private String clientCode;
    @Schema(description = "客户端密钥")
    private String clientSecret;
    @Schema(description = "资源集合")
    private String resourceIds;
    @Schema(description = "授权范围")
    private String scope;
    @Schema(description = "授权类型")
    private String authorizedGrantTypes;
    @Schema(description = "回调地址")
    private String webServerRedirectUri;
    @Schema(description = "权限")
    private String authorities;
    @Schema(description = "访问令牌过期秒数")
    private Integer accessTokenValidity;
    @Schema(description = "刷新令牌过期秒数")
    private Integer refreshTokenValidity;
    @Schema(description = "附加说明")
    private String additionalInformation;
    @Schema(description = "自动授权配置")
    @TableField("auto_approve")
    private String autoApprove;
    @Schema(description = "状态")
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
