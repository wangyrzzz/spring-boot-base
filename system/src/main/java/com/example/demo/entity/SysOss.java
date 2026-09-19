package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oss")
public class SysOss extends BaseEntity {
    private Integer category;
    private String ossCode;
    private Integer enableOutside;
    private String endpoint;
    private String outsideEndpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String appId;
    private String region;
    private String providerType;
    private String localRoot;
    private String publicBaseUrl;
    private String remark;
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
