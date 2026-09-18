package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict")
public class SysDict extends BaseEntity {
    private Long parentId;
    private String code;
    private String dictKey;
    private String dictValue;
    private Integer sort;
    private String remark;
    private Integer isSealed;
    @TableLogic
    private Integer deleted;
}
