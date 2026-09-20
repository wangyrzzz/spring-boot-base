package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
@Schema(description = "机构部门")
public class SysDept extends BaseEntity {
    @Schema(description = "父部门 ID")
    private Long parentId;
    @Schema(description = "部门名称")
    private String name;
    @Schema(description = "部门编码")
    private String code;
    @Schema(description = "祖先编码路径")
    private String ancestors;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
