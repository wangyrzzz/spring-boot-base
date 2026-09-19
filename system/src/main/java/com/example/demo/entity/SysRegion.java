package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_region")
public class SysRegion extends BaseEntity {
    private String code;
    private String parentCode;
    private String ancestors;
    private String name;
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    private String townCode;
    private String townName;
    private String villageCode;
    private String villageName;
    private Integer regionLevel;
    private Integer sort;
    private String remark;
    private Integer status;
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
