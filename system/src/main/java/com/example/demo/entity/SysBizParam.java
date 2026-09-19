package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_biz_param")
public class SysBizParam extends BaseEntity {
    private String paramName;
    private String paramKey;
    private String paramValue;
    private String remark;
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
