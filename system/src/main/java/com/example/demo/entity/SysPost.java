package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_post")
@Schema(description = "岗位")
public class SysPost extends BaseEntity {
    @Schema(description = "岗位类型")
    private Integer category;
    @Schema(description = "岗位编码")
    private String postCode;
    @Schema(description = "岗位名称")
    private String postName;
    @Schema(description = "岗位排序")
    private Integer sort;
    @Schema(description = "岗位描述")
    private String remark;
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
