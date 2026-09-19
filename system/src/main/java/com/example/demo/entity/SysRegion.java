package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_region")
@Schema(description = "行政区域")
public class SysRegion extends BaseEntity {
    @Schema(description = "区划编号")
    private String code;
    @Schema(description = "父区划编号")
    private String parentCode;
    @Schema(description = "祖区划编号")
    private String ancestors;
    @Schema(description = "区划名称")
    private String name;
    @Schema(description = "省级区划编号")
    private String provinceCode;
    @Schema(description = "省级名称")
    private String provinceName;
    @Schema(description = "市级区划编号")
    private String cityCode;
    @Schema(description = "市级名称")
    private String cityName;
    @Schema(description = "区级区划编号")
    private String districtCode;
    @Schema(description = "区级名称")
    private String districtName;
    @Schema(description = "镇级区划编号")
    private String townCode;
    @Schema(description = "镇级名称")
    private String townName;
    @Schema(description = "村级区划编号")
    private String villageCode;
    @Schema(description = "村级名称")
    private String villageName;
    @Schema(description = "区划层级")
    private Integer regionLevel;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
