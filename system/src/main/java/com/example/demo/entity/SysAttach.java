package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_attach")
public class SysAttach extends BaseEntity {
    private String objectKey;
    private String url;
    private String fileName;
    private String extension;
    private String contentType;
    private Long fileSize;
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
