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
    @Schema(description = "文档类型")
    private String type;
    @Schema(description = "文档编码")
    private String code;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "语言编码")
    private String languageCode;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "副标题")
    private String subheading;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "链接")
    private String link;
    @Schema(description = "文档内容")
    private String content;
    @Schema(description = "文档版本")
    private String documentVersion;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
