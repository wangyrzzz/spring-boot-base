package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oss")
@Schema(description = "对象存储配置")
public class SysOss extends BaseEntity {
    @Schema(description = "存储分类")
    private Integer category;
    @Schema(description = "资源编号")
    private String ossCode;
    @Schema(description = "是否启用内外地址")
    private Integer enableOutside;
    @Schema(description = "资源地址")
    private String endpoint;
    @Schema(description = "外部资源地址")
    private String outsideEndpoint;
    @Schema(description = "访问密钥")
    private String accessKey;
    @Schema(description = "密钥")
    private String secretKey;
    @Schema(description = "空间名")
    private String bucketName;
    @Schema(description = "应用 ID")
    private String appId;
    @Schema(description = "地域简称")
    private String region;
    @Schema(description = "存储 Provider 类型")
    private String providerType;
    @Schema(description = "本地存储根目录")
    private String localRoot;
    @Schema(description = "公共访问地址前缀")
    private String publicBaseUrl;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "状态")
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
