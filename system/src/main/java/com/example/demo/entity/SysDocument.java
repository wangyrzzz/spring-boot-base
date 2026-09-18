package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_document")
public class SysDocument extends BaseEntity {
    private String type;
    private String code;
    private Integer sort;
    private String languageCode;
    private String title;
    private String subheading;
    private String description;
    private String icon;
    private String link;
    private String content;
    private String documentVersion;
    @TableLogic
    private Integer deleted;
}
