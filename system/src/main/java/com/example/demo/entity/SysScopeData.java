package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_scope_data")
@Schema(description = "数据权限规则")
public class SysScopeData extends BaseEntity {
    @Schema(description = "资源编号")
    private String resourceCode;
    @Schema(description = "数据权限名称")
    private String scopeName;
    @Schema(description = "数据权限返回字段")
    private String scopeField;
    @Schema(description = "Mapper 方法全名")
    private String scopeClass;
    @Schema(description = "数据权限字段")
    private String scopeColumn;
    @Schema(description = "数据权限类型")
    private Integer scopeType;
    @Schema(description = "自定义规则值")
    private String scopeValue;
    @Schema(description = "数据权限备注")
    private String remark;
    @Schema(description = "状态")
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
