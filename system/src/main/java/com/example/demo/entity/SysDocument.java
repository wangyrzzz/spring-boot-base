package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_document")
@Schema(description = "系统文档")
public class SysDocument extends BaseEntity {
    @Schema(description = "文档类型，1：指南文档，2：接口文档")
    private String type;
    @Schema(description = "文档编码")
    private String code;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "语言编码，如 zh-CN、en-US")
    private String languageCode;
    @Schema(description = "文档标题")
    private String title;
    @Schema(description = "副标题")
    private String subheading;
    @Schema(description = "文档说明")
    private String description;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "跳转链接")
    private String link;
    @Schema(description = "文档内容,markdown")
    private String content;
    @Schema(description = "文档版本")
    private String documentVersion;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除，0：正常，1：已删除")
    private Integer deleted;
}
