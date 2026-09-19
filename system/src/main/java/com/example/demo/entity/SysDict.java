package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict")
@Schema(description = "系统字典")
public class SysDict extends BaseEntity {
    @Schema(description = "父级字典 ID")
    private Long parentId;
    @Schema(description = "字典码")
    private String code;
    @Schema(description = "字典键")
    private String dictKey;
    @Schema(description = "字典值")
    private String dictValue;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "字典备注")
    private String remark;
    @Schema(description = "状态")
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
