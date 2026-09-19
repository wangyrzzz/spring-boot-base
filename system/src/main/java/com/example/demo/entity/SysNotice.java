package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
public class SysNotice extends BaseEntity {
    private String title;
    private Integer category;
    private Date releaseTime;
    private String content;
    private Integer status;
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
