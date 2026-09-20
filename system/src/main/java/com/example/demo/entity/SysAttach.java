package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_attach")
@Schema(description = "附件元数据")
public class SysAttach extends BaseEntity {
    @Schema(description = "对象存储键")
    private String objectKey;
    @Schema(description = "文件访问地址")
    private String url;
    @Schema(description = "文件名")
    private String fileName;
    @Schema(description = "文件扩展名")
    private String extension;
    @Schema(description = "文件内容类型")
    private String contentType;
    @Schema(description = "文件大小，单位：字节")
    private Long fileSize;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除，0：正常，1：已删除")
    private Integer deleted;
}
