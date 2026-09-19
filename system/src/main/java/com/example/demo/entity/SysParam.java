package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_param")
@Schema(description = "系统参数")
public class SysParam extends BaseEntity {
    @Schema(description = "参数名")
    private String paramName;
    @Schema(description = "参数键")
    private String paramKey;
    @Schema(description = "参数值")
    private String paramValue;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "状态")
    private Integer status;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
